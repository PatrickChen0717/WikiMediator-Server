package cpen221.mp3;

import cpen221.mp3.fsftbuffer.*;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class Tests {

        /*
        You can add your tests here.
        Remember to import the packages that you need, such
        as cpen221.mp3.fsftbuffer.
     */
        @Test
        public void test2() throws InterruptedException {
            WikiMediator test=new WikiMediator(5,20);
            long time1=System.currentTimeMillis();
            System.out.println(test.search("Edison Chen",10));
            test.getPage("Edison Chen");
            test.getPage("China");
            long time2=System.currentTimeMillis();
            System.out.println("---------------------------");
            System.out.println((time2-time1)/1000);
            TimeUnit.SECONDS.sleep(5);
            long time3=System.currentTimeMillis();
            test.getPage("China");
            test.getPage("Bilibili");
            long time4=System.currentTimeMillis();
            System.out.println("---------------------------");
            System.out.println((time3-time4)/1000);
            TimeUnit.SECONDS.sleep(5);

            test.getPage("Counter-Strike: Global Offensive");
            test.trending(2,2);








        }
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

        /*
        System.out.println(test.put(t1));
        TimeUnit.SECONDS.sleep(45);
        System.out.println(test.touch("1"));

        T t2 =test.get("1");

        */





        // list.put(e1);
        // System.out.println(list.arraylist.get(0).lifetime);
        //System.out.println(list.get("1").lifetime);




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
