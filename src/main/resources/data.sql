-- Заполнение таблицы жанров
INSERT INTO GENRES (GENRE)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

-- Заполнение таблицы рейтингов
INSERT INTO MPA (RATING)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

-- // Добавление двух фильмов
-- INSERT INTO FILMS (FILM_NAME, FILM_DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID)
-- VALUES ('Film 1', 'Description 1', '2022-01-01', 1, 1, 1),
--        ('Film 2', 'Description 2', '2022-01-01', 2, 2, 2);
-- INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)
-- VALUES ((SELECT FILM_ID
--          FROM FILMS
--          WHERE FILM_NAME = 'Film 1'
--            AND FILM_DESCRIPTION = 'Description 1'
--            AND RELEASE_DATE = '2022-01-01'
--            AND DURATION = 1
--            AND RATE = 1
--            AND MPA_ID = 1), 1),
--        ((SELECT FILM_ID
--          FROM FILMS
--          WHERE FILM_NAME = 'Film 1'
--            AND FILM_DESCRIPTION = 'Description 1'
--            AND RELEASE_DATE = '2022-01-01'
--            AND DURATION = 1
--            AND RATE = 1
--            AND MPA_ID = 1), 2),
--        ((SELECT FILM_ID
--          FROM FILMS
--          WHERE FILM_NAME = 'Film 2'
--            AND FILM_DESCRIPTION = 'Description 2'
--            AND RELEASE_DATE = '2022-01-01'
--            AND DURATION = 2
--            AND RATE = 2
--            AND MPA_ID = 2), 2);
--
-- // Добавление двух пользователей
-- INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
-- VALUES ('Email 1', 'Login 1', 'Name 1', '2000-01-01'),
--        ('Email 2', 'Login 2', 'Name 2', '2000-01-01');
--
-- //Получение всех фильмов
-- SELECT (f.FILM_ID, f.FILM_NAME, FILM_DESCRIPTION, f.RELEASE_DATE, f.DURATION, m.RATING, g.GENRE)
-- FROM films AS f
--          LEFT JOIN film_genre AS fg on f.film_id = fg.film_id
--          LEFT JOIN mpa AS m on f.MPA_ID = m.MPA_ID
--          LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID
--          LEFT JOIN FILM_LIKES FL on f.FILM_ID = FL.FILM_ID;
--
-- -- //Обновление фильма
-- -- UPDATE films
-- -- SET film_name = 'Film 1 Updated', film_description = 'Description 2 Updated'
-- -- WHERE film_id = 1;
-- --
-- -- DELETE FROM film_genre WHERE film_id = 1;
-- --
-- -- INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)
-- -- VALUES ((SELECT FILM_ID
-- --          FROM FILMS
-- --          WHERE FILM_NAME = 'Film 1 Updated'
-- --            AND FILM_DESCRIPTION = 'Description 2 Updated'
-- --            AND RELEASE_DATE = '2022-01-01'
-- --            AND DURATION = 1
-- --            AND RATE = 1
-- --            AND MPA_ID = 1), 4);
-- -- ;
--
-- //Получение всех пользователей
-- SELECT *
-- FROM USERS AS u
--     LEFT JOIN USER_FRIENDS AS uf on u.USER_ID = uf.FRIEND_ID;



