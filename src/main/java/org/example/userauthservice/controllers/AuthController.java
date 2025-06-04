package org.example.userauthservice.controllers;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice.dtos.LoginRequestDto;
import org.example.userauthservice.dtos.SignUpRequestDto;
import org.example.userauthservice.dtos.UserDto;
import org.example.userauthservice.models.User;
import org.example.userauthservice.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        User user = authService.signup(signUpRequestDto.getName(), signUpRequestDto.getEmail(), signUpRequestDto.getPassword(), signUpRequestDto.getPhoneNumber());
        return from(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
         Pair<User, String> userWithToken = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
         UserDto userDto = from(userWithToken.a);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    private UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
