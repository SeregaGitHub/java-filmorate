package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film_util.FilmUtilDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/script.sql"})
class FilmorateApplicationTests {
	private final FilmUtilDao filmUtilDao;
	private final UserStorage userDbStorage;
	private final FilmStorage filmDbStorage;
	private User user1;
	private User user2;
	private User user3;
	private Film film1;
	private Film film2;
	private Film film3;
	private Mpa mpa1;
	private Mpa mpa2;
	private Mpa mpa3;
	private Genre genre1;
	private Genre genre2;

	@BeforeEach
	void beforeEach() {
		user1 = User.builder()
				.email("email_1")
				.login("login_1")
				.name("name_1")
				.birthday(LocalDate.parse("1985-01-01"))
				.build();
		userDbStorage.addUser(user1);
		user2 = User.builder()
				.email("email_2")
				.login("login_2")
				.name("name_2")
				.birthday(LocalDate.parse("1985-02-02"))
				.build();
		userDbStorage.addUser(user2);
		user3 = User.builder()
				.email("email_3")
				.login("login_3")
				.name("name_3")
				.birthday(LocalDate.parse("1985-03-03"))
				.build();
		userDbStorage.addUser(user3);

		mpa1 = Mpa.builder()
				.id(1)
				.name("G")
				.build();

		film1 = Film.builder()
				.name("film_1")
				.description("description_1")
				.releaseDate(LocalDate.parse("2023-01-01"))
				.duration(111)
				.mpa(mpa1)
				.build();
		filmDbStorage.addFilm(film1);

		mpa2 = Mpa.builder()
				.id(2)
				.name("PG")
				.build();

		film2 = Film.builder()
				.name("film_2")
				.description("description_2")
				.releaseDate(LocalDate.parse("2023-02-02"))
				.duration(222)
				.mpa(mpa2)
				.build();
		filmDbStorage.addFilm(film2);

		mpa3 = Mpa.builder()
				.id(3)
				.name("PG-13")
				.build();

		film3 = Film.builder()
				.name("film_3")
				.description("description_3")
				.releaseDate(LocalDate.parse("2023-03-03"))
				.duration(333)
				.mpa(mpa3)
				.build();
		filmDbStorage.addFilm(film3);

		genre1 = Genre.builder()
				.id(1)
				.name("Комедия")
				.build();

		genre2 = Genre.builder()
				.id(2)
				.name("Драма")
				.build();
	}

	@Test
	void getUserWhenUserNotExists() {
		assertNull(userDbStorage.getUser(9999), "Пользователя с Id: '9999' - не существует.");
	}

	@Test
	void getUserWhenUserExists() {
		User user = userDbStorage.getUser(1);
		assertEquals(1, user.getId(), "Id пользователя должно быть - '1'.");
		assertEquals("email_1", user.getEmail(), "емэйл пользователя должен быть - 'email_1'.");
		assertEquals("login_1", user.getLogin(), "логин пользователя должен быть - 'login_1'.");
		assertEquals("name_1", user.getName(), "имя пользователя должно быть - 'name_1'.");
		assertEquals(LocalDate.of(1985, 1, 1), user.getBirthday()
				, "день рождения пользователя должно быть - '1985-01-01'.");
	}

	@Test
	void addUser() {
		User user4 = User.builder()
				.email("email_4")
				.login("login_4")
				.name("name_4")
				.birthday(LocalDate.parse("1985-04-04"))
				.build();
		userDbStorage.addUser(user4);
		User userAdd = userDbStorage.getUser(4);

		assertEquals(4, userAdd.getId(), "Id пользователя должно быть - '4'.");
		assertEquals("email_4", userAdd.getEmail(), "емэйл пользователя должен быть - 'email_4'.");
		assertEquals("login_4", userAdd.getLogin(), "логин пользователя должен быть - 'login_4'.");
		assertEquals("name_4", userAdd.getName(), "имя пользователя должно быть - 'name_4'.");
		assertEquals(LocalDate.of(1985, 4, 4), userAdd.getBirthday()
				, "день рождения пользователя должно быть - '1985-04-04'.");
	}

	@Test
	void updateUser() {
		User user5 = User.builder()
				.id(1)
				.email("email_5")
				.login("login_5")
				.name("name_5")
				.birthday(LocalDate.parse("1985-05-05"))
				.build();
		userDbStorage.updateUser(user5);
		User userUpdate = userDbStorage.getUser(1);

		assertEquals(1, userUpdate.getId(), "Id пользователя должно быть - '1'.");
		assertEquals("email_5", userUpdate.getEmail(), "емэйл пользователя должен быть - 'email_5'.");
		assertEquals("login_5", userUpdate.getLogin(), "логин пользователя должен быть - 'login_5'.");
		assertEquals("name_5", userUpdate.getName(), "имя пользователя должно быть - 'name_5'.");
		assertEquals(LocalDate.of(1985, 5, 5), userUpdate.getBirthday()
				, "день рождения пользователя должно быть - '1985-05-05'.");
	}

