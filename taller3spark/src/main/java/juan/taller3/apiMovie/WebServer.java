package juan.taller3.apiMovie;

import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.*;

import java.io.*;

/**
 * Web server class to use the web application
 * 
 * @author juan cepeda
 */
public class WebServer {

    private static final int PORT = 35000;
    private static WebServer _instance = getInstace();
    private static Map<String, WebService> handlers = new HashMap<String, WebService>();
    private static final APIMovies apiMovie = new APIMovies();

    /**
     * method that returns the instance of this class
     * 
     * @return the instance of this class
     */
    public static WebServer getInstace() {
        return _instance;
    }

    /**
     * Method that start the web server
     * 
     * @throws IOException
     */
    public static void startServer() throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine = "";
            boolean readingFirst = true;
            String petition = "";
            String method = "";
            Boolean mv = false;

            while ((inputLine = in.readLine()) != null) {

                if (readingFirst) {
                    method = inputLine.split(" ")[0];
                    petition = inputLine.split(" ")[1];
                    break;
                }
                if (!in.ready()) {
                    break;
                }
            }

            mv = petition.contains("/film?name=");
            try {
                String query = extractQuery(petition);

                if (isSparkRequest(petition)) {
                    String newUri = removeSparkPrefix(petition);
                    outputLine = handleSparkRequest(newUri, query, clientSocket.getOutputStream());
                } else {
                    outputLine = (mv)
                            ? movieInfo(query, clientSocket.getOutputStream())
                            : petitionPage(petition, clientSocket.getOutputStream());
                }
            } catch (URISyntaxException e) {
                System.out.println("Invalid URI: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error handling request: " + e.getMessage());
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String extractQuery(String petition) throws URISyntaxException {
        URI myURI = new URI(petition);
        String query = myURI.getQuery();
        return (query != null) ? query.split("=")[1] : "";
    }

    private static boolean isSparkRequest(String petition) {
        return petition.startsWith("/spark");
    }

    private static String removeSparkPrefix(String petition) {
        return petition.replace("/spark", "");
    }

    private static String handleSparkRequest(String newUri, String query, OutputStream outputStream) {
        try {
            URI uri = new URI(newUri);
            String path = uri.getPath();

            if (handlers.containsKey(path)) {
                return petitionPage(handlers.get(path).handle(query), outputStream).replace("{name}", query);
            } else if (path.contains("css") || path.contains("jpg") || path.contains("js")) {
                return petitionPage(path, outputStream);
            } else {
                return errorPage("/NotFound.html", outputStream);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n";
        }
    }

    private static String ok() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: ";
    }

    private static String not() {
        return "HTTP/1.1 404 NOT FOUND\r\n"
                + "Content-Type: ";
    }

    private static String errorPage(String file, OutputStream op) {
        return not() + getMimeType(file) + "\r\n"
                + "\r\n"
                + getStaticFile(file, op);
    }

    private static String petitionPage(String filePetition, OutputStream op) {

        return ok() + getMimeType(filePetition) + "\r\n"
                + "\r\n"
                + getStaticFile(filePetition, op);
    }

    /**
     * return a html structure with some movie information
     * 
     * @param name the name of the movie
     * @return a html structure with movie information
     */
    private static String movieInfo(String name, OutputStream ops) {
        try {
            JsonObject resp = apiMovie.searchMovie(name);
            JsonElement title = resp.get("Title");
            JsonElement poster = resp.get("Poster");
            JsonElement director = resp.get("Director");
            JsonElement plot = resp.get("Plot");

            String outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type:text/html\r\n"
                    + "\r\n"
                    + getStaticFile("/movieInfo.html", ops)
                            .replace("{Title}", title.toString())
                            .replace("\"{Poster}\"", poster.toString())
                            .replace("{Directors}", director.toString())
                            .replace("{Plot}", plot.toString());

            return outputLine;
        } catch (Exception e) {
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n";
        }
    }

    /**
     * method that returns the principal html page
     * 
     * @return the principal page of the application
     */
    private static String mainPage(String file, OutputStream ops) {
        String contentType = getMimeType(file);
        String content = getStaticFile(file, ops);

        String outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type:" + contentType + "\r\n"
                + "\r\n"
                + content;

        return outputLine;
    }

    private static String getMimeType(String file) {
        if (file.endsWith(".html") || file.endsWith("/")) {
            return "text/html";
        } else if (file.endsWith(".css")) {
            return "text/css";
        } else if (file.endsWith(".js")) {
            return "application/javascript";
        } else if (file.endsWith(".jpg")) {
            return "image/jpeg";
        } else {
            return "text/plain";
        }
    }

    /**
     * returns the static file related with the request
     * 
     * @return string with all information insite the file
     */
    private static String getStaticFile(String file, OutputStream ops) {
        Path path = (file.equals("/"))
                ? Paths.get(getStaticFilesDirectory() + "/movie.html")
                : Paths.get(getStaticFilesDirectory() + file);

        try {
            Charset charset = Charset.forName("UTF-8");
            StringBuilder outputLine = new StringBuilder();
            byte[] bytes;

            if (file.endsWith(".jpg")) {
                bytes = getAnImage(file);
                String response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: image/jpeg\r\n" +
                        "Content-Length: " + bytes.length + "\r\n" +
                        "\r\n";
                ops.write(response.getBytes());
                ops.write(bytes);
            } else {
                try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputLine.append(line).append("\n");
                    }
                }
            }

            return outputLine.toString();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return "HTTP/1.1 404 Not Found\r\n\r\n";
        }
    }

    /**
     * return the bytes of an image
     * 
     * @param file the route of the file to return to the browser
     * @return an array of bytes
     */
    private static byte[] getAnImage(String file) {

        Path image = Paths.get("target/classes/public/static" + file);

        try {
            return Files.readAllBytes(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void handleGetRequest(String path, WebService handler) {
        handlers.put(path, handler);
    }

    private static void handlePostRequest(String path, WebService handler) {

        handlers.put(path, handler);
    }

    // public static void post(String path, Function<String, String> handler) {
    // postHandlers.put(path, handler);
    // }

    // private static void handleRequest(Socket clientSocket) {
    // try (BufferedReader in = new BufferedReader(new
    // InputStreamReader(clientSocket.getInputStream()));
    // PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

    // String requestLine = in.readLine();
    // String[] requestParts = requestLine.split(" ");
    // String method = requestParts[0];
    // String path = requestParts[1];

    // String responseContent = null;
    // if ("GET".equals(method)) {
    // Function<String, String> handler = handlers.getOrDefault(path,
    // defaultGetHandler());
    // responseContent = handler.apply(path);
    // } else if ("POST".equals(method)) {
    // Function<String, String> handler = handlers.getOrDefault(path,
    // defaultPostHandler());
    // // Implementar la lógica para manejar las solicitudes POST según tus
    // necesidades
    // }

    // sendResponse(responseContent, out);

    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // private static Function<String, String> defaultGetHandler() {
    // return path -> "<h1>404 Not Found</h1>";
    // }

    // private static Function<String, String> defaultPostHandler() {
    // return path -> "<h1>404 Not Found</h1>";
    // }

    // public static void setStaticFilesDirectory(String directory) {
    // String path = "target/classes/public/static";
    // path = directory;
    // }

    public static String getStaticFilesDirectory() {
        return "target/classes/public/static";
    }

}
