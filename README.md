# java-filmorate
Проект социальной сети для выбора фильмов.

## Структура базы данных
Ссылка на структуру: https://dbdiagram.io/d/633a816b7b3d2034ff0c1313
Относительный путь к изображению структуры: src/main/java/ru/yandex/practicum/filmorate/sql/filmorate_db_structure.png
![](src/main/resources/filmorate_db_structure.png)

## Примеры запросов для основных операций приложения

Получение всех фильмов:
```sql 
SELECT * 
FROM film AS f 
JOIN film_genre AS fg ON f.film_id = fg.film_id
JOIN film_likes AS fl ON f.film_id = fl.film_id
GROUP BY f.film_id
ORDER BY f.film_id
```

Добавление нового фильма:
```sql
INSERT INTO film (film_name, film_description, release_date, duration, rating_id)
VALUES ('filmName', 'filmDescription', '2022-01-01', 1, 1)
INSERT INTO film_genre (film_id, genre_id) (
VALUES ((SELECT film_id from films WHERE film_name = 'filmName' AND film_description = 'filmDescription' AND release_date = '2022-01-01' AND duration = 1 AND rating_id = 1),1),
((SELECT film_id from films WHERE film_name = 'filmName' AND film_description = 'filmDescription' AND release_date = '2022-01-01' AND duration = 1 AND rating_id = 1),2),
((SELECT film_id from films WHERE film_name = 'filmName' AND film_description = 'filmDescription' AND release_date = '2022-01-01' AND duration = 1 AND rating_id = 1), 3),
((SELECT film_id from films WHERE film_name = 'filmName' AND film_description = 'filmDescription' AND release_date = '2022-01-01' AND duration = 1 AND rating_id = 1), 4)                                            
)
;
```

Обновление фильма c id = 1:
```sql
UPDATE film
SET film_name = 'filmNameNew', film_description = 'filmDescriptionNew', release_date = '2022-01-02', duration = 2, rating_id = 2)
WHERE id = 1;

DELETE FROM film_genre WHERE film_id = 1;

INSERT INTO film_genre (film_id, genre_id) (
    VALUES ((SELECT film_id from films WHERE film_name = 'filmNameNew' AND film_description = 'filmDescriptionNew' AND release_date = '2022-01-02' AND duration = 2 AND rating_id = 2),1 ),
           ((SELECT film_id from films WHERE film_name = 'filmNameNew' AND film_description = 'filmDescriptionNew' AND release_date = '2022-01-02' AND duration = 2 AND rating_id = 2),2)
)
;

INSERT INTO film_likes(film_id, genre_id) (
    VALUES ((SELECT film_id from films WHERE film_name = 'filmNameNew' AND film_description = 'filmDescriptionNew' AND release_date = '2022-01-02' AND duration = 2 AND rating_id = 2),1),
           ((SELECT film_id from films WHERE film_name = 'filmNameNew' AND film_description = 'filmDescriptionNew' AND release_date = '2022-01-02' AND duration = 2 AND rating_id = 2),2)
)
;
```

Получить фильм по id = 1:
```sql
SELECT f.film_id,
       f.film_description,
       f.film_name,
       f.release_date,
       f.duration,
       f.rating_id,
       fg.genre_id
       fl.user_id
FROM film AS f 
JOIN film_genre AS fg ON f.film_id = fg.film_id
JOIN film_likes AS fl ON f.film_id = fl.film_id
WHERE f.film_id = 1;       
```

Удалить фильм по id = 1:
```sql
DELETE FROM film WHERE film_id = 1;
DELETE FROM film_genre WHERE film_id = 1;
DELETE FROM film_likes WHERE film_id = 1;
```

Поставить like фильму с id = 1 пользователем c id = 1:
```sql
INSERT INTO film_like (film_id, user_id)
VALUES (1, 1);
```

Удалить like фильму с id = 1 пользователем c id = 1:
```sql
DELETE FROM film_likes WHERE film_id = 1 AND user_id = 1;
```

Получить топ 10 фильмов по кол-ву лайков:
```sql
SELECT f.film_id,
       COUNT(f.user_id) AS like_count
FROM film_likes
GROUP BY f.film_id
ORDER BY like_count DESC
LIMIT 10;
```

Получить список всех пользователей:
```sql
SELECT *
FROM user AS u
JOIN user_friends AS uf ON u.user_id = uf.user_id
GROUP BY u.user_id
ORDER BY u.user_id
```

Добавить пользователя:
```sql
INSERT INTO user (email, login, name, birthday)
VALUES ('email@ya.ru', 'login', 'name', '2022-01-01');
```

Обновить пользователя c id = 1:
```sql
UPDATE user
SET email = 'email@ya.com', login = 'loginNew', name = 'nameNew', birthday = '2022-01-02')
WHERE id = 1;

DELETE FROM user_friends
WHERE user_id = 1;

INSERT INTO user_friends (user_id, friend_id) (
    VALUES ((SELECT film_id from films WHERE film_name = 'filmNameNew' AND film_description = 'filmDescriptionNew' AND release_date = '2022-01-02' AND duration = 2 AND rating_id = 2),1),
           ((SELECT film_id from films WHERE film_name = 'filmNameNew' AND film_description = 'filmDescriptionNew' AND release_date = '2022-01-02' AND duration = 2 AND rating_id = 2),2)
)
;
```

Удалить пользователя по id = 1:
```sql
DELETE FROM user WHERE user_id = 1;
DELETE FROM user_friends WHERE user_id = 1;
```

Добавление в друзья:
```sql
INSERT INTO user_friends (user_id, friend_id)
VALUES (1, 1);
```

Добавление в друзья:
```sql
INSERT INTO user_friends (user_id, friend_id)
VALUES (1, 1);
```

Удаление из друзей:
```sql
DELETE FROM user_friends WHERE user_id = 1 AND friend_id = 1;
```

Получение списка всех друзей пользователя:
```sql
SELECT user_id, friend_id
FROM user_friends
WHERE user_id = 1;
```

Получение списка друзей, общих с другим пользователем (id = 1 и 2):
```sql
SELECT friend_id
FROM user_friends
WHERE user_id = 1 and friend_id IN (SELECT friend_id
                                    FROM user_friends
                                    WHERE user_id = 2)
```

