package org.example.userauthservice.services;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.dtos.SignUpRequestDto;
import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.exceptions.PasswordMismatchException;
import org.example.userauthservice.exceptions.UserAlreadyExistException;
import org.example.userauthservice.exceptions.UserNotPresentException;
import org.example.userauthservice.models.User;
import org.example.userauthservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    public User signup(String name, String email, String password, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmailEquals(email);

        if (userOptional.isPresent()) {
            throw new UserAlreadyExistException("User with email " + email + " already exists. Please try logging in.");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // Use Bcrypt here
        user.setPhoneNumber(phoneNumber);
        return userRepo.save(user);
    }

    public Pair<User, String> login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmailEquals(email);

        if (userOptional.isEmpty()) {
            throw new UserNotPresentException("User with email " + email + " does not exist. Please sign up");
        }

        String storedPassword = userOptional.get().getPassword();

        if (!password.equals(storedPassword)) {
            throw new PasswordMismatchException("Passwords do not match. Please try again");
        }

        // TODO: Generate JWT

        return new Pair<>(userOptional.get(), "");
    }

}
