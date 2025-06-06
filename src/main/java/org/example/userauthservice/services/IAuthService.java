package org.example.userauthservice.services;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.models.Token;
import org.example.userauthservice.models.User;

public interface IAuthService {
    public User signup(String name, String email, String password, String phoneNumber);

    public Token login(String email, String password);
}
