package ru.yandex.practicum.filmorate.storage.MPA;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPAStorage {
    List<MPA> getAll();

    MPA getById(Long id) throws NotFoundException;
}