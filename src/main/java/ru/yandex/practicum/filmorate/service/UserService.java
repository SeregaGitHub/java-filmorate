package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user_util.FriendsStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;
    FriendsStorage friendsStorage;

    @Autowired
    public UserService(@Qualifier ("userDbStorage") UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public List<User> getUserList() {
        return userStorage.getUsersList();
    }

    public User getUser(Integer userId) {
        return userStorage.getUser(userId);
    }

    public User createNewUser(User user) {
        userStorage.addUser(user);
        return user;
    }

    public User updateUser(User user) {
        userStorage.updateUser(user);
        return user;
    }

    public User deleteUser(Integer id) {
        return userStorage.deleteUser(id);
    }

    public List<User> getListUserFriends(Integer userId) {
        return friendsStorage.getListUserFriends(userId);
    }

    public List<User> getAllCommonFriends(Integer id, Integer otherId) {
        return friendsStorage.getAllCommonFriends(id, otherId);
    }
}
