package cpen221.mp3.wikimediator;

import org.fastily.jwiki.core.Wiki;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

public class WikiMediator {
    /**
     * pagelist: a cache that contains all the wikipages
     * wiki: a new wiki instance
     * capacity: the number of wikitext that exists in the the cache
     * stalenessInterval: the lifetime of the wikitexts inside wikipages in seconds
     * counting: the times stamps list of the access times of all wikipages
     */

    private long otherfunctionused;
    private ArrayList<wikipage> pagelist = new ArrayList<wikipage>();
    private final Wiki wiki;
    private int capacity;
    private final int stalenessInterval;
    private ArrayList<Long> counting = new ArrayList<Long>();

    /**
     * Create a WikiMediator with fix capacity and time window
     * @param capacity the number of the pretext that a WikiMediator can hold
     * @param stalenessInterval the duration, in seconds, an object should
     *                          be in the WikiMediator before it times out
     * */
    public WikiMediator(int capacity, int stalenessInterval) {
        this.capacity = capacity;
        this.otherfunctionused = 0;
        this.stalenessInterval = stalenessInterval;
        this.wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
    }
    /**
     * Search the given string (query) in wikipedia a
     * and return the with limit responses
     * @param query the string needed to be searched can not be null
     * @param limit the numbers of responses that should be received
     * @return  a list of string with the limit size that contain the responses from wikipedia
     * */
    public List<String> search(String query, int limit) {
        long currenttime = System.currentTimeMillis();
        counting.add((currenttime));
        //check whether there is query in the list
        int index = -1;
        for (int i = 0; i < pagelist.size(); i++) {
            if (pagelist.get(i).getPagetitle().equals(query)) {
                index = i;
            }
        }

        List<String> result = new ArrayList<>();
        if (index == -1) {
            long start = System.currentTimeMillis();
            result = wiki.search(query, limit);
            long end = System.currentTimeMillis();
            otherfunctionused = otherfunctionused + (end - start);
            wikipage newpage = new wikipage(query);
            newpage.timeout = stalenessInterval;
            newpage.starttime = System.currentTimeMillis();
            newpage.incrementAccesscount(System.currentTimeMillis());
            pagelist.add(newpage);
        } else {
            pagelist.get(index).incrementAccesscount(System.currentTimeMillis());
            result = wiki.search(query, limit);
        }

        return result;
    }

    /**
     * get the page of a given string from the wikipedia
     * and add the page content to a cache with limit time life and capacity
     * will be deleted after the page is died
     * if the cache was filled ,size =capacity the most non-recent used one will be replace  by the new adding one
     * @param pageTitle is not null
     * @return the content of the pageTitle in the wikipedia
     * */
    public String getPage(String pageTitle) {
        long currenttime = System.currentTimeMillis();
        counting.add(currenttime);
        //check for timeout
        checkstaleness();
        //check if the page exist
        int index = -1;
        for (int i = 0; i < pagelist.size(); i++) {
            if (pagelist.get(i).getPagetitle().equals(pageTitle)) {
                index = i;
            }
        }

        int count=0;
        for (int i = 0; i < pagelist.size(); i++) {
            if (!pagelist.get(i).getPagetext().equals("")) {
                count++;
            }
        }


        if (count < this.capacity && index == -1) {//new to list
            wikipage newpage = new wikipage(pageTitle);
            long start = System.currentTimeMillis();
            newpage.changePagetext(wiki.getPageText(pageTitle));
            long end = System.currentTimeMillis();
            otherfunctionused = otherfunctionused + (end - start);
            newpage.starttime = System.currentTimeMillis();
            newpage.timeout = stalenessInterval;
            newpage.incrementAccesscount(System.currentTimeMillis());
            pagelist.add(newpage);

            return newpage.getPagetext() ;
        } else if( index != -1){//existed already in the list
            //check wether the page text has been deleted due to staleness
            //if the text is staleness we need re-search the text
            if (pagelist.get(index).getPagetext().equals("")) {
                pagelist.get(index).changePagetext(wiki.getPageText(pagelist.get(index).getPagetitle())) ;
                pagelist.get(index).starttime = System.currentTimeMillis();
                pagelist.get(index).incrementAccesscount(System.currentTimeMillis());
                return pagelist.get(index).getPagetext();
            } else {
                pagelist.get(index).incrementAccesscount(System.currentTimeMillis());
                return pagelist.get(index).getPagetext();
            }
        }else {//new to list and capacity full
            ArrayList<wikipage> temp=new ArrayList<>();
            for(int i=0;i<pagelist.size();i++){
                if(!pagelist.get(i).getPagetext().equals("")){
                    temp.add(pagelist.get(i));
                }
            }

            //find the most unrecent used page
            Long timecheck=temp.get(0).getAccesshistory().get(temp.get(0).getAccesshistory().size()-1);
            String removepagetitle=temp.get(0).getPagetitle();
            for(int i=0;i<temp.size();i++){
                if(temp.get(i).getAccesshistory().get(temp.get(i).getAccesshistory().size()-1)<timecheck){
                    timecheck=temp.get(i).getAccesshistory().get(temp.get(i).getAccesshistory().size()-1);
                    removepagetitle=temp.get(i).getPagetitle();
                }
            }


            for(int i=0;i<pagelist.size();i++){
                if(pagelist.get(i).getPagetitle().equals(removepagetitle)){
                    pagelist.get(i).changePagetext("");
                }
            }

            wikipage newpage = new wikipage(pageTitle);
            long start = System.currentTimeMillis();
            newpage.changePagetext(wiki.getPageText(pageTitle));
            long end = System.currentTimeMillis();
            otherfunctionused = otherfunctionused + (end - start);
            newpage.starttime = System.currentTimeMillis();
            newpage.timeout = stalenessInterval;
            newpage.incrementAccesscount(System.currentTimeMillis());
            pagelist.add(newpage);

            return newpage.getPagetext() ;
        }

    }

