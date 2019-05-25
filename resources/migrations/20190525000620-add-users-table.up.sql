CREATE TABLE IF NOT EXISTS users
(id VARCHAR(20) PRIMARY KEY,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30),
 admin BOOLEAN,
 last_login TIMESTAMP,
 is_active BOOLEAN,
 pass VARCHAR(300));

--;;

 INSERT INTO users (id, first_name, last_name, email) VALUES ('bob', 'Bob', 'Bobberton', 'bob@bobberton.org')
