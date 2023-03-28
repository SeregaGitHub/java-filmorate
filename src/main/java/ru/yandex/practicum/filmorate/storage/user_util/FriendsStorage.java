package ru.yandex.practicum.filmorate.storage.user_util;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendsStorage {
    Set<Integer> getAllFriendshipRequests(Integer id);

    Set<Integer> getAllUserFriends(Integer id);

    List<User> getListUserFriends(Integer userId);

    List<User> getAllCommonFriends(Integer id, Integer otherId);
}
