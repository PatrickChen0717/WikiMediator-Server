package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.T1;

public class Multithread_Put extends Thread{

    FSFTBuffer<T1> list;
    private T1 item;

    public Multithread_Put(FSFTBuffer<T1> list, T1 item){

        this.list = list;
        this.item = item;
    }

    @Override
    public void run() {
        list.put(item);

    }


}
