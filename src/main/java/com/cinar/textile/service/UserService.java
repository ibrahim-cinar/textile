package com.cinar.textile.service;

import com.cinar.textile.dto.UserDto;
import com.cinar.textile.dto.request.ChangePasswordRequest;
import com.cinar.textile.dto.request.CreateUserRequest;
import com.cinar.textile.dto.response.UserResponse;
import com.cinar.textile.exception.InvalidInputException;
import com.cinar.textile.exception.PasswordNotMatchException;
import com.cinar.textile.exception.UserNotFoundException;
import com.cinar.textile.exception.WrongPasswordException;
import com.cinar.textile.model.User;
import com.cinar.textile.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public UserResponse getAllUsers(int pageNo,int pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<User> users = userRepository.findAll(pageable);
        List<User> userList = users.getContent();
        List<UserDto> content = userList.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());

        UserResponse userResponse = new UserResponse();

        userResponse.setContent(content);
        userResponse.setPageNo(users.getNumber());
        userResponse.setPageSize(users.getSize());
        userResponse.setTotalElements(users.getTotalElements());
        userResponse.setTotalPages(users.getTotalPages());
        userResponse.setLast(users.isLast());

        return userResponse;
    }
   /////////////////////////// find user with email
    protected Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
    public User findUserByEmail(String email) {
        return findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
    ////////////////////////////////////////////////

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(username);
        return user.orElseThrow(EntityNotFoundException::new);
    }
    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return !Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
    public User createUser(CreateUserRequest request) {

        return new User(
                request.getFirstName(), request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhoneNumber(),
                request.getAuthorities());
    }
    public User createUserFromRequest(CreateUserRequest request) {

        if (patternMatches(request.getEmail(), "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            throw new InvalidInputException("Email is not valid");
        }
        if (patternMatches(request.getPhoneNumber(), "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}")) {
            throw new InvalidInputException("Phone number is not valid");
        }
        return createUser(request);
    }
    public User createNewUser(CreateUserRequest createUserRequest) {
        User user = createUserFromRequest(createUserRequest);
        return userRepository.save(user);
    }
    public void changePassword(ChangePasswordRequest changePasswordRequest, Principal connectedUser) {

        var user = (User)((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if(!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException("Wrong password ");
        }
        if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new PasswordNotMatchException("Password not match");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}

