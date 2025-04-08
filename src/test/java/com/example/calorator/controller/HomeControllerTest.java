package com.example.calorator.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {
    @InjectMocks
    private HomeController homeController;

    @Test
    void home_ShouldReturnIndexView() {

        String viewName = homeController.home();

        assertEquals("index", viewName, "Home method should return 'index' view name");
    }
}
