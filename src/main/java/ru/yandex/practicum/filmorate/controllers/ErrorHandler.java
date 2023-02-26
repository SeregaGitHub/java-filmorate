package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNegativeFilmValidation(ValidationException exception) {
        return Map.of(
                "errorValidation - ", "Валидация фильма не пройдена.",
                "errorMessage    - ", exception.getMessage()
                );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleNegativeFilmAnnotationsValidation(MethodArgumentNotValidException exception) {
        return Map.of(
                "errorMessage    - ", exception.getMessage(),
                "errorValidation - ", "Валидация фильма не пройдена."
                );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFilmNotFound(FilmNotFoundException exception) {
        return Map.of(
                "errorMessage - ", exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(UserNotFoundException exception) {
        return Map.of(
                "errorMessage - ", exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleIncorrectFilmControllerParameter(IncorrectParameterException exception) {
        return Map.of(
                "errorMessage - ", exception.getMessage()
        );
    }
}
