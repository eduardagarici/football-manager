package com.aeduard.soccerOnline.controller;


import com.aeduard.soccerOnline.SoccerOnlineApp;
import com.aeduard.soccerOnline.config.H2DataSourceConfig;
import com.aeduard.soccerOnline.exception.ErrorResponse;
import com.aeduard.soccerOnline.model.User;
import com.aeduard.soccerOnline.model.UserRole;
import com.aeduard.soccerOnline.repository.UserRepository;
import com.aeduard.soccerOnline.representation.request.RegisterUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.aeduard.soccerOnline.Util.extractContentAsType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {SoccerOnlineApp.class, H2DataSourceConfig.class})
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerItTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static final String USER_ALREADY_EXISTS = "This email is already used";
    private static final String USER_EMAIL = "aeduard@email.com";
    private static final String USER_PASSWORD = "password";
    private static final String EMAIL_INVALID = "email invalid";


    @Before
    public void setup() {
        userRepository.deleteAll();
    }


    @Test
    public void registerUserShouldWorkSuccessfullyWhenInputIsCorrect() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(USER_EMAIL, USER_PASSWORD);

        MvcResult mvcResult = mvc
                .perform(post("/api/v1/users/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualEmail = mvcResult.getResponse().getContentAsString();
        assertThat(actualEmail).isEqualTo(USER_EMAIL);
    }

    @Test
    public void registerUserShouldThrowBadRequestIfUserAlreadyExists() throws Exception {
        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);
        user.setRole(UserRole.USER);
        userRepository.save(user);

        RegisterUserRequest registerUserRequest = new RegisterUserRequest(USER_EMAIL, USER_PASSWORD);

        MvcResult mvcResult = mvc
                .perform(post("/api/v1/users/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = extractContentAsType(ErrorResponse.class, mvcResult, objectMapper);
        assertThat(errorResponse.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(errorResponse.getErrorMessage()).isEqualTo(USER_ALREADY_EXISTS);
    }

    @Test
    public void registerUserShouldThrowBadRequestIfEmailIsNotValid() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest("not_valid_email", USER_PASSWORD);

        MvcResult mvcResult = mvc
                .perform(post("/api/v1/users/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse errorResponse = extractContentAsType(ErrorResponse.class, mvcResult, objectMapper);
        assertThat(errorResponse.getErrorCode()).isEqualTo(HttpStatus.BAD_REQUEST.toString());
        assertThat(errorResponse.getErrorMessage()).isEqualTo(EMAIL_INVALID);
    }

}
