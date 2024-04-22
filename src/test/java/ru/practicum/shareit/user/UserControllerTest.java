package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private final UserResponse userResponse = new UserResponse(1, "vfl@mai.ru", "Ruslan");


    @SneakyThrows
    @Test
    void create_shouldCreateUser() {
        UserCreate userCreate = new UserCreate();
        userCreate.setEmail("mai@sdf.ru");
        userCreate.setId(1);
        userCreate.setName("Ruslan");
        when(userService.addUser(any(UserCreate.class)))
                .thenReturn(userResponse);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userCreate))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @SneakyThrows
    @Test
    void getUser_shouldFindUserById() {
        when(userService.getUser(any(Integer.class)))
                .thenReturn(userResponse);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @SneakyThrows
    @Test
    void deleteUser_shouldDeleteUserById() {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void updateUser_shouldUpdateUserData() {
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setEmail("mai@sdf.ru");
        userUpdate.setName("Ruslan");
        when(userService.updateUser(any(), any()))
                .thenReturn(userResponse);

        mvc.perform(patch("/users/1")
                .content(mapper.writeValueAsString(userUpdate))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @SneakyThrows
    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userService.getAllUsers())
                .thenReturn(List.of(userResponse));
        mvc.perform(get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(userResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].email", is(userResponse.getEmail())))
                .andExpect(jsonPath("$.[0].name", is(userResponse.getName())));
    }

    @SneakyThrows
    @Test
    void create_EmptyField() {
        UserCreate userCreate = new UserCreate();
        userCreate.setEmail("mai@sdf.ru");
        when(userService.addUser(any(UserCreate.class)))
                .thenReturn(userResponse);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userCreate))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("name - поле name не может быть пустым;"));
    }

    @SneakyThrows
    @Test
    void updateUser_FailEmail() {
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setEmail("mai@sdf.ru");
        userUpdate.setName("Ruslan");
        when(userService.updateUser(any(), any()))
                .thenThrow(new ConflictException("Два пользователя не могут иметь одинаковый адрес электронной почты"));

        mvc.perform(patch("/users/1")
                .content(mapper.writeValueAsString(userUpdate))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("Два пользователя не могут иметь одинаковый адрес электронной почты"));
    }
}