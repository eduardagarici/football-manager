package com.aeduard.soccerOnline.service;

import com.aeduard.soccerOnline.exception.BusinessException;
import com.aeduard.soccerOnline.model.UserRole;
import com.aeduard.soccerOnline.model.Team;
import com.aeduard.soccerOnline.model.User;
import com.aeduard.soccerOnline.repository.UserRepository;
import com.aeduard.soccerOnline.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private static final String USER_ALREADY_EXISTS = "This email is already used";

    private final AuthenticationManager authenticationManager;
    private final DBUserDetailsService dbUserDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String authenticateUser(String email, String password){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        final UserDetails userDetails = dbUserDetailsService.loadUserByUsername(email);
        final String jwt = jwtUtil.generateToken(userDetails);
        return jwt;
    }

    public String registerUser(String email, String password){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            log.error("User with email {} already exists", email);
            throw new BusinessException(HttpStatus.BAD_REQUEST.toString(), USER_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.USER);

        //setup new item for newly registerd user
        log.debug("Setup new team for player with email: {}", email);

        Team team = new Team();
        team.setUser(user);
        user.setTeam(team);
        user = userRepository.save(user);

        return user.getEmail();
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
