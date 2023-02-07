package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class FilmValidator {

    public static boolean filmDurationValidator(Film film) {
        boolean check = false;
        if (film.getDuration() < 0) {
            log.info("Валидация фильма \"" + film.getName() + "\" не пройдена: продолжительность фильма меньше нуля !!!");
        } else {
            check = true;
        }
        return check;
    }

    public static boolean filmReleaseDateValidator(Film film) {
        boolean check = false;
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.info("Валидация фильма \"" + film.getName() + "\" не пройдена: дата релиза " + film.getReleaseDate()
                    + " раньше чем день рождения кино !!!");
        } else {
            check = true;
        }
        return check;
    }
}
