package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public User getUser(Integer userId) {
        return userStorage.getUser(userId);
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

    public User deleteUser(Integer id) {
        return userStorage.deleteUser(id);
    }

    public List<User> getAllUserFriends(Integer userId) {
        return new ArrayList<>(userStorage.getUsersList().stream()
                                                         .filter(u -> u.getFriends().contains(userId))
                                                         .collect(Collectors.toList()));
    }

    public List<User> getAllCommonFriends(Integer id, Integer otherId) {
        Set<Integer> commonFriends = new HashSet<>(userStorage.getUser(id).getFriends());
        commonFriends.retainAll(userStorage.getUser(otherId).getFriends());

        return new ArrayList<>(userStorage.getUsersList().stream()
                                                         .filter(user1 -> commonFriends.contains(user1.getId()))
                                                         .collect(Collectors.toList()));
    }
}
