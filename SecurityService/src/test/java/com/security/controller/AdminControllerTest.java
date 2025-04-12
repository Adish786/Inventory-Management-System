package com.security.controller;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private MockMvc mockMvc;

    @Test(expected = NullPointerException.class)
    public void greeting_ShouldReturnGreetingMessage() throws Exception {
        mockMvc.perform(get("/api/v1/admin/greet"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hi Admin !"));
    }
}