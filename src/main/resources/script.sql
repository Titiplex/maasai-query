DROP DATABASE `maa`;
CREATE DATABASE `maa`;
USE `maa`;

-- Table for languages
CREATE TABLE `Language`
(
    `id`   INT         NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(10) NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for dialects
CREATE TABLE `Dialect`
(
    `id`          INT          NOT NULL AUTO_INCREMENT,
    `dialectName` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table Vocabulary
CREATE TABLE `Vocabulary`
(
    `id`        INT          NOT NULL AUTO_INCREMENT,
    `entry`     VARCHAR(255) NOT NULL,
    `ipa`       VARCHAR(255) NOT NULL,
    `syllables` TEXT NOT NULL,
    `homonymIndex` INT NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for POS
CREATE TABLE `PartOfSpeech`
(
    `id`  INT         NOT NULL AUTO_INCREMENT,
    `pos` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for meanings
CREATE TABLE `Meaning`
(
    `id`           INT  NOT NULL AUTO_INCREMENT,
    `vocabularyId` INT  NOT NULL,
    `definition`   TEXT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_meaning_vocab` (`vocabularyId`),
    CONSTRAINT `fk_meaning_vocab`
        FOREIGN KEY (`vocabularyId`) REFERENCES `Vocabulary` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for examples
CREATE TABLE `Example`
(
    `id`           INT  NOT NULL AUTO_INCREMENT,
    `vocabularyId` INT  NOT NULL,
    `example`      TEXT NOT NULL,
    `gloss`        TEXT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_example_vocab` (`vocabularyId`),
    CONSTRAINT `fk_example_vocab`
        FOREIGN KEY (`vocabularyId`) REFERENCES `Vocabulary` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for linking between POS and Vocabulary (many‑to‑many)
CREATE TABLE `VocabularyPartOfSpeech`
(
    `vocabularyId` INT NOT NULL,
    `posId`        INT NOT NULL,
    PRIMARY KEY (`vocabularyId`, `posId`),
    INDEX `idx_vps_vocab` (`vocabularyId`),
    INDEX `idx_vps_pos` (`posId`),
    CONSTRAINT `fk_vps_vocab`
        FOREIGN KEY (`vocabularyId`) REFERENCES `Vocabulary` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_vps_pos`
        FOREIGN KEY (`posId`) REFERENCES `PartOfSpeech` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for linked words (self‑referential many‑to‑many)
CREATE TABLE `VocabularyLinked`
(
    `vocabularyId`       INT NOT NULL,
    `linkedVocabularyId` INT NOT NULL,
    PRIMARY KEY (`vocabularyId`, `linkedVocabularyId`),
    INDEX `idx_vl_vocab` (`vocabularyId`),
    INDEX `idx_vl_linked` (`linkedVocabularyId`),
    CONSTRAINT `fk_vl_vocab`
        FOREIGN KEY (`vocabularyId`) REFERENCES `Vocabulary` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_vl_linked`
        FOREIGN KEY (`linkedVocabularyId`) REFERENCES `Vocabulary` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for ExampleLanguage (liaison Example ⇄ Language)
CREATE TABLE `ExampleLanguage`
(
    `exId`   INT NOT NULL,
    `langId` INT NOT NULL,
    PRIMARY KEY (`exId`, `langId`),
    INDEX `idx_exlang_ex` (`exId`),
    INDEX `idx_exlang_lang` (`langId`),
    CONSTRAINT `fk_exlang_example`
        FOREIGN KEY (`exId`) REFERENCES `Example` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_exlang_language`
        FOREIGN KEY (`langId`) REFERENCES `Language` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for MeaningLanguage (liaison Meaning ⇄ Language)
CREATE TABLE `MeaningLanguage`
(
    `meaningId` INT NOT NULL,
    `langId`    INT NOT NULL,
    PRIMARY KEY (`meaningId`, `langId`),
    INDEX `idx_meanlang_mean` (`meaningId`),
    INDEX `idx_meanlang_lang` (`langId`),
    CONSTRAINT `fk_meanlang_meaning`
        FOREIGN KEY (`meaningId`) REFERENCES `Meaning` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_meanlang_language`
        FOREIGN KEY (`langId`) REFERENCES `Language` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for ExampleDialect (liaison Example ⇄ Dialect)
CREATE TABLE `ExampleDialect`
(
    `exampleId` INT NOT NULL,
    `dialectId` INT NOT NULL,
    PRIMARY KEY (`exampleId`, `dialectId`),
    INDEX `idx_exdial_ex` (`exampleId`),
    INDEX `idx_exdial_dial` (`dialectId`),
    CONSTRAINT `fk_exdial_example`
        FOREIGN KEY (`exampleId`) REFERENCES `Example` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_exdial_dialect`
        FOREIGN KEY (`dialectId`) REFERENCES `Dialect` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for MeaningDialect (liaison Meaning ⇄ Dialect)
CREATE TABLE `MeaningDialect`
(
    `meaningId` INT NOT NULL,
    `dialectId` INT NOT NULL,
    PRIMARY KEY (`meaningId`, `dialectId`),
    INDEX `idx_meandial_mean` (`meaningId`),
    INDEX `idx_meandial_dial` (`dialectId`),
    CONSTRAINT `fk_meandial_meaning`
        FOREIGN KEY (`meaningId`) REFERENCES `Meaning` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_meandial_dialect`
        FOREIGN KEY (`dialectId`) REFERENCES `Dialect` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table for VocabularyDialect (liaison Vocabulary ⇄ Dialect)
CREATE TABLE `VocabularyDialect`
(
    `vocabularyId`     INT NOT NULL,
    `dialectId` INT NOT NULL,
    PRIMARY KEY (`vocabularyId`, `dialectId`),
    INDEX `idx_vocdial_voc` (`vocabularyId`),
    INDEX `idx_vocdial_dial` (`dialectId`),
    CONSTRAINT `fk_vocdial_vocab`
        FOREIGN KEY (`vocabularyId`) REFERENCES `Vocabulary` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_vocdial_dialect`
        FOREIGN KEY (`dialectId`) REFERENCES `Dialect` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
