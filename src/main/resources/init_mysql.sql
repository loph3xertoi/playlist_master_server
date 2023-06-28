CREATE DATABASE IF NOT EXISTS playlist_master;
USE playlist_master;
CREATE USER IF NOT EXISTS 'pms'@'localhost' IDENTIFIED BY 'pms';
GRANT ALL PRIVILEGES ON playlist_master.* TO 'pms'@'localhost';
FLUSH PRIVILEGES;