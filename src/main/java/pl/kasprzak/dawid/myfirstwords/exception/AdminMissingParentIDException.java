package pl.kasprzak.dawid.myfirstwords.exception;

public class AdminMissingParentIDException extends RuntimeException{
    public AdminMissingParentIDException(String message){
        super(message);
    }
}
