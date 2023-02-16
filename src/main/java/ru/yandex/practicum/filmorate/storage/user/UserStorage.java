package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsersList();
    User getUser(Integer userId);
    void putUser(User user);
    User deleteUser(Integer id);
}
