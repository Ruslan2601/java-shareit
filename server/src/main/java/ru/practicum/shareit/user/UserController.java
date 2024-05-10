package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Validation;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserResponse> getAllUsers() {
        log.info("Получен GET запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResponse addUser(@Valid @RequestBody UserCreate user, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового пользователя");
        Validation.validation(bindingResult);
        return userService.addUser(user);
    }

    @PatchMapping("/{id}")
    public UserResponse updateUser(@Valid @RequestBody UserUpdate user, @PathVariable("id") Integer userId,
                                           BindingResult bindingResult) {
        log.info("Получен Patch запрос на обновление пользователя");
        Validation.validation(bindingResult);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable("id") Integer userId) {
        log.info("Получен GET запрос на получение пользователя");
        return userService.getUser(userId);
    }

    @DeleteMapping("/{id}")
    public UserResponse deleteUser(@PathVariable("id") Integer userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);
        return userService.deleteUser(userId);
    }
}
