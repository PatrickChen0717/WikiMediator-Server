package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.T;
import cpen221.mp3.fsftbuffer.element;

import java.util.ArrayList;

public class wikipage {
    /**
     * pagetext: the pagetext of the wikipage named pagetitle
     * pagetitle: the title of the wikipage
     * timeout: the life time of pagetext before deleting
     * accesshistory: the cache that contains the timestamp of all access
     */
    private String pagetext="";
    private String pagetitle;
    public long timeout;
    public long starttime;
    public ArrayList<Long> accesshistory =new ArrayList<>();

    /**
     * create a wikipage with the pagetitle
     * @param pagetitle the title of the wikipage
     */
    public wikipage(String pagetitle) {
        this.pagetitle=pagetitle;
    }

    /**
     * get the size of the cache that contains all the access timestamps
     * @return return the size of accesshistory
     */
    public int getAccesscount() {
        return accesshistory.size();
    }

    /**
     * get the title of wikipage
     * @return return the title of wikipage
     */
    public String getPagetitle() {
        return pagetitle;

    }

    /** add the new access timestamp into the accesshistory cache
     * @param: time the access timestamp
     */
    public void incrementAccesscount( long time) {
        this.accesshistory.add(time);
    }

    /**
     * output the accesshistory of the wikipage
     * @return a clone of the accesshistory cache
     */
    public ArrayList<Long> getAccesshistory() {
        ArrayList<Long> clonedList = new ArrayList<Long>();
        for (Long x: accesshistory) {
            clonedList.add(x);
        }
        return clonedList;
    }

    /**
     * return the page text of the wikipage
     * @return the text of the wikipage
     */
    public String getPagetext(){
        return pagetext;
    }

    /**
     * change the pagetext of the wikipage
     * @param newpagetext the new pagetext needs to be stored inside the wikipage
     */
    public void changePagetext(String newpagetext){
        this.pagetext=newpagetext;
    }

}
