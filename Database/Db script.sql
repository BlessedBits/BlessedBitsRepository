-- MySQL Script generated by MySQL Workbench
-- Tue Dec  3 20:57:54 2024
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema educational_website
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema educational_website
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `educational_website` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `educational_website` ;

-- -----------------------------------------------------
-- Table `educational_website`.`Schools`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`Schools` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC) VISIBLE,
  UNIQUE INDEX `address` (`address` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`Roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`Roles` (
  `id` INT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `role_name` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`Users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`Users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NULL DEFAULT NULL,
  `phone_number` VARCHAR(100) NULL DEFAULT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email` (`email` ASC) VISIBLE,
  UNIQUE INDEX `password` (`password` ASC) VISIBLE,
  UNIQUE INDEX `phone_number` (`phone_number` ASC) VISIBLE,
  INDEX `role_id` (`role_id` ASC) VISIBLE,
  CONSTRAINT `users_ibfk_1`
    FOREIGN KEY (`role_id`)
    REFERENCES `educational_website`.`Roles` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`Classes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`Classes` (
  `id` INT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `school_id` INT NOT NULL,
  `homeroom_teacher_id` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `school_id` (`school_id` ASC) VISIBLE,
  INDEX `fk_homeroom_teacher1_idx` (`homeroom_teacher_id` ASC) VISIBLE,
  CONSTRAINT `classes_ibfk_1`
    FOREIGN KEY (`school_id`)
    REFERENCES `educational_website`.`Schools` (`id`),
  CONSTRAINT `fk_homeroom_teacher1`
    FOREIGN KEY (`homeroom_teacher_id`)
    REFERENCES `educational_website`.`Users` (`id`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`ClassUsers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`ClassUsers` (
  `class_id` INT NOT NULL,
  `student_id` INT NOT NULL,
  PRIMARY KEY (`class_id`, `student_id`),
  INDEX `student_id` (`student_id` ASC) VISIBLE,
  CONSTRAINT `classstudents_ibfk_1`
    FOREIGN KEY (`class_id`)
    REFERENCES `educational_website`.`Classes` (`id`),
  CONSTRAINT `classstudents_ibfk_2`
    FOREIGN KEY (`student_id`)
    REFERENCES `educational_website`.`Users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`Courses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`Courses` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `school_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name` (`name` ASC, `school_id` ASC) VISIBLE,
  INDEX `school_id` (`school_id` ASC) VISIBLE,
  CONSTRAINT `courses_ibfk_2`
    FOREIGN KEY (`school_id`)
    REFERENCES `educational_website`.`Schools` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`CourseUsers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`CourseUsers` (
  `course_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`course_id`, `user_id`),
  INDEX `usert_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `coursestudents_ibfk_1`
    FOREIGN KEY (`course_id`)
    REFERENCES `educational_website`.`Courses` (`id`),
  CONSTRAINT `coursestudents_ibfk_2`
    FOREIGN KEY (`user_id`)
    REFERENCES `educational_website`.`Users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`Grades`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`Grades` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `student_id` INT NOT NULL,
  `course_id` INT NOT NULL,
  `grade` DECIMAL(4,2) NOT NULL,
  `date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `student_id` (`student_id` ASC, `course_id` ASC) VISIBLE,
  INDEX `course_id` (`course_id` ASC) VISIBLE,
  CONSTRAINT `grades_ibfk_1`
    FOREIGN KEY (`student_id`)
    REFERENCES `educational_website`.`Users` (`id`),
  CONSTRAINT `grades_ibfk_2`
    FOREIGN KEY (`course_id`)
    REFERENCES `educational_website`.`Courses` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`SchoolReviews`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`SchoolReviews` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `school_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `rating` TINYINT NOT NULL,
  `review` TEXT NULL DEFAULT NULL,
  `date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `school_id` (`school_id` ASC) VISIBLE,
  INDEX `user_id` (`user_id` ASC) VISIBLE,
  CONSTRAINT `schoolreviews_ibfk_1`
    FOREIGN KEY (`school_id`)
    REFERENCES `educational_website`.`Schools` (`id`),
  CONSTRAINT `schoolreviews_ibfk_2`
    FOREIGN KEY (`user_id`)
    REFERENCES `educational_website`.`Users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `educational_website`.`ClassCourses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`ClassCourses` (
  `class_id` INT NOT NULL,
  `course_id` INT NOT NULL,
  PRIMARY KEY (`class_id`, `course_id`),
  INDEX `fk_classcourses1_idx` (`course_id` ASC) VISIBLE,
  CONSTRAINT `fk_classcourses1`
    FOREIGN KEY (`class_id`)
    REFERENCES `educational_website`.`Classes` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_classcourses1`
    FOREIGN KEY (`course_id`)
    REFERENCES `educational_website`.`Courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `educational_website`.`Schedule`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `educational_website`.`Schedule` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `class_id` INT NOT NULL,
  `course_id` INT NOT NULL,
  `teacher_id` INT NOT NULL,
  `day_of_week` ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
  `lesson_number` INT NOT NULL,
  `room` VARCHAR(45) NULL,
  `start_time` TIME NOT NULL,
  `end_time` TIME NOT NULL,
  PRIMARY KEY (`id`, `class_id`, `course_id`, `teacher_id`),
  INDEX `fk_schedule1_idx` (`teacher_id` ASC) VISIBLE,
  INDEX `fk_schedule2_idx` (`course_id` ASC) VISIBLE,
  INDEX `fk_schedule3_idx` (`class_id` ASC) VISIBLE,
  INDEX `days_of_week` (`day_of_week` ASC) VISIBLE,
  UNIQUE INDEX `Unique1` (`class_id` ASC, `course_id` ASC, `day_of_week` ASC, `lesson_number` ASC) VISIBLE,
  CONSTRAINT `fk_schedule1`
    FOREIGN KEY (`teacher_id`)
    REFERENCES `educational_website`.`Users` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule2`
    FOREIGN KEY (`course_id`)
    REFERENCES `educational_website`.`Courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule3`
    FOREIGN KEY (`class_id`)
    REFERENCES `educational_website`.`Classes` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
