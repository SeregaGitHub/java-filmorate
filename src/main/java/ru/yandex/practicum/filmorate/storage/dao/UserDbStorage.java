package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user_util.FriendsStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendsStorage friendsStorage;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendsStorage userUtilDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendsStorage = userUtilDao;
    }

    @Override
    public List<User> getUsersList() {
        String sqlUsers = "select * from USER_FILMORATE ORDER BY USER_ID";
        List<User> users = jdbcTemplate.query(sqlUsers, (rs, rowNum) -> makeUser(rs));

        for (User u: users) {
            Set<Integer> userFriends = getAllUserFriends(u.getId());
            if (!userFriends.isEmpty()) {
                u.setFriends(userFriends);
            }
            Set<Integer> userFriendshipRequesters = getAllFriendshipRequests(u.getId());
            if (!userFriendshipRequesters.isEmpty()) {
                u.setFriendshipRequests(userFriendshipRequesters);
            }
        }
        return users;
    }

    @Override
    public User getUser(Integer userId) {
        Set<Integer> userFriends = new HashSet<>(getAllUserFriends(userId));
        Set<Integer> userFriendshipRequests = new HashSet<>(getAllFriendshipRequests(userId));
        String sql = "select * from USER_FILMORATE where USER_ID = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
        User user = null;
        if (users.size() == 1) {
            user = users.stream().findFirst().get();
            user.setFriends(userFriends);
            user.setFriendshipRequests(userFriendshipRequests);
            return user;
        } else {
            return user;
        }
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(
                "insert into USER_FILMORATE (USER_EMAIL" +
                        ", USER_LOGIN, USER_NAME, USER_BIRTHDAY) values (?, ?, ?, ?)"
                , new String[]{"USER_ID"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday())); return statement;}, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
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
        return friendsStorage.getAllFriendshipRequests(id);
    }

    private Set<Integer> getAllUserFriends(Integer id) {
        return friendsStorage.getAllUserFriends(id);
    }

    static User makeUser(ResultSet rs) throws SQLException {
        User user = User.builder()
                .id(rs.getInt("USER_ID"))
                .email(Objects.requireNonNull(rs.getString("USER_EMAIL")))
                .login(Objects.requireNonNull(rs.getString("USER_LOGIN")))
                .name(Objects.requireNonNull(rs.getString("USER_NAME")))
                .birthday(Objects.requireNonNull(rs.getDate("USER_BIRTHDAY")).toLocalDate())
                .build();
        return user;
    }
}
