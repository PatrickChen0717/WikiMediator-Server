package cpen221.mp3.fsftbuffer;
import java.util.concurrent.TimeUnit;

public class element <T extends Bufferable>{

    /**
     * t:   contains the id of this element
     * lifetiem: the time this element will last in buffer in seconds
     * starttime: the time that this element was added into the buffer
     * usedtime: the time that this element was last used
     */
    private T t;
    private int lifetime;
    public long starttime;
    public long usedtime;


    /**
     * create an element with a t, this element exists as element in buffer cache
     * @param t an object contains id
     */
    public element(T t){
        this.t=t;
    }

    /**
     * @return the id of t
     */
    public String getid() {
        return t.id();
    }

    /**
     * @return the field object t of type T
     */
    public T getT() {
        return this.t;
    }

    /**
     * replace the private field t with new parameter t1
     *
     * @param T1 an object contains id
     */
    public void repalceT(T T1){
        this.t=T1;
    }

    /**
     *@return the time the element will last before deleting in seconds
     */
    public int getlifetime(){
        return this.lifetime;
    }

    /**
     * update lifetime to param time
     * @param time how long the element will last in second
     */
    public void changelifetime(int time){
        this.lifetime=time;
    }

}
