package cpen221.mp3.client;

import com.google.gson.JsonObject;
import cpen221.mp3.server.WikiMediatorServer;
import java.io.*;
import java.net.Socket;

public class WikiMediatorClient {
    private Object lock=new Object();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * the constructor of the wikimediator client
     * @param hostname name of the host
     * @param port the port of the server that client will connect to
     * @throws IOException if fail to create socket
     */
    public WikiMediatorClient(String hostname, int port) throws IOException {
        this.socket = new Socket(hostname, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Start a client and connect to server
     */
    public static void main(String[] args) {
        try {
            WikiMediatorClient client = new WikiMediatorClient("127.0.0.1", WikiMediatorServer.DEFAULT_PORT);
            WikiMediatorClient client1 = new WikiMediatorClient("127.0.0.2", WikiMediatorServer.DEFAULT_PORT);
            WikiMediatorClient client2= new WikiMediatorClient("127.0.0.3", WikiMediatorServer.DEFAULT_PORT);
            client.sendRequest(new String[]{"1","search","ubc","50","10"});
            client.getReply();
            client.sendRequest(new String[]{"2","getPage","ubc"});
            client.getReply();
            client.sendRequest(new String[]{"3","getPage","FBI"});
            client.getReply();
            client.sendRequest(new String[]{"4","zeitgeist","4"});
            client.getReply();
            client.sendRequest(new String[]{"ten","stop"});
            client.getReply();

            client.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * get a reply from the server for each requests
     * @return the reply of the sent response
     * @throws IOException if unexpected reply was gotten
     */
    public String getReply() throws IOException {
        String reply = in.readLine();
        if (reply == null) {
            throw new IOException("null reply");
        }

        try {
            System.out.println("WikiMediatorClient.getReply="+reply);
            return reply;
        }
        catch (NumberFormatException nfe) {
            throw new IOException("misformatted reply: " + reply);
        }
    }

    /**
     *  convert the clients' requests into json object and send to server
     * @param array the clients' requests
     * @throws IOException if error occur while parsing
     */
    public void sendRequest(String[] array) throws IOException {
        synchronized (lock) {
            JsonObject input = new JsonObject();
            input.addProperty("id", array[0]);
            input.addProperty("type", array[1]);
            if(array[0].equals("ten")){
                input.addProperty("type", array[1]);
            }
            else{
                if(array[1].equals("search")){
                    input.addProperty("query", array[2]);
                    input.addProperty("limit", array[3]);
                    if(array.length==5){
                        input.addProperty("timeout", array[4]);
                    }

                }
                else if(array[1].equals("getPage")){
                    input.addProperty("pageTitle", array[2]);
                    if(array.length==4){
                        input.addProperty("timeout", array[3]);
                    }

                }
                else if(array[1].equals("zeitgeist")){
                    input.addProperty("limit", array[2]);
                    if(array.length==4){
                        input.addProperty("timeout", array[3]);
                    }

                }
                else if(array[1].equals("trending")){
                    input.addProperty("timeLimitInSeconds", array[2]);
                    input.addProperty("maxItems", array[3]);
                    if(array.length==5){
                        input.addProperty("timeout", array[4]);
                    }

                }
                else if(array[1].equals("windowedPeakload")){
                    if(array.length==4){
                        input.addProperty("timeLimitInSeconds", array[2]);
                        input.addProperty("timeout", array[3]);
                    }else if(array.length==3){
                        input.addProperty("timeLimitInSeconds", array[2]);
                    }
                }
                else if(array[1].equals("shortestPath")){
                        input.addProperty("pageTitle1", array[2]);
                        input.addProperty("pageTitle2", array[3]);
                        input.addProperty("timeout", array[4]);
                }
            }


            System.out.println("input: " + input);
            out.print(input.toString() + "\n");
            out.flush();
        }
    }

    /**
     * Closes the client's connection to the server.
     * This client is now "closed". Requires this is "open".
     *
     * @throws IOException if close fails
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

}

