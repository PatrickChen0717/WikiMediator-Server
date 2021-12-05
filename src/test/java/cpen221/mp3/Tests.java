package cpen221.mp3;

import cpen221.mp3.fsftbuffer.*;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Tests {

        /*
        You can add your tests here.
        Remember to import the packages that you need, such
        as cpen221.mp3.fsftbuffer.
     */

    @Test
    public void test1() throws ObjectNotFoundException, InterruptedException {
        /*
        T t1=new T("1");

        System.out.println(System.currentTimeMillis()/1000);
        FSFTBuffer list=new FSFTBuffer(4,20);

        list.put(t1);
        TimeUnit.SECONDS.sleep(25);

        System.out.println("         "+list.get("1").id());
*/

            T t1=new T("1");

            System.out.println(System.currentTimeMillis()/1000);
            FSFTBuffer<Bufferable> list=new FSFTBuffer<>(4,20);

            list.put(t1);
            System.out.println("--------------------------------");
            TimeUnit.SECONDS.sleep(15);
            System.out.println("----------------------- "+list.get("1").id());

            System.out.println(list.touch("1"));
            TimeUnit.SECONDS.sleep(15);

            System.out.println("------------------ "+list.get("1").id());






            // list.put(e1);
            // System.out.println(list.arraylist.get(0).lifetime);
            //System.out.println(list.get("1").lifetime);


    }



       // list.put(e1);
       // System.out.println(list.arraylist.get(0).lifetime);
        //System.out.println(list.get("1").lifetime);
    @Test
       public void test() throws ObjectNotFoundException, InterruptedException {
           T t1=new T("1");

           System.out.println(System.currentTimeMillis()/1000);
           FSFTBuffer<Bufferable> list=new FSFTBuffer<>(4,20);

           list.put(t1);
           System.out.println("--------------------------------");
           TimeUnit.SECONDS.sleep(15);
           System.out.println("----------------------- "+list.get("1").id());

           System.out.println(list.touch("1"));
           TimeUnit.SECONDS.sleep(15);

           System.out.println("------------------ "+list.get("1").id());






           // list.put(e1);
           // System.out.println(list.arraylist.get(0).lifetime);
           //System.out.println(list.get("1").lifetime);

       }


}
