package com.post.service.app.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Locale;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler  {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleCustomError(RuntimeException ex) {
        log.error("Enter to handleCustomError()");
        ex.printStackTrace();

        ErrorResponse error = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .errorCode(ex.getMessage())
                .message(messageSource.getMessage(ex.getMessage(), null, Locale.US))
                .description("").build();

        log.error("An error of the type + " + CustomException.class.getName() + " ocurred!");
        log.info(error.toString());
        return error;
    }
}
