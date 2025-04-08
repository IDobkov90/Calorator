package com.example.calorator.integration.controller;

import com.example.calorator.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomErrorControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testuser")
    void handleError_NotFound_ShouldReturnNotFoundStatus() throws Exception {

        mockMvc.perform(get("/non-existent-url"))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void handleError_Forbidden_ShouldReturnForbiddenStatus() throws Exception {

        mockMvc.perform(get("/admin/dashboard"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void handleError_DirectErrorEndpoint() throws Exception {

        mockMvc.perform(get("/error"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "testuser")
    void handleError_CustomErrorAttributes() throws Exception {

        mockMvc.perform(get("/error")
                        .requestAttr("javax.servlet.error.status_code", 500)
                        .requestAttr("javax.servlet.error.message", "Test Error"))
                .andDo(print())
                .andExpect(status().isOk());

    }
}