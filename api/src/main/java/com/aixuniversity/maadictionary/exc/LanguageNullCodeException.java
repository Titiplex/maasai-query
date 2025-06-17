package com.aixuniversity.maadictionary.exc;

public class LanguageNullCodeException extends NullCodeException {
    public LanguageNullCodeException(String message) {
        super("language", message);
    }
}
