package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user_util.FriendsStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String sql = "SELECT * FROM USER_FILMORATE WHERE USER_ID IN (" +
                "SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?)";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> UserDbStorage.makeUser(rs), userId);
        return users;
    }

    @Override
    public List<User> getAllCommonFriends(Integer id, Integer otherId) {
        String sql = "select " +
                "u.USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY, o.FRIEND_ID " +
                "FROM USER_FILMORATE u, USER_FRIENDS f, USER_FRIENDS o " +
                "WHERE u.USER_ID = f.FRIEND_ID AND u.USER_ID = o.FRIEND_ID AND " +
                "f.USER_ID = ? AND o.USER_ID = ?";
        List<User> userList = jdbcTemplate.query(sql, (rs, rowNum) -> UserDbStorage.makeUser(rs), id, otherId);
        return userList;
    }
}

