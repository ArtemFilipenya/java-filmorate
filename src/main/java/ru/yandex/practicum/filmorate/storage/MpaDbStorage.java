package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("mpaDbStorage")
public class MpaDbStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpas() {
        String sqlQueryToGetMpas = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQueryToGetMpas, this::makeMpa);
    }

    @Override
    public Mpa getMpa(Integer id) {
        Mpa mpa;
        String sqlQueryToGetMpaById = "SELECT * FROM mpa WHERE mpa_id = ?";

        try {
            mpa = jdbcTemplate.queryForObject(sqlQueryToGetMpaById, this::makeMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Rating not found");
        }
        return mpa;
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }
}