DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `message`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `question`;


CREATE TABLE user(
    id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` varchar(64) NOT NULL DEFAULT '',
    `password` varchar(128) NOT NULL DEFAULT '',
    `salt` varchar(32) NOT NULL DEFAULT '',
    `head_url` varchar(256) NOT NULL DEFAULT '',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `question` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NULL,
    `user_id` INT NOT NULL,
    `created_date` DATETIME NOT NULL,
    `comment_count` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `date_index` (`created_date` ASC)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `message` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `fromid` INT UNSIGNED NOT NULL,
    `toid` INT UNSIGNED NOT NULL,
    `content` TEXT NULL,
    `conversation_id` INT NOT NULL,
    `created_date` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`fromid`) REFERENCES user(`id`),
    FOREIGN KEY (`toid`) REFERENCES user(`id`),
    INDEX `date_index` (`created_date` ASC)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `comment` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `content` TEXT NULL,
    `user_id` INT UNSIGNED NOT NULL,
    `created_date` DATETIME NOT NULL,
    `entity_id` INT NOT NULL,
    `entity_type` varchar(15) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES user(`id`),
    INDEX `date_index` (`created_date` ASC)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;