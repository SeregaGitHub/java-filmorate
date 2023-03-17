package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier ("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
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

    public List<User> getAllUserFriends(Integer userId) {
        User user = userStorage.getUser(userId);
        Set<Integer> userFriendsId = new HashSet<>(user.getFriends());

        return userStorage.getUsersList().stream()
                .filter(u -> userFriendsId.contains(u.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getAllCommonFriends(Integer id, Integer otherId) {
        Set<Integer> commonFriends = new HashSet<>(userStorage.getUser(id).getFriends());
        commonFriends.retainAll(userStorage.getUser(otherId).getFriends());

        return userStorage.getUsersList().stream()
                .filter(user1 -> commonFriends.contains(user1.getId()))
                .collect(Collectors.toList());
    }
}
