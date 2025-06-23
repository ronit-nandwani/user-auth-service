package org.example.userauthservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.userauthservice.dtos.SendEmailEventDto;
import org.example.userauthservice.dtos.SignUpRequestDto;
import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.exceptions.PasswordMismatchException;
import org.example.userauthservice.exceptions.UserAlreadyExistException;
import org.example.userauthservice.exceptions.UserNotPresentException;
import org.example.userauthservice.models.Token;
import org.example.userauthservice.models.User;
import org.example.userauthservice.repos.TokenRepository;
import org.example.userauthservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public User signup(String name, String email, String password, String phoneNumber) throws JsonProcessingException {
        Optional<User> userOptional = userRepo.findByEmailEquals(email);

        if (userOptional.isPresent()) {
            throw new UserAlreadyExistException("User with email " + email + " already exists. Please try logging in.");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setPhoneNumber(phoneNumber);
        user = userRepo.save(user);

        //Produce a message to Kafka topic to send a welcome email.
        SendEmailEventDto emailEventDto = new SendEmailEventDto();
        emailEventDto.setSubject("Welcome to Scaler!");
        emailEventDto.setBody("Welcome aboard");
        emailEventDto.setToEmail(email);

        kafkaTemplate.send(
                "sendEmailEvent",
                objectMapper.writeValueAsString(emailEventDto)
        );

        return user;

    }

    public Token login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmailEquals(email);

        if (userOptional.isEmpty()) {
            throw new UserNotPresentException("User with email " + email + " does not exist. Please sign up");
        }

        if(!bCryptPasswordEncoder.matches(password, userOptional.get().getPassword())) {
            throw new PasswordMismatchException("Please type correct password");
        }

        //Create a token and store it in Tokens table.
        Token token = new Token();
        token.setUser(userOptional.get());
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date dateAfter30Days = calendar.getTime();

        token.setExpiresAt(dateAfter30Days);

        return tokenRepository.save(token);
    }

    @Override
    public User validateToken(String tokenValue) {

        Optional<Token> optionalToken = tokenRepository.
                findByValueAndExpiresAtAfter(tokenValue, new Date());

        if (optionalToken.isEmpty()) {
            //Token is not valid or expired
            return null;
        }

        return optionalToken.get().getUser();
    }

}
