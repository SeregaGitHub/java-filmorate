package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsersList();
    void putUser(User user);
    User getUser(Integer userId);
    List<User> getAllUserFriends(Integer userId);
    List<User> getAllCommonFriends(Integer id, Integer otherId);
}
