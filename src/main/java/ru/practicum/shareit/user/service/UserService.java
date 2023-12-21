package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.util.UserValidation;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper mapper;

    public UserResponse addUser(UserCreate user, BindingResult bindingResult) {
        UserValidation.validation(bindingResult);
        return mapper.toUserResponse(userStorage.addUser(mapper.toUser(user)));
    }

    public UserResponse updateUser(UserUpdate user, Integer userId, BindingResult bindingResult) {
        UserValidation.validation(bindingResult);
        return mapper.toUserResponse(userStorage.updateUser(mapper.toUser(user), userId));
    }

    public UserResponse getUser(Integer userId) {
        return mapper.toUserResponse(userStorage.getUser(userId));
    }

    public UserResponse deleteUser(Integer userId) {
        return mapper.toUserResponse(userStorage.deleteUser(userId));
    }

    public Collection<UserResponse> getAllUsers() {
        return userStorage.getAllUsers().stream().map(mapper::toUserResponse).collect(Collectors.toList());
    }
}
