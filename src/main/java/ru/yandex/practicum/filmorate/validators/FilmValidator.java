package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.Genre;
import ru.yandex.practicum.filmorate.util.Rating;

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

    static boolean filmRatingValidator(Film film) {
        boolean check = false;
        if (!Rating.isRatingExists(film.getMpa().getName())) {
            log.warn("Ошибка пользователя: Валидация фильма \"" + film.getName() + "\" не пройдена: рейтинга "
                    + film.getMpa().getName() + " не существует !!!");
        } else {
            check = true;
        }
        return check;
    }

    static boolean filmGenreValidator(Film film) {
        boolean check = false;
        if (film.getGenres().isEmpty()) {
            log.warn("Ошибка пользователя: Валидация фильма \"" + film.getName() + "\" не пройдена: у фильма "
                    + film.getName() + " отсутствует жанр !!!");
        } else {
            for (ru.yandex.practicum.filmorate.model.Genre s : film.getGenres()) {
                if (!Genre.isGenreExists(s.getName())) {
                    log.warn("Ошибка пользователя: Валидация фильма \"" + film.getName() + "\" не пройдена: жанра "
                            + s + " не существует !!!");
                    return check;
                }
            }
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

    public static void filmInMemoryValidator(Film film) {
        boolean filmRatingValidator = filmRatingValidator(film);
        boolean filmGenreValidator = filmGenreValidator(film);

        if (!filmRatingValidator) {
            throw new ValidationException("Рейтинг фильма должен находиться в рейтинге Ассоциации кинокомпаний !!!");
        } else if (!filmGenreValidator) {
            throw new ValidationException("Жанр фильма должен присутствовать в списке жанров !!!");
        }
    }
}
