CREATE DATABASE IF NOT EXISTS playlist_master;
USE playlist_master;
CREATE USER IF NOT EXISTS 'pm'@'localhost' IDENTIFIED BY 'pmaster';
GRANT ALL PRIVILEGES ON playlist_master.* TO 'pm'@'localhost';
FLUSH PRIVILEGES;