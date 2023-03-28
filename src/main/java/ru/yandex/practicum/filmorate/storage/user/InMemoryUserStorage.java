package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private static int userId = 0;
    private final HashMap<Integer, User> userHashMap = new HashMap<>();
    // userIdSet + isUserExists(): необходимы для проверки существования пользователя при попытке поставить лайк фильму.
    private static final Set<Integer> userIdSet = new HashSet<>();

    public static boolean isUserExists(Integer userId) {
        return userIdSet.contains(userId);
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(userHashMap.values());
    }

    @Override
    public User getUser(Integer userId) {
        return userHashMap.get(userId);
    }

    @Override
    public User addUser(User user) {
        user.setId(++userId);
        userHashMap.put(user.getId(), user);
        userIdSet.add(user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        userHashMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User deleteUser(Integer id) {
        User user = userHashMap.remove(id);
        for (User u: userHashMap.values()) {
            u.getFriends().remove(id);
            u.getFriendshipRequests().remove(id);
        }
        return user;
    }
}
