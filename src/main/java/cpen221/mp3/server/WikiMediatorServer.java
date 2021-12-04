package cpen221.mp3.server;

import cpen221.mp3.wikimediator.WikiMediator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class WikiMediatorServer {
    private ServerSocket serverSocket;
    private WikiMediator wikiMediator;
    /**
     * Start a server at a given port number, with the ability to process
     * upto n requests concurrently.
     *
     * @param port the port number to bind the server to, 9000 <= {@code port} <= 9999
     * @param n the number of concurrent requests the server can handle, 0 < {@code n} <= 32
     * @param wikiMediator the WikiMediator instance to use for the server, {@code wikiMediator} is not {@code null}
     */
    public WikiMediatorServer(int port, int n, WikiMediator wikiMediator) {
        try {
            this.wikiMediator=wikiMediator;
            serverSocket=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        System.err.println("client connected");

        // get the socket's input stream, and wrap converters around it
        // that convert it from a byte stream to a character stream,
        // and that buffer it so that we can read a line at a time
        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // similarly, wrap character=>bytestream converter around the
        // socket output stream, and wrap a PrintWriter around that so
        // that we have more convenient ways to write Java primitive
        // types to it.
        PrintWriter out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream()), true);

        try {
            // each request is a single line containing a number

                for (String id = in.readLine(); id != null; id = in.readLine()) {
                    System.err.println("id: " + id);
                    for (String line = in.readLine(); line != null; line = in.readLine()) {
                        System.err.println("type: " + line);
                        try {
                            //request type 1: search
                            if(line.equals("search")){
                                for (String pagetitle = in.readLine(); line != null; line = in.readLine()) {
                                    System.err.println("query: " + pagetitle);
                                    for (String limit = in.readLine(); line != null; line = in.readLine()) {
                                        System.err.println("limit: " + limit);
                                        System.out.println("response"+wikiMediator.search(pagetitle, Integer.parseInt(limit)));
                                    }
                                }
                            }
                            //request type 1: search
                            if(line.equals("zeitgeist")){
                                for (String pagetitle = in.readLine(); line != null; line = in.readLine()) {
                                    System.err.println("query: " + pagetitle);
                                    for (String limit = in.readLine(); line != null; line = in.readLine()) {
                                        System.err.println("limit: " + limit);
                                        System.out.println("response"+wikiMediator.search(pagetitle, Integer.parseInt(limit)));
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            // complain about ill-formatted request
                            System.err.println("reply: err");
                            out.print("err\n");
                        }
                    }

                }


                try {
                    if(line.equals("search")){

                        WikiMediator wikiMediator=new WikiMediator();
                    }
                } catch (NumberFormatException e) {
                    // complain about ill-formatted request
                    System.err.println("reply: err");
                    out.print("err\n");
                }
                // important! our PrintWriter is auto-flushing, but if it were
                // not:
                // out.flush();


        } finally {
            out.close();
            in.close();
        }
    }
}
