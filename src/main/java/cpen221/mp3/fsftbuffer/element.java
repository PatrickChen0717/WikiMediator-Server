package cpen221.mp3.fsftbuffer;
import java.util.concurrent.TimeUnit;

public class element {

    public T t;
    //constructor
    public int lifetime;
    public long starttime;
    public long usedtime;

    //constructor
    public element(T t){
        this.t=t;
        this.starttime=System.currentTimeMillis();
        this.usedtime=System.currentTimeMillis();
    }

    public String getid() {
        return t.id();
    }
}
