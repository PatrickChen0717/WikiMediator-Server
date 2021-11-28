package cpen221.mp3.fsftbuffer;
import java.util.concurrent.TimeUnit;

public class element implements Bufferable{
    public String id;
    public int lifetime;
    public int starttime;
    public int usedtime;

    //constructor
    public element(String id){
        this.id=id;
        this.starttime=(int)System.nanoTime();
        this.usedtime=(int)System.nanoTime();
    }

    @Override
    public String id() {
        return id;
    }
}
