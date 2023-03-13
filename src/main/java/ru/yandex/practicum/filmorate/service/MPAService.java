package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPA.MPAStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MPAService {
    private final MPAStorage mpaStorage;

    public List<MPA> getAll() {
        return mpaStorage.getAll();
    }

    public MPA getById(Integer id) throws NotFoundException {
        return mpaStorage.getById(id);
    }
}