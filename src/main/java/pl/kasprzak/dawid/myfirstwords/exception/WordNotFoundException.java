package pl.kasprzak.dawid.myfirstwords.exception;

public class WordNotFoundException extends RuntimeException{
    public WordNotFoundException(String message){
        super(message);
    }
}
