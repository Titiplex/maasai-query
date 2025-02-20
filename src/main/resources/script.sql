-- Table for languages
CREATE TABLE Language
(
    id   INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL
);

-- Table for POS
CREATE TABLE PartOfSpeech
(
    id  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pos VARCHAR(50) NOT NULL
);

-- Table Vocabulary
CREATE TABLE Vocabulary
(
    id    INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entry VARCHAR(255) NOT NULL
);

-- Table for linking between POS and voc
CREATE TABLE VocabularyPartOfSpeech
(
    vocabularyId INT NOT NULL,
    posId      INT NOT NULL,
    PRIMARY KEY (vocabularyId, posId),
    FOREIGN KEY (vocabularyId) REFERENCES Vocabulary (id),
    FOREIGN KEY (posId) REFERENCES PartOfSpeech (id)
);

-- Table for meanings
CREATE TABLE Meaning
(
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vocabularyId INT  NOT NULL,
    definition    TEXT NOT NULL,
    languageId   INT  NOT NULL,
    FOREIGN KEY (vocabularyId) REFERENCES Vocabulary (id),
    FOREIGN KEY (languageId) REFERENCES Language (id)
);

-- Table for examples
CREATE TABLE Example
(
    id            INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vocabularyId INT  NOT NULL,
    example       TEXT NOT NULL,
    gloss         TEXT,
    languageId   INT  NOT NULL,
    FOREIGN KEY (vocabularyId) REFERENCES Vocabulary (id),
    FOREIGN KEY (languageId) REFERENCES Language (id)
);

-- Table for Linked words
CREATE TABLE VocabularyLinked
(
    vocabularyId        INT NOT NULL,
    linkedVocabularyId INT NOT NULL,
    PRIMARY KEY (vocabularyId, linkedVocabularyId),
    FOREIGN KEY (vocabularyId) REFERENCES Vocabulary (id),
    FOREIGN KEY (linkedVocabularyId) REFERENCES Vocabulary (id)
);
