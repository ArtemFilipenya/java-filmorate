package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository("userStorage")
public class DBUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public DBUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery =
                "SELECT u.id, " +
                        "u.email, " +
                        "u.login, " +
                        "u.name, " +
                        "u.birthday, " +
                        "FROM users AS u;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery =
                "SELECT u.id, " +
                        "u.email, " +
                        "u.login, " +
                        "u.name, " +
                        "u.birthday, " +
                        "FROM users AS u " +
                        "WHERE u.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " does not exist."));
    }

    @Override
    public User addNewUser(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
            }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return getUserById(user.getId());
    }

    @Override
    public void deleteUser(User user) {
        String sqlQuery = "DELETE FROM users WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    public void makeFriends(int userId, int friendId) {
        String sqlQuery = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public void removeFriends(int userId, int friendId) {
        String sqlQuery = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public List<Integer> getUserFriendsById(Integer userId) {
        String sqlQuery =
                "SELECT fr.friend_id, " +
                        "FROM friendships AS fr " +
                        "WHERE fr.user_id = ?;";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        String birthday = rs.getDate("birthday").toString();

        List<Integer> friends = getUserFriendsById(id);
        return new User(id, email, login, name, birthday);
    }
}