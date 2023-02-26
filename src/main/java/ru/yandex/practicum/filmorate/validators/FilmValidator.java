package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

@Component
@Slf4j
public class FilmValidator {

    boolean filmDurationValidator(Film film) {
        boolean check = false;
        if (film.getDuration() <= 0) {
            log.warn("Ошибка пользователя: Валидация фильма \"" + film.getName() + "\" не пройдена: " +
                    "продолжительность фильма должна быть больше нуля !!!");
        } else {
            check = true;
        }
        return check;
    }

    boolean filmReleaseDateValidator(Film film) {
        boolean check = false;
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Ошибка пользователя: Валидация фильма \"" + film.getName() + "\" не пройдена: дата релиза " +
                    film.getReleaseDate() + " раньше чем день рождения кино !!!");
        } else {
            check = true;
        }
        return check;
    }

    public void filmValidator(Film film) {
        boolean filmDurationValidation = filmDurationValidator(film);
        boolean filmReleaseDateValidation = filmReleaseDateValidator(film);

        if (!filmDurationValidation) {
            throw new ValidationException("Продолжительность фильма должна быть положительной !!!");
        } else if (!filmReleaseDateValidation) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года !!!");
        }
    }
}
