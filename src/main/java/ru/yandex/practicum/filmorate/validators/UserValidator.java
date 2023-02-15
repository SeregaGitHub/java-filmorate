package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Slf4j
public class UserValidator {

    public User userNameValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Ввиду отсутствия у пользователя с логином " + user.getLogin()
                    + " имени, его имя будет идентично его логину.");
        }
        return user;
    }
}