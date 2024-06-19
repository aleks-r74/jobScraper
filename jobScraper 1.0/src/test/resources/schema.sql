DROP TABLE IF EXISTS jobs;

CREATE TABLE IF NOT EXISTS jobs (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(128) NOT NULL,
  company VARCHAR(128) NOT NULL,
  description VARCHAR(4096) NOT NULL,
  url VARCHAR(2048) NOT NULL,
  notes VARCHAR(1024) NULL,
  time_stamp DATETIME NOT NULL,
  applied DATETIME NULL
);

-- Create users table
CREATE TABLE users (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL
);

-- Create authorities table
CREATE TABLE authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_username FOREIGN KEY(username) REFERENCES users(username),
    CONSTRAINT pk_authorities PRIMARY KEY (username, authority)
);



