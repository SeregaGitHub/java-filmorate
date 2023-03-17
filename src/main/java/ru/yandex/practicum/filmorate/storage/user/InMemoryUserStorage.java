package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> userHashMap = new HashMap<>();
    // userIdSet + isUserExists(): необходимы для проверки существования пользователя при попытке поставить лайк фильму.
    private final static Set<Integer> userIdSet = new HashSet<>();

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
    public void putUser(User user) {
        userHashMap.put(user.getId(), user);
        userIdSet.add(user.getId());
    }

    @Override
    public User deleteUser(Integer id) {
        User user = userHashMap.remove(id);
        for (Integer i: user.getFriends()) {
            userHashMap.get(i).getFriends().remove(id);
        }
        return user;
    }
}
