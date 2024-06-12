package pl.kasprzak.dawid.myfirstwords.exception;

public class MilestoneNotFoundException extends RuntimeException{
    public MilestoneNotFoundException(String message){
        super(message);
    }
}
