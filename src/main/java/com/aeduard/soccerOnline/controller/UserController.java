package com.aeduard.soccerOnline.controller;

import com.aeduard.soccerOnline.representation.request.AuthRequest;
import com.aeduard.soccerOnline.representation.request.RegisterUserRequest;
import com.aeduard.soccerOnline.service.UserService;
import com.aeduard.soccerOnline.util.Paths;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@AllArgsConstructor
@RequestMapping(Paths.USERS)
public class UserController {

    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> createAuthenticationToken(@Valid @RequestBody AuthRequest authRequest) throws Exception{
        return ResponseEntity.ok().body(userService.authenticateUser(authRequest.getEmail(), authRequest.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(registerUserRequest.getEmail(), registerUserRequest.getPassword()));
    }
}
