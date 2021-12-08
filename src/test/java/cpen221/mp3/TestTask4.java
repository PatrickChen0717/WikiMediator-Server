package cpen221.mp3;

import com.google.gson.JsonObject;
import cpen221.mp3.client.WikiMediatorClient;
import cpen221.mp3.server.WikiMediatorServer;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

public class TestTask4 {
    //WikiMediatorClient client1 = new WikiMediatorClient("127.0.0.2", WikiMediatorServer.DEFAULT_PORT);
    //WikiMediatorClient client = new WikiMediatorClient("127.0.0.1", WikiMediatorServer.DEFAULT_PORT);
    //WikiMediatorClient client2 = new WikiMediatorClient("127.0.0.2", WikiMediatorServer.DEFAULT_PORT);

    @Test
    public void test1() throws InterruptedException, IOException {
        WikiMediatorClient client = new WikiMediatorClient("127.0.0.2", WikiMediatorServer.DEFAULT_PORT);

        client.sendRequest(new String[]{"1","search","ubc","5"});
        client.getReply();
        JsonObject expect1 = new JsonObject();
        expect1.addProperty("id", "1");
        expect1.addProperty("status", "success");
        expect1.addProperty("response", "[\"University of British Columbia\",\"Talk:UBC (disambiguation)\",\"UBC Thunderbirds\",\"UBC Exchange\",\"UBC Thunderbirds football\"]");
        //Assertions.assertEquals(expect1.toString(),client.getReply());
        //client.getReply();

        client.sendRequest(new String[]{"2","getPage","ubc"});
        JsonObject expect2 = new JsonObject();
        expect2.addProperty("id", "2");
        expect2.addProperty("status", "success");
        expect2.addProperty("response", "#REDIRECT [[University of British Columbia]] {{R from other capitalisation}}");
        Assertions.assertEquals(expect2.toString(),client.getReply());

       // client.sendRequest(new String[]{"ten","stop"});
        //JsonObject expect3 = new JsonObject();
        //expect3.addProperty("id", "ten");
       // expect3.addProperty("response", "bye");
      //  Assertions.assertEquals(expect3.toString(),client.getReply());

    }

    //test the timeout search function of search by setting limit to 50
    @Test
    public void test2() throws InterruptedException, IOException {
        WikiMediatorClient client1 = new WikiMediatorClient("127.0.0.3", WikiMediatorServer.DEFAULT_PORT);

        client1.sendRequest(new String[]{"1","search","ubc","500","0"});
        JsonObject expect = new JsonObject();
        expect.addProperty("id", "1");
        expect.addProperty("status", "failed");
        expect.addProperty("response", "Operation timed out");
        Assertions.assertEquals(expect.toString(),client1.getReply());
        //client1.sendRequest(new String[]{"ten","stop"});
        //client1.getReply();
    }


    //check whether zeitgeist can return most frequent request
    @Test
    public void test3() throws InterruptedException, IOException {

        WikiMediatorClient client1 = new WikiMediatorClient("127.0.0.3", WikiMediatorServer.DEFAULT_PORT);
        client1.sendRequest(new String[]{"1","getPage","apple"});
        client1.getReply();
        client1.sendRequest(new String[]{"2","getPage","iphone"});
        client1.getReply();
        client1.sendRequest(new String[]{"3","search","iphone","4","2"});
        client1.getReply();
        client1.sendRequest(new String[]{"4","search","iphone","3"});
        client1.getReply();
        client1.sendRequest(new String[]{"5","search","apple","5"});
        client1.getReply();
        client1.sendRequest(new String[]{"6","zeitgeist","1"});
        client1.getReply();

        JsonObject expect = new JsonObject();
        expect.addProperty("id", "6");
        expect.addProperty("status", "success");
        expect.addProperty("response", "[\"iphone\"]");
        //client1.getReply();
        //Assertions.assertEquals(expect.toString(),client1.getReply());
        //answer: {"id":"6","status":"success","response":["iphone"]}
    }

    @Test
    public void test4() throws InterruptedException, IOException {
        WikiMediatorClient client2 = new WikiMediatorClient("127.0.0.2", WikiMediatorServer.DEFAULT_PORT);
        //timeout expect
        client2.sendRequest(new String[]{"1","getPage","apple","0"});
        JsonObject expect = new JsonObject();
        expect.addProperty("id", "1");
        expect.addProperty("status", "failed");
        expect.addProperty("response", "Operation timed out");
        Assertions.assertEquals(expect.toString(),client2.getReply());

        //not timeout expect
        client2.sendRequest(new String[]{"2","getPage","apple","10"});
        client2.getReply();
        JsonObject expect1 = new JsonObject();
        expect1.addProperty("id", "2");
        expect1.addProperty("status", "sucess");
        //expect1.addProperty("response", "Operation timed out");


    }

