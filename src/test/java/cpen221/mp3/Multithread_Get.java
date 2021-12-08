package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.T1;

public class Multithread_Get extends Thread{

    FSFTBuffer<T1> list;
    private T1 item;
    private T1 ID;

    public Multithread_Get(FSFTBuffer<T1> list, T1 item){

        this.list = list;
        this.item = item;
    }


    public void run() {
        ID = list.get(item.id());

    }
}
