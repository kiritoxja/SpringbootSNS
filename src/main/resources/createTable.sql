DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `message`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `question`;
 DROP TABLE IF EXISTS `feed`;


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
    `from_id` INT UNSIGNED NOT NULL,
    `to_id` INT UNSIGNED NOT NULL,
    `content` TEXT NULL,
    `conversation_id` VARCHAR(255) NOT NULL,
    `created_date` DATETIME NOT NULL,
    `has_read` INT DEFAULT 0 NOT NULL,
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
    `entity_type` INT NOT NULL,
    `status` INT DEFAULT 0,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES user(`id`),
    INDEX `date_index` (`created_date` ASC)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `feed` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `created_date` DATETIME NULL,
    `user_id` INT NULL,
    `data` TINYTEXT NULL,
    `type` INT NULL,
    PRIMARY KEY (`id`),
    INDEX `user_index` (`user_id` ASC)
)ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;