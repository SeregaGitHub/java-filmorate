package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private static int userId = 0;
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUserList() {
        return userStorage.getUsersList();
    }

    public User createNewUser(User user) {
        user.setId(++userId);
        userStorage.putUser(user);
        return user;
    }

    public User updateUser(User user) {
        userStorage.putUser(user);
        return user;
    }

    public User getUser(Integer userId) {
        return userStorage.getUser(userId);
    }

    public List<User> getAllUserFriends(Integer userId) {
        return userStorage.getAllUserFriends(userId);
    }

    public List<User> getAllCommonFriends(Integer id, Integer otherId) {
        return userStorage.getAllCommonFriends(id, otherId);
    }
}
