package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.ControllerUtil;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class UserValidationTest {
    private User user;
    private UserController userController;
    private UserService userService;
    private UserStorage userStorage;
    private UserValidator userValidator;
    private ControllerUtil controllerUtil;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .email("serega.kraev1@yandex.ru")
                .login("serega_kraev")
                .name("Serega")
                .birthday(LocalDate.of(1985, 1,8))
                .build();
    }

    @BeforeEach
    void createNewUserController() {
        controllerUtil = new ControllerUtil();
        userStorage = new InMemoryUserStorage();
        userValidator = new UserValidator();
        userService = new UserService(userStorage);
        userController = new UserController(userService, userValidator, controllerUtil);
    }

    @AfterEach
    void clearUserController() {
        if (userStorage instanceof InMemoryUserStorage) {
            try {
                Field field = InMemoryUserStorage.class.getDeclaredField("userId");
                field.setAccessible(true);
                field.set(field, 0);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void shouldMakeUserNameLikeLoginIfHisNameIsEmpty() {
        String userLogin = user.getLogin();
        user.setName("");
        userController.createNewUser(user);
        assertEquals(userController.getUsersList().get(0).getName(), userLogin, "При добавлении пользователя " +
                "с пустым именем, его имя должно быть идентично его логину.");
    }

    @Test
    void shouldMakeUserNameLikeLoginIfHisNameIsNull() {
        String userLogin = user.getLogin();
        user.setName(null);
        userController.createNewUser(user);
        assertEquals(userController.getUsersList().get(0).getName(), userLogin, "При добавлении пользователя " +
                "с отсутствующим именем, его имя должно быть идентично его логину.");
    }
}