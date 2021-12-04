package cpen221.mp3.wikimediator;

import org.fastily.jwiki.core.Wiki;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

public class WikiMediator{

    private ArrayList<wikipage> pagelist=new ArrayList<wikipage>();
    private Wiki wiki;

    public WikiMediator(int capacity,int stalenessInterval){
        this.wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
    }

    public List<String> search(String query,int limit){
        return wiki.search(query, limit);
    }

    public String getPage(String pageTitle){
        wikipage newpage=new wikipage(pageTitle);
        newpage.incrementAccesscount();
        pagelist.add(newpage);
        return wiki.getPageText(pageTitle);
    }

    public List<String> zeitgeist(int limit){
        List<wikipage> list=pagelist;
        list.sort(Comparator.comparing(wikipage::getAccesscount).reversed());
        List<String> result=list.stream().map(wikipage::getPagetitle).collect(Collectors.toList());
        for(int i=0;i<limit-list.size();i++){
            result.remove(list.size()-1);
        }

        return result;
    }

    public List<String> trending(int timeLimitInSeconds, int maxItems){
        ArrayList<wikipage> list=pagelist;

        for(int i=0;i<list.size();i++){
            list.get(i).getAccesshistory().stream().filter((x->x>=(System.currentTimeMillis()-timeLimitInSeconds*1000))).collect(Collectors.toList());
        }

        list.sort(Comparator.comparing(wikipage::getAccesscount).reversed());

        for(int i=0;i<maxItems-list.size();i++){
            list.remove(list.size()-1);
        }

        return list.stream().map(x->x.getPagetitle()).collect(Collectors.toList());
    }

    public int windowedPeakLoad(int timeWindowInSeconds){
        ArrayList<wikipage> list=pagelist;
        ArrayList<Integer> allaccesshistory=new ArrayList<>();

        for(int i=0;i<list.size();i++){
            allaccesshistory.addAll(list.get(i).getAccesshistory());
        }

        int maximumrequest=0;


        for(int i=0;i<allaccesshistory.size();i++){
            int starttime=allaccesshistory.get(i);
            int count=0;
            for(int j=0;j<allaccesshistory.size();j++){
                if(allaccesshistory.get(j)<=starttime+timeWindowInSeconds*1000 && allaccesshistory.get(j)>=starttime){
                    count++;
                }
            }
            if(count>maximumrequest){
                count=maximumrequest;
            }
        }

        return maximumrequest;
    }

    public int windowedPeakLoad(){
        ArrayList<wikipage> list=pagelist;
        ArrayList<Integer> allaccesshistory=new ArrayList<>();

        for(int i=0;i<list.size();i++){
            allaccesshistory.addAll(list.get(i).getAccesshistory());
        }

        int maximumrequest=0;


        for(int i=0;i<allaccesshistory.size();i++){
            int starttime=allaccesshistory.get(i);
            int count=0;
            for(int j=0;j<allaccesshistory.size();j++){
                if(allaccesshistory.get(j)<=starttime+30*1000 && allaccesshistory.get(j)>=starttime){
                    count++;
                }
            }
            if(count>maximumrequest){
                count=maximumrequest;
            }
        }

        return maximumrequest;
    }
}
