CREATE DATABASE IF NOT EXISTS logdb;

CREATE USER IF NOT EXISTS 'loguser'@'%' IDENTIFIED BY 'logpass';
GRANT ALL PRIVILEGES ON logdb.* TO 'loguser'@'%';
FLUSH PRIVILEGES;

USE logdb;

CREATE TABLE IF NOT EXISTS logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    service VARCHAR(255),
    endpoint VARCHAR(255),
    status VARCHAR(50),
    method VARCHAR(10),
    severity VARCHAR(50),
    payload TEXT,
    response TEXT,
    timestamp VARCHAR(100)
);
