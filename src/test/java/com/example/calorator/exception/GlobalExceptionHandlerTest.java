package com.example.calorator.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleNoResourceFoundException_ShouldReturn404View() {

        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/non-existent");

        String viewName = globalExceptionHandler.handleNoResourceFoundException(exception, model);

        assertEquals("error/404", viewName);
        verify(model).addAttribute("status", 404);
        verify(model).addAttribute("error", "Page Not Found");
        verify(model).addAttribute("message", "The page you're looking for doesn't exist.");
    }

    @Test
    void handleNoHandlerFoundException_ShouldReturn404View() {

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();

        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/non-existent", headers);

        String viewName = globalExceptionHandler.handleNoHandlerFoundException(exception, model);

        assertEquals("error/404", viewName);
        verify(model).addAttribute("status", 404);
        verify(model).addAttribute("error", "Page Not Found");
        verify(model).addAttribute("message", "The page you're looking for doesn't exist.");
    }

    @Test
    void handleAccessDeniedException_ShouldReturn403View() {

        AccessDeniedException exception = new AccessDeniedException("Access denied");

        String viewName = globalExceptionHandler.handleAccessDeniedException(exception, model);

        assertEquals("error/403", viewName);
        verify(model).addAttribute("status", 403);
        verify(model).addAttribute("error", "Access Denied");
        verify(model).addAttribute("message", "You don't have permission to access this resource.");
    }

    @Test
    void handleGenericException_ShouldReturn500View() {

        Exception exception = new RuntimeException("Something went wrong");

        String viewName = globalExceptionHandler.handleGenericException(exception, model);

        assertEquals("error/500", viewName);
        verify(model).addAttribute("status", 500);
        verify(model).addAttribute("error", "Server Error");
        verify(model).addAttribute("message", "An unexpected error occurred.");
    }

    @Test
    void handleIllegalArgumentException_ShouldReturn500View() {

        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        String viewName = globalExceptionHandler.handleGenericException(exception, model);

        assertEquals("error/500", viewName);
        verify(model).addAttribute("status", 500);
        verify(model).addAttribute("error", "Server Error");
        verify(model).addAttribute("message", "An unexpected error occurred.");
    }

    @Test
    void handleNullPointerException_ShouldReturn500View() {

        NullPointerException exception = new NullPointerException("Null reference");

        String viewName = globalExceptionHandler.handleGenericException(exception, model);

        assertEquals("error/500", viewName);
        verify(model).addAttribute("status", 500);
        verify(model).addAttribute("error", "Server Error");
        verify(model).addAttribute("message", "An unexpected error occurred.");
    }
}
