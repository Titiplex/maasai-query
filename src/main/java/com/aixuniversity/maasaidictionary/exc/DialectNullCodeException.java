package main.java.com.aixuniversity.maasaidictionary.exc;

public class DialectNullCodeException extends NullCodeException {
    public DialectNullCodeException(String message) {
        super("dialect", message);
    }
}
