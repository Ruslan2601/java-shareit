package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.util.UserValidation;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserMapper mapper;
    private final UserRepository userRepository;

    @Transactional
    public UserResponse addUser(UserCreate user, BindingResult bindingResult) {
        UserValidation.validation(bindingResult);
        try {
            User user1 = userRepository.save(mapper.toUser(user));
            log.info("Добавлен пользователь");
            return mapper.toUserResponse(user1);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Два пользователя не могут " +
                    "иметь одинаковый адрес электронной почты");
        }
    }

    @Transactional
    public UserResponse updateUser(UserUpdate user, Integer userId, BindingResult bindingResult) {
        UserValidation.validation(bindingResult);

        User thisUser = mapper.toUser(getUser(userId));
        if (user.getEmail() != null) {
            checkEmail(user.getEmail(), userId);
            thisUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            thisUser.setName(user.getName());
        }
        userRepository.save(thisUser);
        log.info("Обновлены данные по пользователю");
        return mapper.toUserResponse(thisUser);
    }

    public UserResponse getUser(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        log.info("Отображен пользователь с id = {}", userId);
        return mapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse deleteUser(Integer userId) {
        User thisUser = mapper.toUser(getUser(userId));
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удален", userId);
        return mapper.toUserResponse(thisUser);
    }

    public Collection<UserResponse> getAllUsers() {
        log.info("Отображен список всех пользователей");
        return userRepository.findAll().stream().map(mapper::toUserResponse).collect(Collectors.toList());
    }

    private void checkEmail(String email, Integer userId) {
        if (userRepository.findByEmailAndIdNot(email, userId) != null) {
            throw new ConflictException("Два пользователя не могут " +
                    "иметь одинаковый адрес электронной почты");
        }
    }
}
