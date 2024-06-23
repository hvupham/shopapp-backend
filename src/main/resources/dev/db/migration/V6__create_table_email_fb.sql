CREATE TABLE emails (
    id INT AUTO_INCREMENT PRIMARY KEY,
    picture VARCHAR(255) DEFAULT '',
    name VARCHAR(255) DEFAULT '',
    email VARCHAR(255) DEFAULT ''
);
ALTER TABLE emails
    ADD COLUMN createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users
    ADD COLUMN email VARCHAR(255) DEFAULT '';

CREATE TABLE facebooks (
   id INT AUTO_INCREMENT PRIMARY KEY,
   facebook_id VARCHAR(255) DEFAULT '',
   name VARCHAR(255) DEFAULT '',
   email VARCHAR(255) DEFAULT ''
);