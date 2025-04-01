package com.example.calorator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(NoResourceFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Page Not Found");
        model.addAttribute("message", "The page you're looking for doesn't exist.");
        return "error/404";
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Page Not Found");
        model.addAttribute("message", "The page you're looking for doesn't exist.");
        return "error/404";
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("error", "Access Denied");
        model.addAttribute("message", "You don't have permission to access this resource.");
        return "error/403";
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Server Error");
        model.addAttribute("message", "An unexpected error occurred.");
        return "error/500";
    }
}