	@Test
	void deleteUser() {
		userDbStorage.deleteUser(1);
		assertNull(userDbStorage.getUser(1), "Пользователя с ID: 1 - не должно существовать.");
	}

	@Test
	void getUsersList() {
		assertEquals(3, userDbStorage.getUsersList().size()
				, "Количество пользователей должно быть - 3.");
	}

	@Test
	void getFilmWhenFilmNotExists() {
		assertNull(filmDbStorage.getFilm(9999), "Фильма с Id: '9999' - не существует.");
	}

	@Test
	void getFilmWhenFilmExists() {
		Film film = filmDbStorage.getFilm(1);
		assertEquals(1, film.getId(), "Id фильма должно быть - 1");
		assertEquals("film_1", film.getName(), "Имя фильма должно быть - 'film_1'.");
		assertEquals("description_1", film.getDescription()
				, "Описание фильма должно быть - 'description_1'.");
		assertEquals(LocalDate.of(2023, 1, 1), film.getReleaseDate()
				, "Дата релиза фильма должна быть - '2023-01-01'.");
		assertEquals(111, film.getDuration(), "Продолжительность фильма должна быть - '111'.");
		assertEquals("G", film.getMpa().getName(), "Mpa фильма должно быть - 'G'.");
	}

	@Test
	void addFilm() {
		Film film4 = Film.builder()
				.name("film_4")
				.description("description_4")
				.releaseDate(LocalDate.parse("2023-04-04"))
				.duration(444)
				.mpa(mpa1)
				.build();
		filmDbStorage.addFilm(film4);
		Film filmAdd = filmDbStorage.getFilm(4);

		assertEquals(4, filmAdd.getId(), "Id фильма должно быть - 4");
		assertEquals("film_4", filmAdd.getName(), "Имя фильма должно быть - 'film_4'.");
		assertEquals("description_4", filmAdd.getDescription()
				, "Описание фильма должно быть - 'description_4'.");
		assertEquals(LocalDate.of(2023, 4, 4), filmAdd.getReleaseDate()
				, "Дата релиза фильма должна быть - '2023-04-04'.");
		assertEquals(444, filmAdd.getDuration(), "Продолжительность фильма должна быть - '444'.");
		assertEquals("G", filmAdd.getMpa().getName(), "Mpa фильма должно быть - 'G'.");

	}

	@Test
	void updateFilm() {
		Film film5 = Film.builder()
				.id(1)
				.name("film_5")
				.description("description_5")
				.releaseDate(LocalDate.parse("2023-05-05"))
				.duration(555)
				.mpa(mpa1)
				.build();
		filmDbStorage.updateFilm(film5);
		Film filmUpdate = filmDbStorage.getFilm(1);

		assertEquals(1, filmUpdate.getId(), "Id фильма должно быть - 1");
		assertEquals("film_5", filmUpdate.getName(), "Имя фильма должно быть - 'film_5'.");
		assertEquals("description_5", filmUpdate.getDescription()
				, "Описание фильма должно быть - 'description_5'.");
		assertEquals(LocalDate.of(2023, 5, 5), filmUpdate.getReleaseDate()
				, "Дата релиза фильма должна быть - '2023-05-05'.");
		assertEquals(555, filmUpdate.getDuration(), "Продолжительность фильма должна быть - '555'.");
		assertEquals("G", filmUpdate.getMpa().getName(), "Mpa фильма должно быть - 'G'.");
	}

	@Test
	void deleteFilm() {
		filmDbStorage.deleteFilm(1);
		assertNull(filmDbStorage.getFilm(1), "Фильма с ID: 1 - не должно существовать.");
	}

	@Test
	void getFilmsList() {
		assertEquals(3, filmDbStorage.getFilmsList().size(), "Количество фильмов должно быть - 3.");
	}


	@Test
	void getGenreWhenGenreNotExists() {
		assertNull(filmUtilDao.getGenre("9999"), "Жанра с Id: '9999' - не существует.");
	}

	@Test
	void getGenreWhenGenreExists() {
		assertEquals(filmUtilDao.getGenre("1"), genre1
				, "Жанра с Id: '1' - существует.");
	}

	@Test
	void getAllGenres() {
		assertEquals(6, filmUtilDao.getAllGenres().size(), "Жанров должно быть - 6.");
	}

	@Test
	void getMpaWhenMpaNotExists() {
		assertNull(filmUtilDao.getMpa("9999"), "Mpa с Id: '9999' - не существует.");
	}

	@Test
	void getMpaWhenMpaExists() {
		assertEquals(filmUtilDao.getMpa("1"), mpa1
				, "Mpa с Id: '1' - существует.");
	}

	@Test
	void getAllMpa() {
		assertEquals(5, filmUtilDao.getAllMpa().size(), "Mpa должно быть - 5.");
	}
}