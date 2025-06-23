package org.example.userauthservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.models.Token;
import org.example.userauthservice.models.User;

public interface IAuthService {
    public User signup(String name, String email, String password, String phoneNumber) throws JsonProcessingException;

    public Token login(String email, String password);

    User validateToken(String token);
}
