package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int currentId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);

        if (!films.containsKey(id)) {
            throw new NotFoundException(String.format("Film with id=[%d] not found.", id));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException(String.format("Film with id=[%d] not found.", film.getId()));
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film addNewFilm(Film film) {
        film.setId(currentId);
        films.put(currentId, film);
        ++currentId;
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        if (!films.containsValue(film)) {
            throw new NotFoundException(String.format("Film with id=[%d] not found.", film.getId()));
        }
        films.remove(film.getId());
    }
}