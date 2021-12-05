package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.T;
import cpen221.mp3.fsftbuffer.element;

import java.util.ArrayList;

public class wikipage {
    private String pagetext;
    private String pagetitle;
    public long timeout;
    public long starttime;

    private ArrayList<Integer> accesshistory;

    public wikipage(String pagetitle) {
        this.pagetitle=pagetitle;
    }

    public int getAccesscount() {
        return accesshistory.size();
    }


    public String getPagetitle() {
        return pagetitle;
    }

    public void incrementAccesscount() {
        this.accesshistory.add((int)System.currentTimeMillis());
    }

    public ArrayList<Integer> getAccesshistory() {
        ArrayList<Integer> clonedList = new ArrayList<Integer>();
        for (Integer x: accesshistory) {
            clonedList.add(x);
        }
        return clonedList;
    }
}
