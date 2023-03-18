package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(User user) {
        Integer userId = addUserInfo(user);
        user.setId(userId);
        String sqlQueryToAddUser = "INSERT INTO friend_request (sender_id, addressee_id) VALUES (?, ?)";
        user.getFriends().stream().map(friend -> jdbcTemplate.update(sqlQueryToAddUser, userId, friend));
    }

    @Override
    public void delete(User user) {
    }

    @Override
    public void update(User user) {
        String sqlQueryToUpdateUser = "UPDATE person " +
                "SET email = ?, login = ?, name = ?, birthday = ? WHERE person_id = ?";
        if (jdbcTemplate.update(sqlQueryToUpdateUser, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId()) == 0) {
            throw new NotFoundException("Bad user update");
        }
    }

    @Override
    public List<User> getUsers() {
        String sqlQueryToGetAllUsers = "SELECT * FROM person";
        return jdbcTemplate.query(sqlQueryToGetAllUsers, this::makeUser);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sqlQueryToAddFriend = "INSERT INTO friend_request (sender_id, addressee_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQueryToAddFriend, userId, friendId);
        } catch (DuplicateKeyException e) {
            String message = "Unable to add to friends a user who is already friends";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        } catch (DataIntegrityViolationException e) {
            String message = "Unable to add friends to myself";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sqlQueryToDeleteFromFriends = "DELETE FROM friend_request WHERE sender_id = ? AND addressee_id = ?";

        if (jdbcTemplate.update(sqlQueryToDeleteFromFriends, userId, friendId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No like from user");
        }
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        String sqlQueryToGetCommonFriends = "SELECT * " +
                "FROM person " +
                "WHERE person_id IN " +
                "(SELECT * FROM (SELECT  addressee_id " +
                "FROM FRIEND_REQUEST " +
                "WHERE sender_id = ? OR sender_id = ? )  GROUP BY addressee_id HAVING COUNT(addressee_id) > 1)";
        return jdbcTemplate.query(sqlQueryToGetCommonFriends, this::makeFriendUser, userId, friendId);
    }

    @Override
    public List<User> getFriends(Integer friendId) {
        String sqlQueryToGetAllFriends = "SELECT * FROM person " +
                "WHERE person_id IN (SELECT addressee_id FROM friend_request WHERE sender_id = ?)";
        return jdbcTemplate.query(sqlQueryToGetAllFriends, this::makeFriendUser, friendId);
    }

    @Override
    public User getUser(Integer userId) {
        String sqlQueryToGetUserById = "SELECT * FROM person WHERE person_id = ?";
        return jdbcTemplate.queryForObject(sqlQueryToGetUserById, this::makeUser, userId);
    }

    @Override
    public boolean containsUser(Integer userId) {
        String sqlQueryToFindUserById = "SELECT * FROM person WHERE person_id = ?";

        try {
            jdbcTemplate.queryForObject(sqlQueryToFindUserById, this::makeUser, userId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public boolean containsUser(User user) {
        String sqlQueryToFindUser = "SELECT * FROM person WHERE email = ? AND login = ? AND name = ? AND birthday = ?";

        try {
            jdbcTemplate.queryForObject(sqlQueryToFindUser, this::makeUser, user.getEmail(), user.getLogin(),
                    user.getName(), user.getBirthday());
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private int addUserInfo(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("person")
                .usingGeneratedKeyColumns("person_id");
        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("person_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        String sqlQueryToGetAllUser = "SELECT * FROM person WHERE person_id IN (SELECT addressee_id  FROM friend_request" +
                " WHERE sender_id = ?)";
        user.getFriends().addAll(jdbcTemplate.query(sqlQueryToGetAllUser, this::makeFriendUser, user.getId()));
        return user;
    }

    private User makeFriendUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("person_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}