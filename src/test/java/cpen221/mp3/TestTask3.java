package cpen221.mp3;

import cpen221.mp3.wikimediator.WikiMediator;
import cpen221.mp3.wikimediator.wikipage;
import org.fastily.jwiki.core.Wiki;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class TestTask3 {
    @Test
    public void test1() throws InterruptedException {

        WikiMediator test=new WikiMediator(5,5);

        test.search("G.M.E",10);
        test.getPage("G.M.E");
        Thread.sleep(1000);
        test.search("Engineering",2);
        Thread.sleep(2000);
        test.getPage("iPhone");
        test.search("iPhone",2);
        Thread.sleep(1000);
        test.search("Taylor Swift",2);
        test.getPage("Java");
        test.getPage("iPhone");
        test.search("Taylor Swift",2);
        List<String> expect=new ArrayList<>();
        expect.add("Taylor Swift");
        expect.add("iPhone");
        expect.add("Java");
        Thread.sleep(1000);
        /*
        System.out.println(System.currentTimeMillis());
        String temp=test.getPage("G.M.E");
        System.out.println(System.currentTimeMillis());
        Thread.sleep(2000);
        System.out.println(System.currentTimeMillis());

         */
        Assertions.assertEquals(expect,test.trending(2,3));
    }
    WikiMediator  test=new WikiMediator(500,500);
    @Test
    public void test2() throws InterruptedException {
        test.getPage("apple");
        test.getPage("apple");
        Thread.sleep(2000);
        test.search("iphone",4);
        test.search("iphone",3);
        Thread.sleep(1000);
        test.search("iphone",5);
        test.zeitgeist(2);
        Assertions.assertEquals(4 ,test.windowedPeakLoad(2));
        Assertions.assertEquals(6,test.windowedPeakLoad());
    }

    @Test
    public void test3() throws InterruptedException {
        test.getPage("apple");
        test.getPage("apple");
        Thread.sleep(2000);
        test.search("iphone",4);
        test.search("iphone",3);
        Thread.sleep(1000);
        test.search("iphone",5);
        test.zeitgeist(2);
        Assertions.assertEquals(2,test.windowedPeakLoad(1));
    }

    //timeout filter case
    @Test
    public void test4() throws InterruptedException {
        WikiMediator  test1=new WikiMediator(500,5);
        test1.getPage("apple");
        Thread.sleep(10000);
        test1.getPage("UBC");
        List<String> expect=new ArrayList<>();
        expect.add("apple");
        expect.add("UBC");
        Assertions.assertEquals(expect,test1.zeitgeist(2));

    }

    @Test
    public void testTask3() throws InterruptedException {
        WikiMediator  test=new WikiMediator(5,20);

        test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(2000);
        test.search("Counter-Strike: Global Offensive",2);
        Thread.sleep(3000);
        test.getPage("Apple");
        test.search("Apple",2);
        Thread.sleep(1000);
        test.search("Computer",2);
        test.getPage("Apple");
        test.getPage("Computer");
        test.search("Computer",2);
        test.getPage("Counter-Strike: Global Offensive");
        test.getPage("Computer");
        List<String> expect=new ArrayList<>();
        expect.add("Computer");
        expect.add("Apple");
        expect.add("Counter-Strike: Global Offensive");
        Assertions.assertEquals(expect,test.trending(10,3));


    }

    @Test
    public void testTask3_2() throws InterruptedException {
        /*
         * test for trending
         * basic case
         *
         * */
        WikiMediator  test=new WikiMediator(5,5);

        test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(2000);
        test.search("Counter-Strike: Global Offensive",2);
        Thread.sleep(3000);
        test.getPage("Apple");
        Thread.sleep(1000);
        test.search("Apple",2);
        test.search("Computer",2);
        test.getPage("Apple");
        test.getPage("Computer");
        test.search("Computer",2);
        test.getPage("Daniel Wu");
        test.getPage("Computer");
        List<String> expect=new ArrayList<>();
        expect.add("Computer");
        expect.add("Apple");
        expect.add("Daniel Wu");
        Assertions.assertEquals(expect,test.trending(2,3));


    }

    @Test
    public void testTask3_2_1() throws InterruptedException {
        /*
         * test for trending
         * when two strings has the same frequency
         * the most resent one will rank higher
         *
         * */
        WikiMediator  test=new WikiMediator(5,5);

        test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(2000);
        test.search("Counter-Strike: Global Offensive",2);
        Thread.sleep(3000);
        test.getPage("Apple");
        test.search("Apple",2);
        Thread.sleep(2000);
        test.search("Computer",2);
        test.getPage("Apple");
        test.getPage("Computer");
        test.search("Computer",2);
        test.getPage("Daniel Wu");
        test.getPage("Computer");
        List<String> expect=new ArrayList<>();
        expect.add("Computer");
        expect.add("Daniel Wu");
        expect.add("Apple");
        Assertions.assertEquals(expect,test.trending(2,3));

    }

    @Test
    /*
     * testing zeitgeist
     * two string have the same frequency
     * the string that was search first will rank higher
     * */
    public void testTask3_3() throws InterruptedException {
        WikiMediator  test=new WikiMediator(5,20);

        test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(2000);
        test.search("Counter-Strike: Global Offensive",2);
        Thread.sleep(3000);
        test.getPage("Computer");
        test.search("Apple",2);
        Thread.sleep(1000);
        test.search("Computer",2);
        test.getPage("Apple");
        test.getPage("Apple");
        test.search("Computer",2);
        test.getPage("Apple");
        test.getPage("Computer");
        List<String> forz=new ArrayList<>();
        forz.add("Computer");
        forz.add("Apple");
        Assertions.assertEquals(forz,test.zeitgeist(2));


    }

    @Test
    public void testTask3_4() throws InterruptedException {
        /*
         * basic test for zeitgeist
         * return the most popular strings among the search and getpage function
         * */
        WikiMediator  test=new WikiMediator(5,20);

        test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(2000);
        test.search("Counter-Strike: Global Offensive",2);
        Thread.sleep(3000);
        test.getPage("Apple");
        test.search("Apple",2);
        Thread.sleep(1000);
        test.search("Computer",2);
        test.getPage("Dianel Wu");
        test.getPage("Computer");
        test.search("Computer",2);
        test.getPage("Apple");
        test.getPage("Computer");
        List<String> forz=new ArrayList<>();
        forz.add("Computer");
        forz.add("Apple");
        Assertions.assertEquals(forz,test.zeitgeist(2));


    }

    @Test
    public void testTask3_5() throws InterruptedException {
        /*
         * test the windowedPeaLoad default with 5s interval
         * */
        WikiMediator  test=new WikiMediator(5,20);

        test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(2000);
        test.search("Counter-Strike: Global Offensive",2);
        Thread.sleep(3000);
        test.getPage("Apple");
        test.search("Apple",2);
        Thread.sleep(1000);
        test.search("Computer",2);
        test.getPage("Dianel Wu");
        test.getPage("Computer");
        test.search("Computer",2);
        test.getPage("Apple");
        test.getPage("Computer");
        Assertions.assertEquals(8,test.windowedPeakLoad(5));

    }

    @Test
    public void testTask3_6() throws InterruptedException {
        /*
         * test the windowedPeaLoad default with 30s interval
         * */
        WikiMediator  test=new WikiMediator(5,5);

        test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(2000);
        test.search("Counter-Strike: Global Offensive",2);
        Thread.sleep(4000);
        test.search("Apple",2);
        test.getPage("Apple");
        Thread.sleep(10000);
        test.search("Computer",2);

        test.getPage("Dianel Wu");
        test.getPage("Computer");
        test.search("Computer",2);
        test.getPage("Apple");
        Thread.sleep(20000);
        test.getPage("Computer");

        Assertions.assertEquals(10,test.windowedPeakLoad());

    }

    @Test
    public void testTask3_8() throws InterruptedException {
        /*
         * test for getpage but the page is timeout
         * */
        WikiMediator  test=new WikiMediator(5,10);

        // test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(8000);
        test.getPage("Counter-Strike: Global Offensive");
        Thread.sleep(4000);
        test.getPage("Apple");
        Thread.sleep(1000);
        test.getPage("Computer");
        test.getPage("Mark Van Raamsdonk");

        test.getPage("Daniel Wu");

        //Assertions.assertEquals(forz,test.zeitgeist(2));


    }

    @Test
    public void testTask3_7() throws InterruptedException {
        /*
         * test for the getpage l
         * */
        WikiMediator  test=new WikiMediator(5,10);

        // test.search("Edison Chen",10);
        test.getPage("Daniel Wu");
        Thread.sleep(2000);
        test.getPage("Counter-Strike: Global Offensive");
        Thread.sleep(4000);
        test.getPage("Apple");
        Thread.sleep(1000);
        test.getPage("Computer");
        test.getPage("Mark Van Raamsdonk");

        test.getPage("Zhengding County");
        Wiki wiki=new  Wiki.Builder().withDomain("en.wikipedia.org").build();
        Assertions.assertEquals(wiki.getPageText("Zhengding County"),test.getPage("Zhengding County"));

    }

    @Test
    public void testTask3_9() throws InterruptedException, TimeoutException {
        WikiMediator  test=new WikiMediator(5,5);
        List<String> expect=new ArrayList<>();
        expect.add("Apple");
        expect.add("Apple chip");
        expect.add("Apple M1");
        Assertions.assertEquals(expect,test.shortestPath("Apple","Apple M1",25));
    }

    @Test
    public void testTask3_10() throws InterruptedException, TimeoutException {
        WikiMediator  test=new WikiMediator(5,5);
        List<String> expect=new ArrayList<>();
        expect.add("Apple");
        expect.add("Apple chip");
        expect.add("Apple M1");
        Assertions.assertEquals(expect,test.shortestPath("Apple","Apple M1",5));
        //expected timeout exception
    }
}
