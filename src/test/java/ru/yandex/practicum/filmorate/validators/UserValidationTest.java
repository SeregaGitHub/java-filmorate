package ru.yandex.practicum.filmorate.validators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class UserValidationTest {
    private User user;
    private UserController userController;

    @BeforeEach
    void beforeEach() {
        user = new User( "serega.kraev1@yandex.ru", "serega_kraev", "Serega"
                , LocalDate.of(1985, 1,8));
    }

    @BeforeEach
    void createNewUserController() {
        userController = new UserController();
    }

    @AfterEach
    void clearUserController() {
        userController.getUsersList().clear();
        try {
            Field field = UserController.class.getDeclaredField("userId");
            field.setAccessible(true);
            field.set(field, 0);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
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