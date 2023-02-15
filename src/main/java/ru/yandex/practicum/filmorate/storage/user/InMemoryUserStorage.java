package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
    public void putUser(User user) {
        userHashMap.put(user.getId(), user);
        userIdSet.add(user.getId());
    }

    @Override
    public User getUser(Integer userId) {
        return userHashMap.get(userId);
    }

    @Override
    public List<User> getAllUserFriends(Integer userId) {
        return new ArrayList<>(userHashMap.get(userId).getFriends().stream()
                                                                   .map(userHashMap::get)
                                                                   .collect(Collectors.toList()));
    }

    @Override
    public List<User> getAllCommonFriends(Integer id, Integer otherId) {
        Set<Integer> mutualFriends = new HashSet<>(userHashMap.get(id).getFriends());
        mutualFriends.retainAll(userHashMap.get(otherId).getFriends());

        return new ArrayList<>(mutualFriends.stream()
                                            .map(userHashMap::get)
                                            .collect(Collectors.toList()));
    }
}
