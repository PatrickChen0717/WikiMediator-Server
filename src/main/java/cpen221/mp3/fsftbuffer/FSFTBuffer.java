package cpen221.mp3.fsftbuffer;

import java.util.ArrayList;
import static java.util.stream.Collectors.toList;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;


public class FSFTBuffer<T extends Bufferable> {

    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock lock1 = new ReentrantLock();

    /**
     * capacity: the max number of element that can exist in the cache
     * timeout: the lifetime of individual element in the cache
     * arraylist: a cache that contains all the elements in the buffer
     */
    private final int capacity;
    private final int  timeout;
    private ArrayList<element<T>> arraylist=new ArrayList<element<T>>();

    /**
     * Create a buffer with a fixed capacity and a timeout value.
     * Objects in the buffer that have not been refreshed within the
     * timeout period are removed from the cache.
     *
     * @param capacity the number of objects the buffer can hold
     * @param timeout  the duration, in seconds, an object should
     *                 be in the buffer before it times out
     *
     *                 Precondition: capacity>=0 timeout>0
     *                 RI: Number of element always less than capacity
     */
    public FSFTBuffer(int capacity, int timeout) {
        this.capacity=capacity;
        this.timeout= timeout;
    }

    /**
     * @return  the address of arraylist field
     */
    public ArrayList<element<T>> getArray() {
        return this.arraylist;
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DSIZE, DTIMEOUT);
    }

    /**
     * filter out the elements in arraylist that pasts its lifetime
     */
    public void checklifetime(){
        synchronized (lock1){
            long currentime=System.currentTimeMillis();
            arraylist= (ArrayList<element<T>>) arraylist.stream().filter(x->x.getlifetime()* 1000L >=(currentime-x.starttime)).collect(toList());
        }
    }
    /**
     * @param: an object type T
     * @return: true if sucessfully added, false if otherwise
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     */
    public boolean put(T t) {
        element<T> newelement= new element<>(t);
        newelement.changelifetime(timeout);

        System.out.println("arraysize: "+arraylist.size()+ " capacity: "+capacity);

        if(arraylist.size()<capacity){
            lock.lock();
            arraylist.add(newelement);
            newelement.starttime=System.currentTimeMillis();
            newelement.usedtime=System.currentTimeMillis();
            lock.unlock();
            return true;
        }
        else if(arraylist.size()==capacity){
            checklifetime();
            if(arraylist.size()==capacity) {
                lock.lock();
                while (lock1.isLocked()){}//wait for unlock
                long temp = arraylist.get(0).usedtime;
                int index1 = 0;
                for (int i = 0; i < arraylist.size(); i++) {
                    if (temp > arraylist.get(i).usedtime) {
                        index1 = i;
                        temp = arraylist.get(i).usedtime;
                    }
                }
                arraylist.remove(index1);
                arraylist.add(newelement);
                newelement.starttime=System.currentTimeMillis();
                newelement.usedtime=System.currentTimeMillis();
                lock.unlock();
            }
            else {
                lock.lock();
                while (lock1.isLocked()){}//wait for unlock
                arraylist.add(newelement);
                newelement.starttime=System.currentTimeMillis();
                newelement.usedtime=System.currentTimeMillis();
                lock.unlock();
            }
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
     *
     * retrieve an object from the buffer cache (arraylist), and update its usedtime to this moment
     */
    public T get(String id) throws NoSuchElementException {
        long currentime=System.currentTimeMillis();
        checklifetime();
        while (lock.isLocked()){}//wait for unlock
        lock1.lock();//lock put method

        int index=-1;

        try{
            for(element<T> x : arraylist){
                if(id.equals(x.getid())){
                    index = arraylist.indexOf(x);
                }
            }

            if(index!=-1) {
                arraylist.get(index).starttime = currentime;
                arraylist.get(index).usedtime = currentime;
                lock1.unlock();
                return arraylist.get(index).getT();
            }
            else{
                lock1.unlock();
                throw new NoSuchElementException("No such element in the list");
            }
        }
        catch(NoSuchElementException e){
            lock1.unlock();
            throw new NoSuchElementException("No such element in the list");
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
        long currenttime=System.currentTimeMillis();
        while (lock.isLocked()){
        }
        int count= arraylist.stream().map(element::getid).filter(x->x.equals(id)).collect(toList()).size();

        if(count!=1){
            return false;
        }
        else{
            int index2=0;
            for(int i=0;i<arraylist.size();i++){
                if(id.equals(arraylist.get(i).getid())){
                    index2=i;
                }
            }
            arraylist.get(index2).usedtime=currenttime;
            arraylist.get(index2).starttime=currenttime;
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
    public boolean update(T t) throws NoSuchElementException{
        String id=t.id();
        boolean touched=touch(id);
        if(touched){
            for(int i=0;i<arraylist.size()-1;i++){
                if(arraylist.get(i).getid().equals(t.id())){
                    arraylist.get(i).repalceT(t);
                }
            }
            return true;
        }
        else return false;
    }

    /**
     * @return the number of elements inside buffer cache
     */
    public int getCachesize(){
        return this.arraylist.size();
    }


}
