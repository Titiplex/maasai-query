package com.aixuniversity.maadictionary.exc;

public class DialectNullCodeException extends NullCodeException {
    public DialectNullCodeException(String message) {
        super("dialect", message);
    }
}