    /**
     * find the top limit popular strings that used in search and getPage methods
     * in the non-increasing order,if more strings have the same frequency,the one
     * that searched or getPaged earlier will be ranking higher
     * @param limit the numbers of most popular string that need to be return
     * @return A list of strings that contains the top limit popular strings
     *          used in the search and getPage methods
     */
    public List<String> zeitgeist(int limit) {
        long currentime = System.currentTimeMillis();
        counting.add(currentime);
        List<wikipage> list = pagelist;
        list.sort(Comparator.comparing(wikipage::getAccesscount).reversed());
        List<String> result = list.stream().map(wikipage::getPagetitle).collect(toList());
        for (int i = 0; i < list.size() - limit; i++) {
            result.remove(result.size() - 1);
        }


        return result;
    }

    /**
     * similar with zeitgeist,but this methods find the top limits popular strings
     * used in search and getPage functions with in an time
     *
     * @param timeLimitInSeconds  last time period in seconds
     * @param maxItems most frequent requested  items
     * @return  a list of strings with most popular in the time window
     * */
    public List<String> trending(int timeLimitInSeconds, int maxItems) {
        long ctt = System.currentTimeMillis();
        counting.add(ctt);
        ArrayList<wikipage> list = new ArrayList<>();
        ArrayList<String> Return = new ArrayList<>();
        for (int i = 0; i < pagelist.size(); i++) {
            list.add(pagelist.get(i));
        }

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getAccesshistory().size(); j++) {
                if (list.get(i).getAccesshistory().get(j) < ctt - (timeLimitInSeconds * 1000L)) {
                    list.get(i).accesshistory.remove(j);
                    j--;
                }
            }
        }

        list.sort(Comparator.comparing(wikipage::getAccesscount).reversed());

        for (wikipage i : list) {
            if (i.getAccesshistory().size() > 0 && Return.size() < maxItems) {
                Return.add(i.getPagetitle());
            }
        }


        return Return;
    }

    /**
     * filter the wikipages that exceed its staleness
     */
    public void checkstaleness() {
        long ct = System.currentTimeMillis();
        long tct = ct - otherfunctionused;
        System.out.println("current time :" + ct);

        long ct3 = System.currentTimeMillis();

        //we want to only delete the pagetext rather other fields of our wikipage
        for (wikipage i : pagelist) {
            System.out.println(i.getPagetitle()+" start at:"+i.starttime);
            if (i.timeout * 1000L <= (tct - i.starttime)) {
                i.changePagetext("");
            }
        }
    }

    /**
     * the maximum number of requests seen in any time window of a given length
     * @param timeWindowInSeconds time window in seconds
     * @return he maximum number of requests seen in any time window of a given length
     *
     */
    public int windowedPeakLoad(int timeWindowInSeconds) {
        ArrayList<Integer> allcounting = new ArrayList<>();
        ArrayList<Long> rep = new ArrayList<>(counting);
        for (Long i : counting) {
            allcounting.add((int) rep.stream().filter(x -> x < (i + (timeWindowInSeconds * 1000L))).count());
            rep.remove(i);
        }
        return Collections.max(allcounting);

    }

    /**
     * the maximum number of requests seen in any time window of a given length
     * with default time window 30s
     * @return he maximum number of requests seen in any time window of a given length
     *
     */
    public int windowedPeakLoad() {
        int timeWindowInSeconds = 30;
        ArrayList<Integer> allcounting = new ArrayList<>();
        ArrayList<Long> rep = new ArrayList<>(counting);
        System.out.println(rep);
        for (Long i : counting) {
            allcounting.add((int) rep.stream().filter(x -> x < (i + (timeWindowInSeconds * 1000L))).count());
            rep.remove(i);
        }
        return Collections.max(allcounting);

    }

    /**
     * the shortestPath between two wikipage in a given amount of time
     * If there are two or more shortest paths then the one with the
     * lowest lexicographical value is to be returned.
     * @param pageTitle1 a string that represent the starting page title
     * @param pageTitle2 a string that represent the target page title
     * @param timeout time window
     * @return the shortest path of page titles
     * @throw TimeoutException if the task can't be done in time
     *        InterruptedException if the thread is interrupted
     */

    public List<String> shortestPath(String pageTitle1, String pageTitle2, int timeout) throws TimeoutException{
        List<String> result = new ArrayList<>();
        ExecutorService timeoutserve = Executors.newCachedThreadPool();
        Callable<Object> task = new Callable<Object>() {
            public Object call() throws IOException, InterruptedException, TimeoutException {
                System.out.println("checker");
                result.addAll(this.shortestPathhelper( pageTitle1, pageTitle2, timeout));
                return result;
            }
            private List<String> shortestPathhelper(String pageTitle1, String pageTitle2, int timeout) throws TimeoutException, InterruptedException {
                long currentime=System.currentTimeMillis();
                counting.add(currentime);
                List<String> toSearch = new ArrayList<>();
                List<String> foundIt = new ArrayList<>();

                foundIt.add(pageTitle1);

                MultithreadingThing firstLayer = new MultithreadingThing(pageTitle1);
                firstLayer.start();
                firstLayer.join();
                toSearch.addAll(firstLayer.getList());
                int depth =1;

                while(!toSearch.contains(pageTitle2)){

                    Collections.sort(toSearch);//sort

                    List<MultithreadingThing> threads = new ArrayList<MultithreadingThing>();
                    for(int i = 0; i<toSearch.size(); i++){
                        MultithreadingThing myThing = new MultithreadingThing(toSearch.get(i));
                        threads.add(myThing);
                        myThing.start();
                    }

                    for (Thread myThing: threads) myThing.join();

                    List<String> keepFinding = new ArrayList<>();
                    boolean status =true;
                    for (int i = 0; i < threads.size(); i++){
                        if(threads.get(i).getList().contains(pageTitle2) && status){
                            foundIt.add(toSearch.get(i));
                            keepFinding.addAll(threads.get(i).getList());
                            status = false;
                        }
                        else{
                            keepFinding.addAll(threads.get(i).getList());
                        }

                    }

                    toSearch.clear();
                    toSearch.addAll(keepFinding);

                    depth++;

                }


                foundIt.add(pageTitle2);

                if(!wiki.search(foundIt.get(0),60).contains(foundIt.get(1))){
                    List<String> temp = new ArrayList<>();
                    temp = shortestPath(pageTitle1, foundIt.get(1), 1000);
                    foundIt.add(foundIt.size()-2,temp.get(temp.size()-2));
                }

                System.out.println(depth);
                return foundIt;
            }
        };
        Future<Object> future = timeoutserve.submit(task);
        try {
            future.get(timeout, TimeUnit.SECONDS);
            System.out.println(result);
            return result;
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}