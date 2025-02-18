-- Table Vocabulary
CREATE TABLE Vocabulary
(
    id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entry VARCHAR(255) NOT NULL
);

-- Table PartOfSpeech
CREATE TABLE PartOfSpeech
(
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vocabulary_id INT         NOT NULL,
    pos           VARCHAR(50) NOT NULL,
    FOREIGN KEY (vocabulary_id) REFERENCES Vocabulary (id)
);

-- Table Meaning
CREATE TABLE Meaning
(
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vocabulary_id INT         NOT NULL,
    definition    TEXT        NOT NULL,
    language      VARCHAR(10) NOT NULL,
    FOREIGN KEY (vocabulary_id) REFERENCES Vocabulary (id)
);

-- Table Example
CREATE TABLE Example
(
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vocabulary_id INT         NOT NULL,
    example       TEXT        NOT NULL,
    gloss         TEXT,
    language      VARCHAR(10) NOT NULL,
    FOREIGN KEY (vocabulary_id) REFERENCES Vocabulary (id)
);

-- Table Linked Words
CREATE TABLE VocabularyLinked
(
    vocabulary_id        INT NOT NULL,
    linked_vocabulary_id INT NOT NULL,
    PRIMARY KEY (vocabulary_id, linked_vocabulary_id),
    FOREIGN KEY (vocabulary_id) REFERENCES Vocabulary (id),
    FOREIGN KEY (linked_vocabulary_id) REFERENCES Vocabulary (id)
);
