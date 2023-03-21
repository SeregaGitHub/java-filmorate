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
            User user = makeUser(rowSet, rowSet.getInt("USER_ID"));
            users.add(user);
        }
        return users;
    }

    @Override
    public List<User> getAllCommonFriends(Integer id, Integer otherId) {
        //  "опять грузим много лишних данных, нужно сделать один запрос вида:
        // select * from USER_FILMORATE u, USER_FRIENDS f, USER_FRIENDS o  where  ..... "

        //   К сожалению, синтаксис этого запроса мне не знаком.
        // Но, как я понял, мне нужно уложиться в один запрос, а не в несколько.
        // Я написал свой вариант - надеюсь он подойдёт.
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM USER_FILMORATE WHERE USER_FILMORATE.USER_ID" +
                " IN (SELECT FRIEND_ID FROM USER_FRIENDS GROUP BY FRIEND_ID HAVING COUNT (FRIEND_ID) > 1)");
        List<User> users= new ArrayList<>();

        while (rowSet.next()) {
            User user = makeUser(rowSet, rowSet.getInt("USER_ID"));
            users.add(user);
        }
        return users;
    }

    private User makeUser(SqlRowSet rowSet, Integer id) {
        User user = User.builder()
                .id(id)
                .email(Objects.requireNonNull(rowSet.getString("USER_EMAIL")))
                .login(Objects.requireNonNull(rowSet.getString("USER_LOGIN")))
                .name(Objects.requireNonNull(rowSet.getString("USER_NAME")))
                .birthday(Objects.requireNonNull(rowSet.getDate("USER_BIRTHDAY")).toLocalDate())
                .friendshipRequests(new HashSet<>(getAllFriendshipRequests(id)))
                .friends(new HashSet<>(getAllUserFriends(id)))
                .build();
        return user;
    }
}

