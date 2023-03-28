MERGE INTO "RATING" KEY ("RATING_ID") VALUES (1, 'G');        -- у фильма нет возрастных ограничений
MERGE INTO "RATING" KEY ("RATING_ID") VALUES (2, 'PG');       -- детям рекомендуется смотреть фильм с родителями
MERGE INTO "RATING" KEY ("RATING_ID") VALUES (3, 'PG-13');    -- детям до 13 лет просмотр не желателен
MERGE INTO "RATING" KEY ("RATING_ID") VALUES (4, 'R');        -- лицам до 17 лет просматривать фильм можно только в присутствии взрослого
MERGE INTO "RATING" KEY ("RATING_ID") VALUES (5, 'NC-17');    -- лицам до 18 лет просмотр запрещён

MERGE INTO "GENRE" KEY ("GENRE_ID") VALUES (1, 'Комедия');
MERGE INTO "GENRE" KEY ("GENRE_ID") VALUES (2, 'Драма');
MERGE INTO "GENRE" KEY ("GENRE_ID") VALUES (3, 'Мультфильм');
MERGE INTO "GENRE" KEY ("GENRE_ID") VALUES (4, 'Триллер');
MERGE INTO "GENRE" KEY ("GENRE_ID") VALUES (5, 'Документальный');
MERGE INTO "GENRE" KEY ("GENRE_ID") VALUES (6, 'Боевик');