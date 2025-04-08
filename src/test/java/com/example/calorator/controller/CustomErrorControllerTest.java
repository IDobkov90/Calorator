package com.example.calorator.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private CustomErrorController errorController;

    @Test
    void handleError_NotFound_ShouldReturn404View() {

        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.NOT_FOUND.value());
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Page not found");

        String viewName = errorController.handleError(request, model);

        assertEquals("error/404", viewName);
        verify(model).addAttribute("status", HttpStatus.NOT_FOUND.value());
        verify(model).addAttribute("error", "Page not found");
        verify(model).addAttribute("message", "The page you're looking for doesn't exist.");
    }

    @Test
    void handleError_Forbidden_ShouldReturn403View() {

        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.FORBIDDEN.value());
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Access denied");

        String viewName = errorController.handleError(request, model);

        assertEquals("error/403", viewName);
        verify(model).addAttribute("status", HttpStatus.FORBIDDEN.value());
        verify(model).addAttribute("error", "Access denied");
        verify(model).addAttribute("message", "You don't have permission to access this resource.");
    }

    @Test
    void handleError_InternalServerError_ShouldReturn500View() {

        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Server error");

        String viewName = errorController.handleError(request, model);

        assertEquals("error/500", viewName);
        verify(model).addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        verify(model).addAttribute("error", "Server error");
        verify(model).addAttribute("message", "Something went wrong on our end. Please try again later.");
    }

    @Test
    void handleError_UnknownError_ShouldReturnGenericErrorView() {

        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(HttpStatus.BAD_GATEWAY.value());
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Bad gateway");

        String viewName = errorController.handleError(request, model);

        assertEquals("error/error", viewName);
        verify(model).addAttribute("status", HttpStatus.BAD_GATEWAY.value());
        verify(model).addAttribute("error", "Bad gateway");
        verify(model).addAttribute("message", "An unexpected error occurred.");
    }

    @Test
    void handleError_NullStatus_ShouldReturnGenericErrorView() {

        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn(null);

        String viewName = errorController.handleError(request, model);

        assertEquals("error/error", viewName);
        verify(model).addAttribute("status", 500);
        verify(model).addAttribute("error", "Unknown Error");
        verify(model).addAttribute("message", "An unexpected error occurred.");
    }
}