    @Test
    public void test5() throws InterruptedException, IOException {
        WikiMediatorClient client2 = new WikiMediatorClient("127.0.0.8", WikiMediatorServer.DEFAULT_PORT);
        client2.sendRequest(new String[]{"1","getPage","apple","0"});
        client2.getReply();
        client2.sendRequest(new String[]{"2","getPage","apple","10"});
        client2.getReply();
        Thread.sleep(2000);
        client2.sendRequest(new String[]{"3","search","iphone","4","2"});
        client2.getReply();
        client2.sendRequest(new String[]{"4","search","iphone","3"});
        client2.getReply();
        Thread.sleep(1000);
        client2.sendRequest(new String[]{"5","search","apple","5"});
        client2.getReply();
        client2.sendRequest(new String[]{"6","zeitgeist","2"});
        client2.getReply();
        client2.sendRequest(new String[]{"7","windowedPeakLoad","2","2"});

        JsonObject expect1 = new JsonObject();
        expect1.addProperty("id", "7");
        expect1.addProperty("status", "success");
        expect1.addProperty("response", 17);
        Assertions.assertEquals(expect1.toString(),client2.getReply());
    }

    //test timeout and no timeout method of zeitgeist
    @Test
    public void test6() throws InterruptedException, IOException {
        WikiMediatorClient client2 = new WikiMediatorClient("127.0.0.2", WikiMediatorServer.DEFAULT_PORT);
        client2.sendRequest(new String[]{"1","getPage","ubc","1"});
        client2.getReply();
        for(int i=2;i<10;i++){
            client2.sendRequest(new String[]{String.valueOf(i),"getPage","apple","10"});
            client2.getReply();
        }
        client2.sendRequest(new String[]{"10","zeitgeist","2","0"});
        JsonObject expect1 = new JsonObject();
        expect1.addProperty("id", "10");
        expect1.addProperty("status", "failed");
        expect1.addProperty("response", "Operation timed out");
        Assertions.assertEquals(expect1.toString(),client2.getReply());

        client2.sendRequest(new String[]{"10","zeitgeist","2","5"});
        JsonObject expect2 = new JsonObject();
        expect2.addProperty("id", "10");
        expect2.addProperty("status", "failed");
        expect2.addProperty("response", "Operation timed out");
        //Assertions.assertEquals(expect2.toString(),client2.getReply());
        //WikiMediatorClient.getReply={"id":"10","status":"success","response":["apple","ubc"]}
    }

    //test timeout and no timeout method of trending
    @Test
    public void test7() throws InterruptedException, IOException {
        WikiMediatorClient client2 = new WikiMediatorClient("127.0.0.2", WikiMediatorServer.DEFAULT_PORT);
        client2.sendRequest(new String[]{"1","getPage","ubc","1"});
        client2.getReply();
        for(int i=2;i<10;i++){
            client2.sendRequest(new String[]{String.valueOf(i),"getPage","apple","10"});
            client2.getReply();
        }
        client2.sendRequest(new String[]{"10","trending","2","5","10"});
        client2.getReply();
        JsonObject expect1 = new JsonObject();
        expect1.addProperty("id", "10");
        expect1.addProperty("status", "failed");
        expect1.addProperty("response", "Operation timed out");
        //Assertions.assertEquals(expect1.toString(),client2.getReply());
        //WikiMediatorClient.getReply={"id":"10","status":"success","response":["apple","ubc"]}

        client2.sendRequest(new String[]{"11","trending","2","5","0"});
        JsonObject expect2 = new JsonObject();
        expect2.addProperty("id", "11");
        expect2.addProperty("status", "failed");
        expect2.addProperty("response", "Operation timed out");
        Assertions.assertEquals(expect2.toString(),client2.getReply());
    }

    //check the timeout and untimeout version of shortestPath
    @Test
    public void test8() throws InterruptedException, IOException {
        WikiMediatorClient client2 = new WikiMediatorClient("127.0.0.2", WikiMediatorServer.DEFAULT_PORT);
        JsonObject expect2 = new JsonObject();
        expect2.addProperty("id", "1");
        expect2.addProperty("status", "failed");
        expect2.addProperty("response", "Operation timed out");
        client2.sendRequest(new String[]{"1","shortestPath","Apple","Bad Apple","1"});
        Assertions.assertEquals(expect2.toString(),client2.getReply());

        client2.sendRequest(new String[]{"2","shortestPath","Apple","Bad Apple","60"});
        client2.getReply();
    }
}
