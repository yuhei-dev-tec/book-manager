create table if not exists users(
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(256) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    role VARCHAR(50) NOT NULL
    )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create table if not exists book_manager (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    book_title VARCHAR(256) NOT NULL ,
    author_name VARCHAR(256) NOT NULL ,
    rating INT NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_book_user FOREIGN KEY(user_id) REFERENCES users(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
