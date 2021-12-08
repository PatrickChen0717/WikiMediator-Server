package cpen221.mp3;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.T1;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TestTask2 {

    public void operations(FSFTBuffer<T1> list) {
        T1 t1=new T1("1");
        T1 t2=new T1("0");
        T1 t3=new T1("2");
        T1 t4=new T1("3");
        T1 t5=new T1("4");

        list.put(t1);
        System.out.println( "operations result3 "+list.get("3").id()); // get

    }

    private static void operations1(FSFTBuffer<T1> list) {
        T1 t1=new T1("10");
        list.put(t1);
    }

    private static void operations2(FSFTBuffer<T1> list) {
        System.out.println( "operations2 result1 "+list.get("10").id());
    }

    //read and write
    //write and write
    @Test
    public void test1() throws InterruptedException{

        T1 t2=new T1("0");
        T1 t3=new T1("2");
        T1 t4=new T1("3");
        T1 t5=new T1("4");

        System.out.println(System.currentTimeMillis()/1000);
        FSFTBuffer<T1> list=new FSFTBuffer<>(4,20);

        list.put(t2); // put
        list.put(t3); // put
        list.put(t4); // put
        list.put(t5); // put

        Thread th0 = new Thread(new Runnable() {      //thread 0
            public void run() {
                Thread.yield();  // give the other threads a chance to start too, so it's a fair race
                operations1(list);   // do the transactions for this cash machine
            }
        });

        Thread th1 = new Thread(new Runnable() {      //thread 1
            public void run() {
                Thread.yield();  // give the other threads a chance to start too, so it's a fair race
                operations2(list);   // do the transactions for this cash machine
            }
        });

        th0.start();
        th1.start();

        th0.join();
        th1.join();

    }

    @Test
    public void test3() throws InterruptedException {

        T1 t2 = new T1("0");
        T1 t3 = new T1("2");
        T1 t4 = new T1("3");
        T1 t5 = new T1("4");

        T1 t6 = new T1("12");
        T1 t7 = new T1("13");
        T1 t8 = new T1("14");
        T1 t9 = new T1("15");


        T1[] testList = {t6, t7, t8, t9};

        System.out.println(System.currentTimeMillis() / 1000);
        FSFTBuffer<T1> list = new FSFTBuffer<>(4, 20);

        list.put(t2); // put
        list.put(t3); // put
        list.put(t4); // put
        list.put(t5); // put

        List<Multithread_Put> threads11 = new ArrayList<Multithread_Put>();
        List<Multithread_Get> threads22 = new ArrayList<Multithread_Get>();

        for (int i = 0; i < 4; i++) {
            Multithread_Put myThing = new Multithread_Put(list, testList[i]);
            Multithread_Get myThing1 = new Multithread_Get(list, testList[i]);

            threads11.add(myThing);
            threads22.add(myThing1);
            myThing.start();
            Thread.sleep(1);
            myThing1.start();

        }

        for (Thread myThing : threads11) myThing.join();
        for (Thread myThing : threads22) myThing.join();

        System.out.println(list);

    }
    @Test
    public void test5() throws InterruptedException {

        T1 t2 = new T1("0");
        T1 t3 = new T1("2");
        T1 t4 = new T1("3");
        T1 t5 = new T1("4");

        T1 t6 = new T1("12");
        T1 t7 = new T1("13");
        T1 t8 = new T1("14");
        T1 t9 = new T1("15");


        T1[] testList = {t6, t7, t8, t9};

        System.out.println(System.currentTimeMillis() / 1000);
        FSFTBuffer<T1> list = new FSFTBuffer<>(4, 20);

        list.put(t2); // put
        list.put(t3); // put
        list.put(t4); // put
        list.put(t5); // put

        List<Multithread_Put> threads11 = new ArrayList<Multithread_Put>();
        List<Multithread_Get> threads22 = new ArrayList<Multithread_Get>();

        for (int i = 0; i < 4; i++) {
            Multithread_Put myThing = new Multithread_Put(list, testList[i]);
            for(int j = 0; j < 4; j++) {
                Multithread_Get myThing1 = new Multithread_Get(list, testList[j]);
                threads22.add(myThing1);
                myThing1.start();
            }

            threads11.add(myThing);

            myThing.start();
            //Thread.sleep(1);


        }

        for (Thread myThing : threads11) myThing.join();
        for (Thread myThing : threads22) myThing.join();

        System.out.println(list);

    }
}
