package com.cinar.textile.service;

import com.cinar.textile.dto.UserDto;
import com.cinar.textile.dto.request.CreateUserRequest;
import com.cinar.textile.dto.request.SignInRequest;
import com.cinar.textile.dto.request.SignUpRequest;
import com.cinar.textile.dto.response.JwtAuthenticationResponse;
import com.cinar.textile.exception.InvalidInputException;
import com.cinar.textile.exception.UserNotFoundException;
import com.cinar.textile.model.Role;
import com.cinar.textile.model.Token;
import com.cinar.textile.model.TokenType;
import com.cinar.textile.model.User;
import com.cinar.textile.repository.TokenRepository;
import com.cinar.textile.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService, TokenRepository tokenRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.modelMapper = modelMapper;
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return !Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
    public User  signUp(SignUpRequest request) {

        return new User(
                request.getFirstName(), request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhoneNumber(),
                List.of(Role.valueOf("ROLE_ADMIN")));
    }
    public User signUpFromRequest(SignUpRequest request) {

        if (patternMatches(request.getEmail(), "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            throw new InvalidInputException("Email is not valid");
        }
        if (patternMatches(request.getPhoneNumber(), "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}")) {
            throw new InvalidInputException("Phone number is not valid");
        }
        return signUp(request);
    }
    public JwtAuthenticationResponse signUpNewUser(SignUpRequest signUpRequest) {
        User user = signUpFromRequest(signUpRequest);
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        savedUserToken(savedUser, jwtToken);
        var userDto = modelMapper.map(savedUser, UserDto.class);

        return JwtAuthenticationResponse.builder().user(userDto).accessToken(jwtToken).refreshToken(refreshToken).build();
    }


    private void  savedUserToken(User user, String jwtToken) {
        var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).revoked(false).expired(false).build();
        tokenRepository.save(token);}
    private void  revokeAllUserTokens(User user) {
        var validUserToken = tokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserToken.isEmpty())
            return;
        validUserToken.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);

    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),
                signInRequest.getPassword()));

        var user = userRepository.findUserByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with this email"+signInRequest.getEmail()));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        savedUserToken(user, jwtToken);
        var userDto = modelMapper.map(user, UserDto.class);

        return JwtAuthenticationResponse.builder().user(userDto).accessToken(jwtToken).refreshToken(refreshToken).build();
    }
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken ;
        String userEmail ;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;

        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUser(refreshToken);

        if (userEmail != null ) {
            var user = this.userRepository.findUserByEmail(userEmail).orElseThrow();
            if (jwtService.isValidateToken(refreshToken, user)) {
                var accessToken =jwtService.generateToken(user);
                revokeAllUserTokens(user);
                savedUserToken(user, accessToken);
                var authResponse = JwtAuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
                new ObjectMapper().writeValue(response.getOutputStream(),authResponse);
            }
        }

    }

}
