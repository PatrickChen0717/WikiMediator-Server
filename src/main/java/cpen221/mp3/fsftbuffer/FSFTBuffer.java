package cpen221.mp3.fsftbuffer;

import java.util.ArrayList;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;


public class FSFTBuffer<T extends Bufferable> {

    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    private int capacity;
    private int timeout;
    private ArrayList<element> arraylist=new ArrayList<>();

    /**
     * Create a buffer with a fixed capacity and a timeout value.
     * Objects in the buffer that have not been refreshed within the
     * timeout period are removed from the cache.
     *
     * @param capacity the number of objects the buffer can hold
     * @param timeout  the duration, in seconds, an object should
     *                 be in the buffer before it times out
     */
    public FSFTBuffer(int capacity, int timeout) {
        this.capacity=capacity;
        this.timeout=timeout;
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DSIZE, DTIMEOUT);
    }

    public void checklifetime(){
        arraylist.stream().filter(x->x.lifetime+x.starttime>=(int)System.nanoTime());
    }
    /**
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     */
    public boolean put(element t) {
        checklifetime();
        t.lifetime=timeout;
        t.usedtime=(int)System.nanoTime();

        //check if it's possible to add elements
        if(arraylist.size()<capacity){
            arraylist.add(t);
            return true;
        }
        else if(arraylist.size()==capacity){
            int temp=arraylist.get(0).usedtime;
            int index=0;
            for(int i=0;i<arraylist.size();i++){
                if(temp>arraylist.get(i).usedtime){
                    index=i;
                }
            }
            arraylist.remove(index);
            arraylist.add(t);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    public element get(String id) throws ObjectNotFoundException {
        checklifetime();

        int index=-1;
        try{
            for(int i=0;i<arraylist.size();i++){
                if(id.equals(arraylist.get(i).id)){
                    index=i;
                }
            }
            if(index==-1){
                throw new ObjectNotFoundException("Object Not Found Exception");
            }
        }
        catch(ObjectNotFoundException e){

        }
        //update use time


        int timeinSecond=(int)System.nanoTime();
        arraylist.get(index).lifetime=arraylist.get(index).lifetime+timeinSecond;

        return arraylist.get(index);

    }

    /**
     * Update the last refresh time for the object with the provided id.
     * This method is used to mark an object as "not stale" so that its
     * timeout is delayed.
     *
     * @param id the identifier of the object to "touch"
     * @return true if successful and false otherwise
     */
    public boolean touch(String id) {
        checklifetime();

        int count= arraylist.stream().map(x->x.id()).filter(x->x.equals(id)).collect(toList()).size();

        if(count!=1){
            return false;
        }
        else{
            int index=0;
            for(int i=0;i<arraylist.size();i++){
                if(id.equals(arraylist.get(i).id)){
                    index=i;
                }
            }
            int timeinSecond=(int)System.nanoTime();
            arraylist.get(index).lifetime=arraylist.get(index).lifetime+timeinSecond;
            arraylist.get(index).usedtime=(int)System.nanoTime();

            return true;
        }

    }

    /**
     * Update an object in the buffer.
     * This method updates an object and acts like a "touch" to
     * renew the object in the cache.
     *
     * @param t the object to update
     * @return true if successful and false otherwise
     */
    public boolean update(element t) throws ObjectNotFoundException{
        checklifetime();
        int count=0;
        int index=-1;
        try{
            for(int i=0;i<arraylist.size();i++){
                if(t.id().equals(arraylist.get(i).id)){
                    index=i;
                    count++;
                }
            }
            if(index==-1||count>1){
                throw new ObjectNotFoundException("Object Not Found Exception");
            }
        }
        catch(ObjectNotFoundException e){
            return false;
        }
        arraylist.get(index).usedtime=(int)System.nanoTime();
        return true;
    }


}
