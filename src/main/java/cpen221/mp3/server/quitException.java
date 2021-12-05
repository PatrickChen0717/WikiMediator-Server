package cpen221.mp3.server;

public class quitException extends Throwable {
    public quitException(String errorMsg) {
        System.out.println(errorMsg);
    }

}
