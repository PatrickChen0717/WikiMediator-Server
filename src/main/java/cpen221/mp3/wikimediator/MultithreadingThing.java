package cpen221.mp3.wikimediator;

import org.fastily.jwiki.core.Wiki;
import java.util.ArrayList;

public class MultithreadingThing extends Thread{

    /**
     * wiki: an instance of Wiki
     * list: a cache that stores search results
     * Keyword: a word that represents pagetitle
     */
    private final Wiki wiki;
    private ArrayList<String> list;
    private String Keyword;

    /**
     * Constructor
     * @oaram PageTitle is the to-search String
     *
     */
    public MultithreadingThing(String PageTitle){
        this.wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        this.Keyword = PageTitle;
    }

    /**
     * Executor
     * performs a wiki page search with given String keyword
     */
    @Override
    public void run() {
        list = wiki.search(Keyword, 60);
    }

    /**
     * Observer
     * returns the wiki search result list
     */
    public ArrayList<String> getList() {
        list.remove(this.Keyword);
        return list;
    }
}
