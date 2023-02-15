package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.util.ControllerUtil;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;
    private final ControllerUtil controllerUtil;
    @Autowired
    public UserController(UserService userService, UserValidator userValidator, ControllerUtil controllerUtil) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.controllerUtil = controllerUtil;
    }

    @GetMapping
    public List<User> getUsersList() {
        return userService.getUserList();
    }

    @PostMapping
    public User createNewUser(@Valid @RequestBody User user) {
        userValidator.userNameValidation(user);
        userService.createNewUser(user);
        log.info("Пользователь " + user.getName() + " создан.");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userValidator.userNameValidation(user);
        if (userService.getUser(user.getId()) == null) {
            log.warn("Ошибка пользователя: Пользователь " + user.getName() + " не найден.");
            throw new UserNotFoundException("Пользователь " + user.getName() + " не найден.");
        } else {
            userService.updateUser(user);
            log.info("Пользователь " + user.getName() + " обновлён.");
            return user;
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public String addToFriends(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        controllerUtil.isTwoUsersExists(userService, id, friendId);
        userService.getUser(id).getFriends().add(friendId);
        userService.getUser(friendId).getFriends().add(id);
        log.info("Пользователь с id-" + id + " добавил в друзья пользователя с id-" + friendId + ".");
        return "Пользователь с id-" + id + " добавил в друзья пользователя с id-" + friendId + ".";
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public String deleteFromFriends(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        controllerUtil.isTwoUsersExists(userService, id, friendId);
        userService.getUser(id).getFriends().remove(friendId);
        userService.getUser(friendId).getFriends().remove(id);
        log.info("Пользователь с id-" + id + " удалил из друзей пользователя с id-" + friendId + ".");
        return "Пользователь с id-" + id + " удалил из друзей пользователя с id-" + friendId + ".";
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        if (userService.getUser(id) == null) {
            log.warn("Ошибка пользователя: Пользователь с id-" + id + " не найден.");
            throw new UserNotFoundException("Пользователь с id-" + id + " не найден.");
        } else {
            return userService.getUser(id);
        }
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllUserFriends(@PathVariable("id") Integer id) {
        if (userService.getUser(id) == null) {
            log.warn("Ошибка пользователя: Пользователь с id-" + id + " не найден.");
            throw new UserNotFoundException("Пользователь с id-" + id + " не найден.");
        } else {
            return userService.getAllUserFriends(id);
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getAllCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        controllerUtil.isTwoUsersExists(userService, id, otherId);
        return userService.getAllCommonFriends(id, otherId);
    }
}
