/*
 Navicat Premium Data Transfer

 Source Server         : pms
 Source Server Type    : MySQL
 Source Server Version : 50730 (5.7.30)
 Source Host           : localhost:3306
 Source Schema         : playlist_master

 Target Server Type    : MySQL
 Target Server Version : 50730 (5.7.30)
 File Encoding         : 65001

 Date: 26/08/2023 12:14:34
*/

create database if not exists playlist_master;

use playlist_master;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

create user if not exists 'pms'@'localhost' identified by 'pms';
grant all privileges on playlist_master.* to 'pms'@'localhost';
flush privileges;

-- ----------------------------
-- Table structure for tb_bilibili_resource
-- ----------------------------
DROP TABLE IF EXISTS `tb_bilibili_resource`;
CREATE TABLE `tb_bilibili_resource`
(
    `id`          int(11)                          NOT NULL AUTO_INCREMENT,
    `pms_song_id` int(11)                          NOT NULL,
    `aid`         varchar(50) CHARACTER SET latin1 NOT NULL COMMENT 'The aid of resource in bilibili.',
    `bvid`        varchar(50) CHARACTER SET latin1 NOT NULL COMMENT 'The bvid of resource in bilibili.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `pms_song_id` (`pms_song_id`) USING BTREE,
    CONSTRAINT `tb_bilibili_resource_ibfk_1` FOREIGN KEY (`pms_song_id`) REFERENCES `tb_pms_song` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='Store basic resource in bilibili.';

-- ----------------------------
-- Table structure for tb_ncm_song
-- ----------------------------
DROP TABLE IF EXISTS `tb_ncm_song`;
CREATE TABLE `tb_ncm_song`
(
    `id`          int(11)                          NOT NULL AUTO_INCREMENT,
    `pms_song_id` int(11)                          NOT NULL,
    `ncm_id`      varchar(50) CHARACTER SET latin1 NOT NULL COMMENT 'The id of song in ncm.',
    `mv_id`       varchar(50) CHARACTER SET latin1 NOT NULL COMMENT 'The mv id of song in ncm.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `pms_song_id` (`pms_song_id`) USING BTREE,
    CONSTRAINT `tb_ncm_song_ibfk_1` FOREIGN KEY (`pms_song_id`) REFERENCES `tb_pms_song` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='The song in ncm.';

-- ----------------------------
-- Table structure for tb_playlist_song
-- ----------------------------
DROP TABLE IF EXISTS `tb_playlist_song`;
CREATE TABLE `tb_playlist_song`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT,
    `fk_playlist_id` int(11) NOT NULL,
    `fk_song_id`     int(11) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key` (`fk_song_id`, `fk_playlist_id`),
    KEY `fk_playlist_id` (`fk_playlist_id`),
    CONSTRAINT `tb_playlist_song_ibfk_1` FOREIGN KEY (`fk_playlist_id`) REFERENCES `tb_pms_playlist` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `tb_playlist_song_ibfk_2` FOREIGN KEY (`fk_song_id`) REFERENCES `tb_pms_song` (`id`) ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Map playlist and song.';

-- ----------------------------
-- Table structure for tb_pms_detail_playlist
-- ----------------------------
DROP TABLE IF EXISTS `tb_pms_detail_playlist`;
CREATE TABLE `tb_pms_detail_playlist`
(
    `id`          int(11)                          NOT NULL AUTO_INCREMENT,
    `intro`       varchar(500) DEFAULT '' COMMENT 'The description of this playlist.',
    `create_time` varchar(50) CHARACTER SET latin1 NOT NULL COMMENT 'Create time of playlist in pms.',
    `update_time` varchar(50) CHARACTER SET latin1 NOT NULL COMMENT 'Update time of playlist in pms.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key` (`intro`, `create_time`, `update_time`),
    CONSTRAINT `tb_pms_detail_playlist_ibfk_1` FOREIGN KEY (`id`) REFERENCES `tb_pms_playlist` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Detail playlist in pms.';

-- ----------------------------
-- Table structure for tb_pms_playlist
-- ----------------------------
DROP TABLE IF EXISTS `tb_pms_playlist`;
CREATE TABLE `tb_pms_playlist`
(
    `id`          int(11)     NOT NULL AUTO_INCREMENT,
    `creator_id`  int(11)     NOT NULL,
    `name`        varchar(50) NOT NULL,
    `cover`       varchar(500) CHARACTER SET latin1 DEFAULT NULL,
    `songs_count` int(11)     NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='The playlists for pms.';

-- ----------------------------
-- Table structure for tb_pms_singer
-- ----------------------------
DROP TABLE IF EXISTS `tb_pms_singer`;
CREATE TABLE `tb_pms_singer`
(
    `id`     int(11)     NOT NULL AUTO_INCREMENT,
    `type`   int(11)     NOT NULL COMMENT 'Which platform this singer belongs to.',
    `name`   varchar(50) NOT NULL COMMENT 'The name of this singer.',
    `avatar` varchar(300) CHARACTER SET latin1 DEFAULT NULL COMMENT 'The avatar of this singer.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key` (`type`, `name`, `avatar`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='The singer in pms.';

-- ----------------------------
-- Table structure for tb_pms_song
-- ----------------------------
DROP TABLE IF EXISTS `tb_pms_song`;
CREATE TABLE `tb_pms_song`
(
    `id`            int(11)      NOT NULL AUTO_INCREMENT,
    `name`          varchar(100) NOT NULL COMMENT 'The name of this song.',
    `cover`         varchar(500) CHARACTER SET latin1 DEFAULT NULL COMMENT 'The cover of this song.',
    `pay_play`      int(11)      NOT NULL COMMENT 'Whether need vip to play this song.',
    `is_taken_down` int(11)      NOT NULL COMMENT 'Whether this song is taken down.',
    `type`          int(11)      NOT NULL COMMENT 'Which platform this song belogns to.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key` (`name`, `cover`, `pay_play`, `is_taken_down`, `type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='The pms song.';

-- ----------------------------
-- Table structure for tb_pms_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_pms_user`;
CREATE TABLE `tb_pms_user`
(
    `id`             int(11)     NOT NULL AUTO_INCREMENT COMMENT 'User id and primary key in pms user table.',
    `name`           varchar(50) NOT NULL COMMENT 'User name.',
    `pass`           char(96)    NOT NULL COMMENT 'The encoded password with argon2.',
    `role`           varchar(20) NOT NULL COMMENT 'The role of this user.',
    `email`          varchar(40) COMMENT 'The email of user.',
    `phone`          varchar(20) COMMENT 'The phone number of user.',
    `enabled`        bool        NOT NULL COMMENT 'Whether this user is enabled or not.',
    `intro`          varchar(300)                       DEFAULT '',
    `avatar`         varchar(500) CHARACTER SET latin1  DEFAULT NULL,
    `bg_pic`         varchar(500) CHARACTER SET latin1  DEFAULT NULL,
    `qqmusic_id`     varchar(50) CHARACTER SET latin1   DEFAULT NULL,
    `qqmusic_cookie` varchar(2000) CHARACTER SET latin1 DEFAULT NULL,
    `ncm_id`         varchar(50) CHARACTER SET latin1   DEFAULT NULL,
    `ncm_cookie`     varchar(3000) CHARACTER SET latin1 DEFAULT NULL,
    `bilibili_id`    varchar(50) CHARACTER SET latin1   DEFAULT NULL,
    `bili_cookie`    varchar(1000) CHARACTER SET latin1 DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key` (`name`, `intro`, `avatar`, `bg_pic`, `qqmusic_id`, `ncm_id`, `bilibili_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='User in pms.';

-- ----------------------------
-- Table structure for tb_qqmusic_song
-- ----------------------------
DROP TABLE IF EXISTS `tb_qqmusic_song`;
CREATE TABLE `tb_qqmusic_song`
(
    `id`          int(11)                          NOT NULL AUTO_INCREMENT,
    `pms_song_id` int(11)                          NOT NULL,
    `song_id`     varchar(50) CHARACTER SET latin1 NOT NULL,
    `song_mid`    varchar(50) CHARACTER SET latin1 NOT NULL,
    `media_mid`   varchar(50) CHARACTER SET latin1 NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `pms_song_id` (`pms_song_id`) USING BTREE,
    CONSTRAINT `tb_qqmusic_song_ibfk_1` FOREIGN KEY (`pms_song_id`) REFERENCES `tb_pms_song` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Store song in qq music.';

-- ----------------------------
-- Table structure for tb_song_singer
-- ----------------------------
DROP TABLE IF EXISTS `tb_song_singer`;
CREATE TABLE `tb_song_singer`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `fk_song_id`   int(11) NOT NULL COMMENT 'The foreign key to pms song.',
    `fk_singer_id` int(11) NOT NULL COMMENT 'The foreign key for pms singer.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key` (`fk_song_id`, `fk_singer_id`),
    KEY `fk_singer_id` (`fk_singer_id`),
    CONSTRAINT `tb_song_singer_ibfk_1` FOREIGN KEY (`fk_song_id`) REFERENCES `tb_pms_song` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `tb_song_singer_ibfk_2` FOREIGN KEY (`fk_singer_id`) REFERENCES `tb_pms_singer` (`id`) ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Mapping song and singers.';

SET FOREIGN_KEY_CHECKS = 1;
