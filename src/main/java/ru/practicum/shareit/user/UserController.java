package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreate;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        log.info("Получен GET запрос на получение всех пользователей");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody UserCreate user, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового пользователя");
        return ResponseEntity.ok(userService.addUser(user, bindingResult));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@Valid @RequestBody UserUpdate user, @PathVariable("id") Integer userId,
                                           BindingResult bindingResult) {
        log.info("Получен Patch запрос на обновление пользователя");
        return ResponseEntity.ok(userService.updateUser(user, userId, bindingResult));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Integer userId) {
        log.info("Получен GET запрос на получение пользователя");
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") Integer userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
