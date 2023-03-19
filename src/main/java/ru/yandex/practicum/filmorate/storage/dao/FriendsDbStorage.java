package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user_util.FriendsStorage;

import java.util.*;

@Component
public class FriendsDbStorage implements FriendsStorage {
    JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<Integer> getAllFriendshipRequests(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from FRIENDSHIP_REQUESTS where USER_ID = ?", id);
        Set<Integer> set = new HashSet<>();

        while (rowSet.next()) {
            Integer requesterId = rowSet.getInt("REQUESTER_ID");
            set.add(requesterId);
        }
        return set;
    }

    @Override
    public Set<Integer> getAllUserFriends(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from USER_FRIENDS where USER_ID = ?", id);
        Set<Integer> set = new HashSet<>();

        while (rowSet.next()) {
            Integer friendId = rowSet.getInt("FRIEND_ID");
            set.add(friendId);
        }
        return set;
    }

    @Override
    public List<User> getListUserFriends(Integer userId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM USER_FILMORATE WHERE USER_ID IN (" +
                "SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?)", userId);
        List<User> users= new ArrayList<>();

        while (rowSet.next()) {
            User user = User.builder()
                    .id(rowSet.getInt("USER_ID"))
                    .email(Objects.requireNonNull(rowSet.getString("USER_EMAIL")))
                    .login(Objects.requireNonNull(rowSet.getString("USER_LOGIN")))
                    .name(Objects.requireNonNull(rowSet.getString("USER_NAME")))
                    .birthday(Objects.requireNonNull(rowSet.getDate("USER_BIRTHDAY")).toLocalDate())
                    .friendshipRequests(new HashSet<>(getAllFriendshipRequests(userId)))
                    .friends(new HashSet<>(getAllUserFriends(userId)))
                    .build();
            users.add(user);
        }
        return users;
    }

    @Override
    public List<User> getAllCommonFriends(Integer id, Integer otherId) {
        List<User> commonFriends = new ArrayList<>(getListUserFriends(id));
        commonFriends.retainAll(getListUserFriends(otherId));
        return commonFriends;
    }
}
