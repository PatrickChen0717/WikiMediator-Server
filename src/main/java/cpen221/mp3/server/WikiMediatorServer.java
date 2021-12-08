package cpen221.mp3.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpen221.mp3.wikimediator.WikiMediator;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class WikiMediatorServer {

    /**
     * DEFAULT_Port: the default port that the serve will connect to is 9056
     * THREAD_LIMIT: the default thread limit of the serve is 32
     * wikiMediator: an instance of wikimediator that the serve used to solve requests
     * clientsCreated: the amount of clients that are currently using the server
     * port: the port that the server is connecting to
     * thread: the maximum number of thread that the server can use
     * local: the history of the requests by all users that this server had handled
     * quit: the indicator of the server whether is running or quitting
     * idlist: the id list of all clients that have connected to the server
     */
    public static final int DEFAULT_PORT = 9056;
    private static final int THREAD_LIMIT = 32;
    private ServerSocket serverSocket;
    private static WikiMediator wikiMediator;
    public int clientsCreated;
    private int port;
    private int n;
    private Object lock=new Object();
    private Object clientlock=new Object();
    public static ArrayList<JsonObject> local=new ArrayList<>();
    public boolean quit=false;

    public ArrayList<String> idlist=new ArrayList<>();
    /**
     * Start a server at a given port number, with the ability to process
     * upto n requests concurrently.
     *
     * @param port the port number to bind the server to, 9000 <= {@code port} <= 9999
     * @param n the number of concurrent requests the server can handle, 0 < {@code n} <= 32
     * @param wikiMediator the WikiMediator instance to use for the server, {@code wikiMediator} is not {@code null}
     */
    public WikiMediatorServer(int port, int n, WikiMediator wikiMediator) {
        this.port=port;
        this.n=n;
        this.wikiMediator=wikiMediator;
        try {
            this.serverSocket=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.clientsCreated=0;
    }

    /**
     * Handle one client connection. Returns when client disconnects.
     *
     * @param socket
     *            socket where client is connected
     * @throws IOException
     *             if connection encounters an error
     */
     private void handle(Socket socket) throws IOException, InterruptedException, TimeoutException {

            System.out.println("client connected");

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            try {
                for (String inputLine = input.readLine(); inputLine != null; inputLine = input.readLine()) {
                    System.err.println("request" + inputLine.toString());

                    JsonObject request = JsonParser.parseString(inputLine).getAsJsonObject();

                    if(request.get("id").getAsString().equals("ten")==false){
                        idlist.add(request.get("id").getAsString());
                    }

                    JsonObject response = getInput(request);
                    System.out.println("Responding:");
                    System.out.println(response.toString());
                    local.add(response);
                    out.println(response.toString());
                    out.flush();
                    System.out.println(quit);

                    if (quit == true) {
                        synchronized (clientlock) {
                            clientsCreated--;
                        }
                        socket.close();
                    }
                }
            } finally {
                out.close();
                input.close();
            }

    }

    /**
     * handle the requests from clients and return different types of responds base on circumstances
     *
     * @param request the commands sent from client that needs to be handled
     * @return the responds of the request
     */
    public JsonObject getInput(JsonObject request) throws IOException, InterruptedException, TimeoutException {
        synchronized (lock) {
            String requestType = request.get("type").getAsString();
            String id = request.get("id").getAsString();

            String timeout = "";
            try {
                timeout = request.get("timeout").getAsString();
            } catch (NullPointerException e) {
                timeout = null;
            }

            System.out.println("timeout:" + timeout);
            if (timeout == null) {//case without timeout
                if (id.equals("ten") && requestType.equals("stop")) {
                    return quitResponse(id, "bye");
                } else {
                    System.out.println("getting input " + id);
                    if (requestType.contains("search")) {
                        System.out.println("searching");
                        String query = request.get("query").getAsString();
                        int limit = request.get("limit").getAsInt();
                        List<String> result = wikiMediator.search(query, limit);
                        return listResponse(id, "success", result);
                    } else if (requestType.contains("getPage")) {
                        String pageTitle = request.get("pageTitle").getAsString();
                        String result = wikiMediator.getPage(pageTitle);
                        System.out.println(result);
                        return StringResponse(id, "success", result);
                    } else if (requestType.contains("zeitgeist")) {
                        int limit = request.get("limit").getAsInt();
                        List<String> result = wikiMediator.zeitgeist(limit);
                        return listResponse(id, "success", result);
                    } else if (requestType.contains("trending")) {
                        int timeLimitInSeconds = request.get("timeLimitInSeconds").getAsInt();
                        int maxItems = request.get("maxItems").getAsInt();
                        List<String> result = wikiMediator.trending(timeLimitInSeconds, maxItems);
                        return listResponse(id, "success", result);

                    } else if (requestType.contains("windowedPeakLoad")) {
                        String timeLimitInSeconds;
                        try {
                            timeLimitInSeconds = request.get("timeLimitInSeconds").getAsString();
                            int result = wikiMediator.windowedPeakLoad(Integer.parseInt(timeLimitInSeconds));
                            return intResponse(id, "success", result);
                        } catch (NullPointerException e) {
                            timeLimitInSeconds = null;
                            int result = wikiMediator.windowedPeakLoad();
                            return intResponse(id, "success", result);
                        }

                    } else {
                        throw new IOException("Unknown type");
                    }
                }

            } else {//timeout case
                if (id.equals("ten") && requestType.equals("stop")) {
                    return quitResponse(id, "bye");
                } else {
                    System.out.println("(time out)getting input " + id);
                    if (requestType.contains("search")) {
                        String query = request.get("query").getAsString();
                        int limit = request.get("limit").getAsInt();
                        final List<String>[] result = new List[]{new ArrayList<>()};

                        ExecutorService executor = Executors.newCachedThreadPool();
                        Callable<Object> task = new Callable<Object>() {
                            public Object call() throws IOException {
                                result[0] = wikiMediator.search(query, limit);
                                return listResponse(id, "success", result[0]);
                            }
                        };
                        Future<Object> future = executor.submit(task);

                        try {
                            future.get(Integer.parseInt(timeout), TimeUnit.SECONDS);
                            return listResponse(id, "success", result[0]);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            return timeoutResponse(id);
                        }
                    } else if (requestType.contains("getPage")) {
                        String pageTitle = request.get("pageTitle").getAsString();
                        ExecutorService timeoutserve = Executors.newCachedThreadPool();
                        final String[] result = new String[1];
                        Callable<Object> task = new Callable<Object>() {
                            public Object call() throws IOException {
                                result[0] = wikiMediator.getPage(pageTitle);
                                return StringResponse(id, "success", result[0]);
                            }
                        };

                        Future<Object> future = timeoutserve.submit(task);

                        try {
                            future.get(Integer.parseInt(timeout), TimeUnit.SECONDS);
                            return StringResponse(id, "success", result[0]);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            return timeoutResponse(id);
                        }
                    } else if (requestType.contains("zeitgeist")) {
                        int limit = request.get("limit").getAsInt();
                        final List<String>[] result = new List[]{new ArrayList<>()};
                        ExecutorService timeoutserve = Executors.newCachedThreadPool();
                        Callable<Object> task = new Callable<Object>() {
                            public Object call() throws IOException {
                                result[0] = wikiMediator.zeitgeist(limit);
                                return listResponse(id, "success", result[0]);
                            }
                        };
                        Future<Object> future = timeoutserve.submit(task);
                        try {
                            future.get(Integer.parseInt(timeout), TimeUnit.SECONDS);
                            return listResponse(id, "success", result[0]);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            return timeoutResponse(id);
                        }

                    } else if (requestType.contains("trending")) {
                        int timeLimitInSeconds = request.get("timeLimitInSeconds").getAsInt();
                        int maxItems = request.get("maxItems").getAsInt();
                        final List<String>[] result = new List[]{new ArrayList<>()};
                        ExecutorService timeoutserve = Executors.newCachedThreadPool();
                        Callable<Object> task = new Callable<Object>() {
                            public Object call() throws IOException {
                                result[0] = wikiMediator.trending(timeLimitInSeconds, maxItems);
                                return listResponse(id, "success", result[0]);
                            }
                        };
                        Future<Object> future = timeoutserve.submit(task);

                        try {
                            future.get(Integer.parseInt(timeout), TimeUnit.SECONDS);
                            return listResponse(id, "success", result[0]);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            return timeoutResponse(id);
                        }

                    } else if (requestType.contains("windowedPeakLoad")) {
                        String timeLimitInSeconds;
                        try {
                            timeLimitInSeconds = request.get("timeLimitInSeconds").getAsString();
                            final int[] result = new int[1];
                            ExecutorService timeoutserve = Executors.newCachedThreadPool();
                            String finalTimeLimitInSeconds = timeLimitInSeconds;
                            Callable<Object> task = new Callable<Object>() {
                                public Object call() throws IOException {
                                    result[0] = wikiMediator.windowedPeakLoad(Integer.parseInt(finalTimeLimitInSeconds));
                                    return intResponse(id, "success", result[0]);
                                }
                            };
                            Future<Object> future = timeoutserve.submit(task);
                            try {
                                future.get(Integer.parseInt(timeout), TimeUnit.SECONDS);
                                return intResponse(id, "success", result[0]);
                            } catch (ExecutionException ex) {
                                ex.printStackTrace();
                            } catch (TimeoutException ex) {
                                return timeoutResponse(id);
                            }
                        } catch (NullPointerException e) {
                            timeLimitInSeconds = null;
                            final int[] result = new int[1];
                            ExecutorService timeoutserve = Executors.newCachedThreadPool();
                            Callable<Object> task = new Callable<Object>() {
                                public Object call() throws IOException {
                                    result[0] = wikiMediator.windowedPeakLoad();
                                    return intResponse(id, "success", result[0]);
                                }
                            };
                            Future<Object> future = timeoutserve.submit(task);
                            try {
                                future.get(Integer.parseInt(timeout), TimeUnit.SECONDS);
                                return intResponse(id, "success", result[0]);
                            } catch (ExecutionException ex) {
                                e.printStackTrace();
                            } catch (TimeoutException ex) {
                                return timeoutResponse(id);
                            }
                        }
                    } else if (requestType.contains("shortestPath")) {
                        System.out.println("processing shortestpath");
                        String pageTitle1 = request.get("pageTitle1").getAsString();
                        String pageTitle2 = request.get("pageTitle2").getAsString();

                        final List<String>[] result = new List[]{new ArrayList<>()};
                        ExecutorService timeoutserve = Executors.newCachedThreadPool();
                        Callable<Object> task = new Callable<Object>() {
                            public Object call() throws IOException, InterruptedException, TimeoutException {
                                System.out.println("checker");
                                result[0] = wikiMediator.shortestPath(pageTitle1, pageTitle2, request.get("timeout").getAsInt());
                                return listResponse(id, "success", result[0]);
                            }
                        };
                        Future<Object> future = timeoutserve.submit(task);
                        try {
                            future.get(Integer.parseInt(timeout), TimeUnit.SECONDS);
                            return listResponse(id, "success", result[0]);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            return timeoutResponse(id);
                        }
                    } else {
                        throw new IOException("Unknown type");
                    }
                }

            }
            return null;
        }
    }

    /**
     * Run the server, listening for connections and handling them.
     *
     * @throws IOException
     *             if the main server socket is broken
     */
    public void serve() throws IOException, InterruptedException {
        System.out.println("serve start");
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            // create a new thread to handle that client
            Thread handler = new Thread(new Runnable() {
                public void run() {
                        try {
                            try {
                                synchronized (clientlock){
                                    clientsCreated++;
                                }

                                handle(socket);
                            } finally {
                                socket.close();

                            }
                        } catch (IOException | InterruptedException | TimeoutException ioe) {
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

    /**
     * Start a WikiMediatorServer running on the default port.
     */
    public static void main(String[] args) {
        WikiMediator wikiMediator = new WikiMediator(5, 1);
        try {
            WikiMediatorServer server = new WikiMediatorServer(DEFAULT_PORT, THREAD_LIMIT, wikiMediator);
            server.serve();
        } catch (IOException | InterruptedException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * return a string response to user
     *
     * @param id the id of the client that show reply to
     * @param status the indicator whether the request success
     * @param response the response that should reply to the client
     */
    private JsonObject StringResponse(String id, String status, String response) {
        JsonObject returnJson = new JsonObject();

        returnJson.addProperty("id", id);
        returnJson.addProperty("status", status);
        returnJson.addProperty("response", response);

        return returnJson;
    }

    /**
     * return an int response to user
     *
     * @param id the id of the client that show reply to
     * @param status the indicator whether the request success
     * @param response the response that should reply to the client
     */
    private JsonObject intResponse(String id, String status, int response) {
        JsonObject returnJson = new JsonObject();

        returnJson.addProperty("id", id);
        returnJson.addProperty("status", status);
        returnJson.addProperty("response", response);

        return returnJson;
    }

    /**
     * return a list response to user
     *
     * @param id the id of the client that show reply to
     * @param status the indicator whether the request success
     * @param responseArray the responses that should reply to the client
     */
    private JsonObject listResponse(String id, String status, List<String> responseArray) {
        JsonObject result = new JsonObject();
        JsonArray jsonResult = new JsonArray();
        for (String response: responseArray) {
            jsonResult.add(response);
        }

        result.addProperty("id", id);
        result.addProperty("status", status);
        result.add("response", jsonResult);

        return result;
    }

    /**
     * return a quit response to user
     *
     * @param id the id of the client that show reply to
     * @param response the responses that should reply to the client
     */
    private JsonObject quitResponse(String id, String response) {
        JsonObject returnJson = new JsonObject();

        returnJson.addProperty("id", id);
        returnJson.addProperty("response", response);
        quit=true;
        return returnJson;
    }

    /**
     * return a timeout response to user
     *
     * @param id the id of the client that show reply to
     */
    private JsonObject timeoutResponse(String id) {
        JsonObject returnJson = new JsonObject();
        returnJson.addProperty("id", id);
        returnJson.addProperty("status", "failed");
        returnJson.addProperty("response", "Operation timed out");
        return returnJson;
    }

    /**
     * @return the local cache of the server that contains all past responses
     */
    public static ArrayList<JsonObject> getlocal(){
        return local;
    }



}
