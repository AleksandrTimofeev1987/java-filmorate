DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS user_friends CASCADE;
DROP TABLE IF EXISTS film_likes CASCADE;

CREATE TABLE mpa
(
    mpa_id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    rating varchar(40)
);

CREATE TABLE films
(
    film_id          int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    film_name        varchar(40) NOT NULL,
    film_description varchar(200),
    release_date     date,
    duration         int,
    rate             int,
    mpa_id           int,
    FOREIGN KEY (mpa_id) REFERENCES mpa (MPA_ID)
);

CREATE TABLE genres
(
    genre_id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    genre    varchar(40)
);

CREATE TABLE film_genre
(
    film_id  int REFERENCES films (film_id),
    genre_id int REFERENCES genres (genre_id),
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (FILM_ID),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)
);

CREATE TABLE users
(
    user_id  int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email    varchar(40) NOT NULL,
    login    varchar(40) NOT NULL,
    name     varchar(40) NOT NULL,
    birthday date        NOT NULL
);

CREATE TABLE user_friends
(
    user_id   int REFERENCES users (USER_ID),
    friend_id int REFERENCES users (USER_ID),
    is_confirmed boolean DEFAULT false,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (friend_id) REFERENCES users (user_id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE film_likes
(
    film_id   int REFERENCES films (film_id),
    user_id int REFERENCES users (USER_ID),
    FOREIGN KEY (film_id) REFERENCES films (film_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    PRIMARY KEY (film_id, user_id)
);