package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DBUserStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    private final DBUserStorage userStorage;

    @Test
    void getByIdValidIdValidUser() {
        User testUser = User.builder()
                .email("myname@ya.ru")
                .login("login")
                .name("MyName")
                .birthday("1999-01-01")
                .build();

        Integer userId = userStorage.addNewUser(testUser).getId();

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(userId));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userId)
                );
    }

    @Test
    void getByIdNotValidIdEmpty() {
        assertThrows(NotFoundException.class, () -> userStorage.getUserById(100));
    }
}
