package ru.otus.hw.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.otus.hw.exceptions.EntityNotFoundException;

@ControllerAdvice(assignableTypes = BookController.class)
public class PageExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(EntityNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error-page";
    }
}