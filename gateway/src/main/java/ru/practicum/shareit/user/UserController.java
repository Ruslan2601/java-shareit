package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.util.Validation;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен GET запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserCreate user, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового пользователя");
        Validation.validation(bindingResult);
        return userClient.addUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdate user, @PathVariable("id") Integer userId,
                                           BindingResult bindingResult) {
        log.info("Получен Patch запрос на обновление пользователя");
        Validation.validation(bindingResult);
        return userClient.updateUser(user, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Integer userId) {
        log.info("Получен GET запрос на получение пользователя");
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Integer userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);
        return userClient.deleteUser(userId);
    }
}
