# Filmorate

### Приложение для киноманов

Пользователи имеют возможность:
1. Ставить лайки фильмам
2. Получть список лучших фильмов
3. Добавлять друг друга в друзья

**Схема базы данных приложения**

![db_schema](https://github.com/SeregaGitHub/java-filmorate/blob/add-friends-likes/filmorate.png)

Пояснения к схеме базы данных:
* `Таблица user_filmorate` - модель пользователя.
* `Таблица user_friends` - содержит в себе информацию о друзьях пользователей.
* `Таблица friendship_requests` - содержит в себе запросы пользователей на дружбу
  (если запрос будет подтверждён, то id пользователя удалится из этой таблицы и 
                       добавится в таблиицу user_friends).
* `Таблица film` - модель фильма.
* `Таблица rating` - содержит в себе информацию о рейтинге Ассоциации кинокомпаний
  (англ. Motion Picture Association, сокращённо МРА).
* `Таблица genre` - возможные жанры фильмов.
* `Таблица film_genre` - жанры конкретных фильмов.
* `Таблица likes` - лайки пользователей конкретным фильмам.

***Примеры SQL запросов к приложению:***
* -- Выбрать все драмы
```
  SELECT film.film_name, genre.genre_name  
  FROM film_genre  
  JOIN film USING (film_id)  
  JOIN genre USING (genre_id)  
  WHERE genre.genre_name = 'Драма'
```
* -- Выбрать все жанры фильма 'Graf'
```
  SELECT film.film_name, genre.genre_name  
  FROM film_genre  
  JOIN film USING (film_id)  
  JOIN genre USING (genre_id)  
  WHERE film.film_name = 'Graf'
```
* -- Выбрать все фильмы, которым поставил лайк пользователь 'IGORYAN'
```
  SELECT film.film_name, user_filmorate.user_login  
  FROM likes  
  JOIN film USING (film_id)  
  JOIN user_filmorate USING (user_id)  
  WHERE user_filmorate.user_login = 'IGORYAN'
```
* -- Выбрать все фильмы, у которых более одного лайка
```  
  SELECT film.film_name, COUNT(user_id)  
  FROM likes  
  JOIN film USING (film_id)  
  JOIN user_filmorate USING (user_id)  
  GROUP BY film.film_name  
  HAVING COUNT(user_id) > 1;
```
* -- Выбрать друзей пользователя с логином 'VANYA' (id=4)
```
  SELECT user_filmorate.user_login  
  FROM user_filmorate  
  WHERE user_filmorate.user_id IN (  
      SELECT friend_id  
      FROM user_friends  
      WHERE user_friends.user_id =  
                                 (SELECT user_id  
                                  FROM user_filmorate  
                                  WHERE user_login = 'VANYA')  
  );
```

Приложение написано на Java
