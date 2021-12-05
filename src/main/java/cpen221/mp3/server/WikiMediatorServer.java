package cpen221.mp3.server;

import cpen221.mp3.fsftbuffer.ObjectNotFoundException;
import cpen221.mp3.wikimediator.WikiMediator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class WikiMediatorServer {
    private ServerSocket serverSocket;
    private static WikiMediator wikiMediator;
    private static int port;
    private static int n;
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
            this.port=port;
            this.n=n;
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


            // each request is a single line containing a number
            try{
                for (String id = in.readLine(); id != null; id = in.readLine()) {
                    System.err.println("id: " + id);
                    if(id.equals("ten")){
                        throw new quitException(new String("bye"));
                    }
                    else{
                        for (String line = in.readLine(); line != null; line = in.readLine()) {
                            System.err.println("type: " + line);
                            try {
                                //request type 1: search
                                if (line.equals("search")) {
                                    for (String pagetitle = in.readLine(); line != null; line = in.readLine()) {
                                        System.err.println("query: " + pagetitle);
                                        for (String limit = in.readLine(); line != null; line = in.readLine()) {
                                            System.err.println("limit: " + limit);
                                            System.out.println("response" + wikiMediator.search(pagetitle, Integer.parseInt(limit)));
                                        }
                                    }
                                }
                                //request type 2: zeitgeist (int limit)
                                if (line.equals("zeitgeist")) {
                                    for (String limit = in.readLine(); line != null; line = in.readLine()) {
                                        System.err.println("limit: " + limit);
                                        System.out.println("response" + wikiMediator.zeitgeist(Integer.parseInt(limit)));
                                    }
                                }
                                //request type 3: zeitgeist (int limit)
                                if (line.equals("getPage")) {
                                    for (String title = in.readLine(); line != null; line = in.readLine()) {
                                        System.err.println("title: " + title);
                                        System.out.println("response" + wikiMediator.getPage(title));
                                    }
                                }
                                //request type 4: trending(int timeLimitInSeconds, int maxItems)
                                if (line.equals("trending")) {
                                    for (String timeLimitInSeconds = in.readLine(); line != null; line = in.readLine()) {
                                        System.err.println("timeLimitInSeconds: " + timeLimitInSeconds);
                                        for (String maxItems = in.readLine(); line != null; line = in.readLine()) {
                                            System.err.println("maxItems: " + maxItems);
                                            System.out.println("response" + wikiMediator.trending(Integer.parseInt(timeLimitInSeconds), Integer.parseInt(maxItems)));
                                        }
                                    }
                                }
                                //request type 5: windowedPeakLoad(int timeWindowInSeconds)
                                if (line.equals("windowedPeakLoad")) {
                                    for (String timeLimitInSeconds = in.readLine(); line != null; line = in.readLine()) {
                                        System.err.println("timeLimitInSeconds: " + timeLimitInSeconds);
                                        if (timeLimitInSeconds.equals("")) {
                                            System.out.println("response" + wikiMediator.windowedPeakLoad());
                                        } else {
                                            System.out.println("response" + wikiMediator.windowedPeakLoad(Integer.parseInt(timeLimitInSeconds)));
                                        }
                                    }
                                }
                            } catch (NumberFormatException e) {
                                // complain about ill-formatted request
                                System.err.println("reply: command not found");
                                out.print("err\n");
                            }
                        }
                    }
                }
            }
            catch(quitException x){
                System.out.println("Thank you for using");
            }


                    // important! our PrintWriter is auto-flushing, but if it were
                    // not:
                    // out.flush();
    }

    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            // create a new thread to handle that client
            Thread handler = new Thread(new Runnable() {
                public void run() {
                    try {
                        try {
                            handle(socket);
                        } finally {
                            socket.close();
                        }
                    } catch (IOException ioe) {
                        // this exception wouldn't terminate serve(),
                        // since we're now on a different thread, but
                        // we still need to handle it
                        ioe.printStackTrace();
                    }
                }
            });
            // start the thread
            handler.start();
        }
    }

    public static void main(String[] args) {
        try {
            WikiMediatorServer server = new WikiMediatorServer(port,n,wikiMediator);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
