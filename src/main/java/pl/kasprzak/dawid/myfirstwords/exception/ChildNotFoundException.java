package pl.kasprzak.dawid.myfirstwords.exception;

public class ChildNotFoundException extends RuntimeException{
    public ChildNotFoundException(String message){
        super(message);
    }
}
