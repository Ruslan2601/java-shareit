package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    protected final Map<Integer, User> userMap = new HashMap<>();
    private int id = 0;

    @Override
    public User addUser(User user) {
        checkEmail(user, user.getId());
        user.setId(++id);
        userMap.put(user.getId(), user);
        log.info("Добавлен пользователь");
        return user;
    }

    @Override
    public User updateUser(User user, Integer userId) {
        if (userMap.containsKey(userId)) {
            User thisUser = userMap.get(userId);
            if (user.getEmail() != null) {
                checkEmail(user, userId);
                thisUser.setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                thisUser.setName(user.getName());
            }
            thisUser.setId(userId);
            userMap.put(userId, thisUser);
            log.info("Обновлены данные по пользователю");
            return thisUser;
        } else {
            throw new ObjectNotFoundException("Пользователя с такими id нет");
        }
    }

    @Override
    public User getUser(Integer userId) {
        if (userMap.containsKey(userId)) {
            log.info("Отображен пользователь с id = {}", userId);
            return userMap.get(userId);
        } else {
            throw new ObjectNotFoundException("Пользователя с такими id нет");
        }

    }

    @Override
    public User deleteUser(Integer userId) {
        User user = getUser(userId);
        userMap.remove(userId);
        log.info("Пользователь с id = {} удален", userId);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Отображен список всех пользователей");
        return userMap.values();
    }

    private void checkEmail(User user, Integer userId) {
        if (userMap.values().stream().filter(x -> x.getId() != userId)
                .anyMatch(x -> x.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Два пользователя не могут " +
                    "иметь одинаковый адрес электронной почты");
        }
    }
}
