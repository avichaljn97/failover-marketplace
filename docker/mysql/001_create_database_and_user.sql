CREATE DATABASE IF NOT EXISTS logs;

CREATE USER IF NOT EXISTS 'router_app'@'%' IDENTIFIED BY 'routerpass';
GRANT ALL PRIVILEGES ON logs.* TO 'router_app'@'%';
FLUSH PRIVILEGES;
