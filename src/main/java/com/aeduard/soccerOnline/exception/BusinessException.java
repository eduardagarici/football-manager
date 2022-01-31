package com.aeduard.soccerOnline.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException{

    private String errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;
}
