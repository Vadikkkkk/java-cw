package com.example.task_java.controller_test;

import com.example.task_java.controller.UserController;
import com.example.task_java.dto.UserRegistrationRequest;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.User;
import com.example.task_java.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testUser = User.builder()
                .userId(1L)
                .userName("Vasilev Vadim")
                .email("gta899670@gmail.com")
                .build();
    }

    @Test
    void registerUser_ReturnsCreatedUser() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("Vasilev Vadim", "gta899670@gmail.com");

        when(userService.registerUser(Mockito.any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1"))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.userName", is("Vasilev Vadim")))
                .andExpect(jsonPath("$.email", is("gta899670@gmail.com")));
    }

    @Test
    void loginUser_ReturnsUser() throws Exception {
        when(userService.loginUser("gta899670@gmail.com")).thenReturn(testUser);

        mockMvc.perform(get("/users/login")
                        .param("email", "gta899670@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.userName", is("Vasilev Vadim")));
    }

    @Test
    void getAllUsers_ReturnsList() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("gta899670@gmail.com")));
    }

    @Test
    void getUserById_ReturnsUser() throws Exception {
        when(userService.findUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @Test
    void deleteUser_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/delete/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }

    // Негативные тесты

    @Test
    void getUserById_UserNotFound_Returns404() throws Exception {
        when(userService.findUserById(99L))
                .thenThrow(new RecordNotFoundException("User 99 not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User 99 not found"));
    }

    @Test
    void loginUser_UserNotFound_Returns404() throws Exception {
        when(userService.loginUser("nonexistent@example.com"))
                .thenThrow(new RecordNotFoundException("User not found"));

        mockMvc.perform(get("/users/login")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User not found"));
    }

    @Test
    void registerUser_NullBody_Returns400() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")) // пустое тело запроса
                .andExpect(status().isBadRequest());
    }

}
