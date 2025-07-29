# DROP DATABASE `maa`;
# CREATE DATABASE `maa`;
# USE `maa`;

-- Metadata

CREATE TABLE `ImportStatus`
(
    `source`         VARCHAR(100) PRIMARY KEY,
    `last_import_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `payload_hash`   CHAR(64)  NULL -- SHA-256 of the raw file/response
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE VocabularyAudit
(
    `vocabulary_id` INT PRIMARY KEY,
    `last_modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_indexed`  TIMESTAMP NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Linguistics related Tables

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
    `id`             INT          NOT NULL AUTO_INCREMENT,
    `entry`          VARCHAR(255) NOT NULL,
    `ipa`            VARCHAR(255) NOT NULL,
    `syll_count`     INT                   DEFAULT 0,
    `syll_pattern`   Text,
    `homonymIndex`   INT          NOT NULL DEFAULT 1,
    `specifications` VARCHAR(255),
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
    `vocabularyId` INT NOT NULL,
    `dialectId`    INT NOT NULL,
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

-- Indexation Tables

-- Table Phoneme
CREATE TABLE `Phoneme`
(
    `id`   INT         NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(10) NOT NULL,
    `ipa`  VARCHAR(10) NOT NULL,
    `freq` INT         NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

-- Table Category
CREATE TABLE `Category`
(
    `id`   INT         NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `abbr` VARCHAR(10),
    `freq` INT         NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

-- Table de jointure PhonemeCategory
CREATE TABLE `PhonemeCategory`
(
    `phoneme_id`  INT NOT NULL,
    `category_id` INT NOT NULL,
    PRIMARY KEY (`phoneme_id`, `category_id`),
    INDEX `idx_phoncat_phon` (`phoneme_id`),
    INDEX `idx_phoncat_cat` (`category_id`),
    CONSTRAINT `fk_phoncat_phon`
        FOREIGN KEY (`phoneme_id`) REFERENCES `Phoneme` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_phoncat_cat`
        FOREIGN KEY (`category_id`) REFERENCES `Category` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table VocabularyPhoneme
CREATE TABLE `VocabularyPhoneme`
(
    `id`            INT NOT NULL AUTO_INCREMENT,
    `vocabularyId`  INT NOT NULL,
    `phonemeId`     INT NOT NULL,
    `position`      INT NOT NULL,
    `syllableIndex` INT NOT NULL,
    `posSyllable`   INT NOT NULL,
    INDEX `idx_vocphon_voc` (`vocabularyId`),
    INDEX `idx_vocphon_phon` (`phonemeId`),
    INDEX `idx_phon_syll` (`phonemeId`, `syllableIndex`),
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_vocphon_vocab`
        FOREIGN KEY (`vocabularyId`) REFERENCES `Vocabulary` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_vocphon_phon`
        FOREIGN KEY (`phonemeId`) REFERENCES `Phoneme` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- Table VocabularyPhonemeCategory
CREATE TABLE `VocabularyPhonemeCategory`
(
    `vocab_phoneme_id` INT NOT NULL,
    `category_id`      INT NOT NULL,
    `syllableIndex`    INT NOT NULL,
    `posSyllable`      INT NOT NULL,
    INDEX `idx_cat_syll_pos` (`category_id`, `syllableIndex`, `posSyllable`),
    PRIMARY KEY (`vocab_phoneme_id`, `category_id`),
    CONSTRAINT `fk_vpc_vp`
        FOREIGN KEY (`vocab_phoneme_id`) REFERENCES `VocabularyPhoneme` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_vpc_cat`
        FOREIGN KEY (`category_id`) REFERENCES `Category` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;