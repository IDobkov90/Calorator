package com.example.calorator.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
    private static final String MESSAGE_ATTRIBUTE = "message";

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMsg = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        logger.debug("Error handling - Status: {}", status);
        logger.debug("Error message: {}", errorMsg);
        logger.debug("Exception: {}", exception != null ? exception.toString() : "null");

        model.addAttribute("status", status != null ? status : 500);
        model.addAttribute("error", errorMsg != null ? errorMsg : "Unknown Error");

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute(MESSAGE_ATTRIBUTE, "The page you're looking for doesn't exist.");
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute(MESSAGE_ATTRIBUTE, "You don't have permission to access this resource.");
                return "error/403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute(MESSAGE_ATTRIBUTE, "Something went wrong on our end. Please try again later.");
                return "error/500";
            }
        }

        model.addAttribute(MESSAGE_ATTRIBUTE, "An unexpected error occurred.");
        return "error/error";
    }
}