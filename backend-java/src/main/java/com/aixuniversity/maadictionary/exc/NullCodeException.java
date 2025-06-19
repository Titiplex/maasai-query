package com.aixuniversity.maadictionary.exc;

public class NullCodeException extends DictionaryException {
    public NullCodeException(String type, String message) {
        super(type + " code missing", "\"" + message + "\"");
    }
}
