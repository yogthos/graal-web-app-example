CREATE TABLE IF NOT EXISTS users
(userid VARCHAR(20) PRIMARY KEY,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30),
 admin BOOLEAN,
 last_login TIMESTAMP,
 is_active BOOLEAN,
 pass VARCHAR(300));

--;;
-- password: secret
 INSERT INTO users (userid, first_name, last_name, email, pass)
 VALUES ('bob', 'Bob', 'Bobberton', 'bob@bobberton.org', 'bcrypt+sha512$dd65ddafab09f838e2ef6a21291dfafc$12$9f31670c13a0a84454ff398cfdf19aeb5f7e909baa4c7825')
