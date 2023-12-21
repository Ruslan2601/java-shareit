package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.util.UserValidation;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper mapper;

    public User addUser(UserCreate user, BindingResult bindingResult) {
        UserValidation.validation(bindingResult);
        return userStorage.addUser(mapper.toUser(user));
    }

    public User updateUser(UserUpdate user, Integer userId, BindingResult bindingResult) {
        UserValidation.validation(bindingResult);
        return userStorage.updateUser(mapper.toUser(user), userId);
    }

    public User getUser(Integer userId) {
        return userStorage.getUser(userId);
    }

    public User deleteUser(Integer userId) {
        return userStorage.deleteUser(userId);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }
}
