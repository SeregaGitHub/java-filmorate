package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user_util.FriendsStorage;

import java.util.*;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsStorage userUtilDao;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendsStorage userUtilDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userUtilDao = userUtilDao;
    }

    @Override
    public List<User> getUsersList() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from USER_FILMORATE ORDER BY USER_ID");
        List<User> users = new ArrayList<>();

        while (rowSet.next()) {
            User user = makeUser(rowSet, rowSet.getInt("USER_ID"));
            users.add(user);
        }
        return users;
    }

    @Override
    public User getUser(Integer userId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from USER_FILMORATE where USER_ID = ?", userId);
        User user = null;
        if (rowSet.next()) {
            user = makeUser(rowSet, userId);
        }
        return user;
    }

    @Override
    public User addUser(User user) {
        jdbcTemplate.update("insert into USER_FILMORATE (USER_EMAIL" +
                        ", USER_LOGIN, USER_NAME, USER_BIRTHDAY) values (?, ?, ?, ?)"
                , user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

    // Следующая ниже часть кода нужна только для того, чтобы присвоить айди пользователю, проверяемому в тестах Postman
        String nextUserId = "select USER_ID as NEXT_USER_ID from USER_FILMORATE where USER_EMAIL = ?";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(nextUserId, user.getEmail());
        int userId = 1;
        if (sqlRowSet.next()) {
            userId = sqlRowSet.getInt("NEXT_USER_ID");
        }
        user.setId(userId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int userId = user.getId();
        jdbcTemplate.update("update USER_FILMORATE set USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ?" +
                        ", USER_BIRTHDAY = ? where USER_ID = ?"
                , user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), userId);

        Set<Integer> friendshipRequests = user.getFriendshipRequests();
        Set<Integer> friends = user.getFriends();

        jdbcTemplate.update("DELETE FROM FRIENDSHIP_REQUESTS WHERE USER_ID = ?", userId);
        jdbcTemplate.update("DELETE FROM USER_FRIENDS WHERE USER_ID = ?", userId);

        if (friendshipRequests != null) {
            for (Integer i : friendshipRequests) {
                jdbcTemplate.update("MERGE INTO FRIENDSHIP_REQUESTS KEY (USER_ID, REQUESTER_ID) VALUES (?, ?)"
                        , userId, i);
            }
        }
        if (friends != null) {
            for (Integer i : friends) {
                jdbcTemplate.update("MERGE INTO USER_FRIENDS KEY (USER_ID, FRIEND_ID) VALUES (?, ?)"
                        , userId, i);
            }
        }
        return getUser(userId);
    }

    @Override
    public User deleteUser(Integer id) {
        User user = getUser(id);
        jdbcTemplate.update("delete from USER_FILMORATE where USER_ID = ?", id);
        return user;
    }

    private Set<Integer> getAllFriendshipRequests(Integer id) {
        return userUtilDao.getAllFriendshipRequests(id);
    }

    private Set<Integer> getAllUserFriends(Integer id) {
        return userUtilDao.getAllUserFriends(id);
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
