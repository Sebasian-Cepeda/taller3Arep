package juan.taller3;

import java.io.IOException;

import juan.taller3.apiMovie.WebServer;

/**
 * Main class to start the application
 * 
 * @author Juan cepeda
 * 
 * 
 */
public class Main {

    public static void main(String[] args) {
        try {

            WebServer.handleGetRequest("/hello", (path) -> {
                return "/hello.html";
            });

            WebServer.startServer();
        } catch (IOException e) {
            System.out.println("server error: " + e.getMessage());
        }

    }
}