package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserService service;

    private final UserResponse userResponse = new UserResponse(1, "vfl@mai.ru", "Ruslan");

    @Test
    void getUser_whenUserExist() {
        User user = new User();

        when(repository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        assertEquals(userResponse, service.getUser(1));
    }

    @Test
    void getUser_whenUserNotFound() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getUser(1));
    }

    @Test
    void getAllUsers_whenUsersExist() {
        User user = new User();

        when(repository.findAll()).thenReturn(List.of(user, user, user));
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        List<UserResponse> usersResponse = new ArrayList<>(service.getAllUsers());

        assertEquals(3, usersResponse.size());
        assertEquals(userResponse, usersResponse.get(0));
        assertEquals(userResponse, usersResponse.get(1));
        assertEquals(userResponse, usersResponse.get(2));
    }

    @Test
    void getAllUsers_whenUsersNotFound() {
        when(repository.findAll()).thenReturn(List.of());

        assertEquals(0, service.getAllUsers().size());
    }

    @Test
    void addUser() {
        User user = new User();
        UserCreate userCreate = new UserCreate();
        userCreate.setEmail("mai@sdf.ru");
        userCreate.setId(1);
        userCreate.setName("Ruslan");

        when(mapper.toUser(userCreate)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        assertEquals(userResponse, service.addUser(userCreate));
    }

    @Test
    void addUserSameEmail() {
        User user = new User();
        user.setEmail("mai@sdf.ru");
        UserCreate userCreate = new UserCreate();
        userCreate.setEmail("mai@sdf.ru");
        userCreate.setId(1);
        userCreate.setName("Ruslan");

        when(mapper.toUser(userCreate)).thenReturn(user);
        when(repository.save(user)).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(ConflictException.class, () -> service.addUser(userCreate));
    }


    @Test
    void updateUser_whenUserNotFound() {
        UserUpdate userCreate = new UserUpdate();
        userCreate.setEmail("mai@sdf.ru");
        userCreate.setName("Ruslan");

        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.updateUser(userCreate, 1));
    }

    @Test
    void updateUser_whenUserUpdateName() {
        UserUpdate userCreate = new UserUpdate();
        userCreate.setName("Ruslan");

        User userOld = new User();
        userOld.setName("name old");
        userOld.setEmail("email old");

        User user = new User();
        user.setName("name");

        userResponse.setName("name");
        userResponse.setEmail("email old");

        when(repository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toUser(any(UserResponse.class))).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        assertEquals(userResponse, service.updateUser(userCreate, 1));
    }

    @Test
    void updateUser_whenUserUpdateEmail() {
        UserUpdate userCreate = new UserUpdate();
        userCreate.setEmail("Ruslan@m.ru");

        User userOld = new User();
        userOld.setName("name old");
        userOld.setEmail("email old");

        User user = new User();
        user.setName("name");

        userResponse.setName("name");
        userResponse.setEmail("email old");

        when(repository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toUser(any(UserResponse.class))).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        assertEquals(userResponse, service.updateUser(userCreate, 1));
    }

    @Test
    void updateUser_whenUserUpdateSameEmail() {
        UserUpdate userCreate = new UserUpdate();
        userCreate.setEmail("Ruslan@m.ru");

        User userOld = new User();
        userOld.setName("name old");
        userOld.setEmail("Ruslan@m.ru");

        User user = new User();
        user.setName("name");

        userResponse.setName("name");
        userResponse.setEmail("Ruslan@m.ru");

        when(repository.findByEmailAndIdNot(anyString(),anyInt())).thenReturn(user);
        when(repository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toUser(any(UserResponse.class))).thenReturn(user);
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        assertThrows(ConflictException.class, () -> service.updateUser(userCreate, 1));
    }

    @Test
    void deleteUser_whenUserNotFound() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.deleteUser(1));
    }

    @Test
    void deleteUser_whenUserExist() {
        User user = new User();

        when(repository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toUser(userResponse)).thenReturn(user);
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        assertEquals(userResponse, service.deleteUser(1));
    }
}