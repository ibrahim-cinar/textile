package com.cinar.textile.service;
import com.cinar.textile.model.Token;
import com.cinar.textile.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;

    }


    public boolean isTokenExpired(String tokenId) {
        return tokenRepository.findByTokenId(tokenId).stream().anyMatch(Token::isExpired);

    }
    public boolean isTokenRevoke(String tokenId) {
        return tokenRepository.findByTokenId(tokenId).stream().anyMatch(Token::isRevoked);

    }
    public void revokeToken(String tokenId) {
        var token = tokenRepository.findByTokenId(tokenId).orElseThrow(() -> new RuntimeException("token not found"));
        if(isTokenRevoke(tokenId) && isTokenExpired(tokenId)) {
            tokenRepository.delete(token);
        }
    }

}