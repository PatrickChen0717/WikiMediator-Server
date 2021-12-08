package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.T;
import cpen221.mp3.fsftbuffer.T1;
import cpen221.mp3.fsftbuffer.element;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.NoSuchElementException;

public class TestTask1 {
    FSFTBuffer buffer1=new FSFTBuffer(5, 10);
    //functionality test
    @Test
    public void test4() {
        T t1=new T("t1");
        T t2=new T("t2");
        buffer1.put(t1);
        buffer1.put(t2);

        for(int i=0;i<buffer1.getCachesize();i++){
           // System.out.println(buffer1.arraylist.get(i).getid());
        }

        //ArrayList<String> cache=buffer1.arraylist.stream().map(x->x.get)
        //Assertions.assertEquals(new String[]{"t1","t2"},buffer1.arraylist.);



    }

    @Test
    public void test1_1() throws NoSuchElementException, InterruptedException {
        T1 t1=new T1("1");
        T1 t2=new T1("0");
        T1 t3=new T1("2");
        T1 t4=new T1("3");
        T1 t5=new T1("4");
        T1 t6=new T1("5");
        FSFTBuffer<T1> list=new FSFTBuffer<>();
        Assertions.assertTrue(list.put(t1));
        Assertions.assertTrue(list.put(t2));
        Assertions.assertTrue(list.put(t3));
        Assertions.assertTrue(list.put(t4));
        Assertions.assertFalse(list.touch("4"));
        Assertions.assertTrue(list.put(t5));
        Assertions.assertTrue(list.touch("4"));
        Assertions.assertTrue(list.update(t5));
        Assertions.assertFalse(list.update(t6));
    }

    @Test
    /*
     *
     * check for whether the element will be delete after time out
     * check when the list if full the most nonrecent used one will be replace when the put method is called
     *
     * */
    public void test1_2() throws NoSuchElementException, InterruptedException {
        T1 t1=new T1("1");
        T1 t2=new T1("0");
        T1 t3=new T1("2");
        T1 t4=new T1("3");
        T1 t5=new T1("4");
        T1 t6=new T1("3");
        FSFTBuffer<T1> list=new FSFTBuffer<>(2,4);
        Assertions.assertTrue(list.put(t1));
        Assertions.assertTrue(list.put(t2));
        Thread.sleep(4000);
        Assertions.assertTrue(list.put(t3));
        Thread.sleep(1000);

        Assertions.assertTrue(list.put(t4));
        Assertions.assertFalse(list.touch("1"));
        Assertions.assertFalse(list.touch("0"));
        Assertions.assertTrue(list.update(t6));
        Assertions.assertTrue(list.put(t5));
        Assertions.assertFalse(list.touch("2"));
        Assertions.assertTrue(list.touch("3"));
        Thread.sleep(3000);
        Assertions.assertEquals(t5,list.get("4"));

        Assertions.assertEquals(t4,list.get("3"));


    }

    @Test
    public void testTask1_3() throws InterruptedException {

        T1 t1=new T1("1");
        T1 t2=new T1("0");
        T1 t3=new T1("2");
        T1 t4=new T1("3");
        T1 t5=new T1("4");
        T1 t6=new T1("3");
        FSFTBuffer<T1> list=new FSFTBuffer<>(2,10);
        Assertions.assertTrue(list.put(t1));
        Assertions.assertTrue(list.put(t2));
        Thread.sleep(4000);
        Assertions.assertTrue(list.put(t3));
        Thread.sleep(1000);

        Assertions.assertTrue(list.put(t4));
        Assertions.assertFalse(list.touch("1"));
        Assertions.assertFalse(list.touch("0"));
        Assertions.assertTrue(list.update(t6));
        Assertions.assertTrue(list.put(t5));
        Assertions.assertFalse(list.touch("2"));
        Assertions.assertTrue(list.touch("3"));
        Thread.sleep(3000);
        Assertions.assertEquals(t5,list.get("4"));
        Assertions.assertEquals(t4,list.get("3"));



    }

    @Test
    public void testTask1_4() throws InterruptedException {

        T1 t1=new T1("1");
        T1 t2=new T1("0");
        T1 t3=new T1("2");
        T1 t4=new T1("3");
        T1 t5=new T1("4");
        T1 t6=new T1("3");
        FSFTBuffer<T1> list=new FSFTBuffer<>(2,4);
        Assertions.assertTrue(list.put(t1));

        Thread.sleep(4000);
        Assertions.assertTrue(list.put(t2));
        Assertions.assertTrue(list.put(t3));
        Thread.sleep(1000);

        Assertions.assertTrue(list.put(t4));
        Assertions.assertFalse(list.touch("1"));
        Assertions.assertFalse(list.touch("0"));
        Assertions.assertTrue(list.update(t6));
        Assertions.assertTrue(list.put(t5));
        Assertions.assertFalse(list.touch("2"));
        Assertions.assertTrue(list.touch("3"));
        Thread.sleep(3000);
        Assertions.assertEquals(t5,list.get("4"));
        Assertions.assertEquals(t4,list.get("3"));



    }

    @Test
    public void testTask1_5() throws InterruptedException {

        T1 t1=new T1("1");
        T1 t2=new T1("0");
        T1 t3=new T1("2");
        T1 t4=new T1("3");
        T1 t5=new T1("4");
        T1 t6=new T1("3");
        T1 t7=new T1("8");
        FSFTBuffer<T1> list=new FSFTBuffer<>(2,5);
        Assertions.assertTrue(list.put(t1));
        Assertions.assertTrue(list.put(t7));
        Thread.sleep(4000);
        Assertions.assertTrue(list.put(t2));

        Thread.sleep(1000);
        Assertions.assertTrue(list.put(t3));

        Assertions.assertTrue(list.put(t4));
        Assertions.assertFalse(list.touch("1"));
        Assertions.assertFalse(list.touch("0"));
        Assertions.assertTrue(list.update(t6));
        Assertions.assertTrue(list.put(t5));
        Assertions.assertFalse(list.touch("2"));
        Assertions.assertTrue(list.touch("3"));
        Thread.sleep(3000);
        Assertions.assertEquals(t5,list.get("4"));
        Assertions.assertEquals(t4,list.get("3"));



    }

    @Test
    public void testTask1_6() throws InterruptedException {
        //expect exception thrown
        T1 t1=new T1("8");
        FSFTBuffer<T1> list=new FSFTBuffer<>(2,5);
        list.get("8");

    }
    @Test

    public void testTask1_7() throws InterruptedException {
        //expect exception thrown
        T1 t1=new T1("8");
        FSFTBuffer<T1> list=new FSFTBuffer<>();
        Assertions.assertEquals(false,list.touch("8"));
        list.getArray();
        list.getCachesize();
    }
}
