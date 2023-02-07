package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static int userId = 0;
    private final HashMap<Integer, User> userHashMap = new HashMap<>();

    @GetMapping
    public List<User> getUsersList() {
        return new ArrayList<>(userHashMap.values());
    }

    @PostMapping
    public User createNewUser(@Valid @RequestBody User user) {
        UserValidator.userNameValidation(user);
        user.setId(++userId);
        userHashMap.put(userId, user);
        log.info("Пользователь " + user.getName() + " добавлен.");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (userHashMap.containsKey(user.getId())) {
            UserValidator.userNameValidation(user);
            userHashMap.put(user.getId(), user);
            log.info("Пользователь " + user.getName() + " обновлён.");
        } else {
            log.info("Валидация пользователя " + user.getName() + " не пройдена: " +
                    "обновление отсутствующего пользователя - невозможно.");
            throw new ValidationException("Пользователь " + user.getName() + " отсутствует в списке пользоватлей.");
        }
        return user;
    }

    private static void setUserId(int userId) {
        UserController.userId = userId;
    }
}
