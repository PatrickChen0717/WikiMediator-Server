package cpen221.mp3.fsftbuffer;

import java.util.ArrayList;
import static java.util.stream.Collectors.toList;
import java.util.concurrent.locks.Lock;


public class FSFTBuffer<T extends Bufferable> {

    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    private Object lock=new Object();
    private Object lock1=new Object();

    private int capacity;
    private int timeout;
    public ArrayList<element> arraylist=new ArrayList<>();

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
        synchronized (lock){
            arraylist= (ArrayList<element>) arraylist.stream().filter(x->x.lifetime>=(System.currentTimeMillis()-x.starttime)/1000).collect(toList());
        }
    }
    /**
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     */
    public boolean put(T t) {
        checklifetime();
        synchronized (lock1){
            element newelement=new element((cpen221.mp3.fsftbuffer.T) t);
            newelement.lifetime=this.timeout;
            newelement.usedtime=System.currentTimeMillis();

            //check if it's possible to add elements
            if(arraylist.size()<capacity){
                arraylist.add(newelement);
                System.out.println(arraylist.size());
                System.out.println("element added");
                return true;
            }
            else if(arraylist.size()==capacity){
                long temp=arraylist.get(0).usedtime;
                int index=0;
                for(int i=0;i<arraylist.size();i++){
                    if(temp>arraylist.get(i).usedtime){
                        index=i;
                    }
                }
                arraylist.remove(index);
                arraylist.add(newelement);
                return true;
            }
            else {
                return false;
            }
        }

    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    public T get(String id) throws ObjectNotFoundException {

        checklifetime();
        System.out.println("time:"+(System.currentTimeMillis())/1000);
        System.out.println("time:"+(arraylist.get(0).starttime)/1000);

        synchronized (lock1){
            int index=-1;

            try{
                System.out.println(arraylist.size());
                for(int i=0;i<arraylist.size();i++){
                    if(id.equals(arraylist.get(i).getid())){
                        index=i;
                    }

                }
                if(index==-1){
                    throw new ObjectNotFoundException("Object Not Found Exception");
                }
            }
            catch(ObjectNotFoundException e){

            }


            int timeinSecond=(int)System.currentTimeMillis()/1000;
            arraylist.get(index).lifetime=arraylist.get(index).lifetime+timeinSecond;

            return (T) arraylist.get(index).t;
        }


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

        int count= arraylist.stream().map(x->x.getid()).filter(x->x.equals(id)).collect(toList()).size();

        if(count!=1){
            return false;
        }
        else{
            int index=0;
            for(int i=0;i<arraylist.size();i++){
                if(id.equals(arraylist.get(i).getid())){
                    index=i;
                }
            }
            int timeinSecond=(int)System.currentTimeMillis()/1000;
            arraylist.get(index).lifetime=arraylist.get(index).lifetime+timeinSecond;
            arraylist.get(index).usedtime=(int)System.currentTimeMillis()/1000;

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
    public boolean update(T t) throws ObjectNotFoundException{
        checklifetime();
        int count=0;
        int index=-1;
        try{
            for(int i=0;i<arraylist.size();i++){
                if(t.id().equals(arraylist.get(i).getid())){
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
        arraylist.get(index).usedtime=System.currentTimeMillis()/1000;
        return true;
    }


}
