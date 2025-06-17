package main.java.com.aixuniversity.maasaidictionary.exc;

public class DictionaryException extends RuntimeException {
    public DictionaryException(String type, String message) {
        super("Dictionary exception: [" + type+ "]\t" + message);
    }
}
