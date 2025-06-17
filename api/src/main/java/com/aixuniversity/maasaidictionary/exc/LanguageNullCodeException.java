package main.java.com.aixuniversity.maasaidictionary.exc;

public class LanguageNullCodeException extends NullCodeException {
    public LanguageNullCodeException(String message) {
        super("language", message);
    }
}
