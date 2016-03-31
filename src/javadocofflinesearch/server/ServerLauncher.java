package javadocofflinesearch.server;

import java.io.File;
import java.net.ServerSocket;

/**
 * wrapper around tiny http server to separate lunch configurations and servers.
 * to allow terminations and stuff around.
 */
public class ServerLauncher implements Runnable {

    private boolean running;
    private final Integer port;
    private ServerSocket serverSocket;

    public ServerLauncher(int port) {
        this.port = port;
        System.err.println("http://localhost:" + port);
        System.out.println("http://localhost:" + port);
    }

    public boolean isRunning() {
        return running;
    }

    public Integer getPort() {
        return port;
    }

    public void run() {
        running = true;
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                TinyHttpdImpl server = new TinyHttpdImpl(serverSocket.accept(), false);
                server.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            running = false;
        }
    }

    private String sanitizeResource(String resource) {
        if (resource == null) {
            resource = "";
        }
        if (resource.trim().length() > 0 && !resource.startsWith("/")) {
            resource = "/" + resource;
        }
        return resource;
    }

    public void stop() {
        this.running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.err.println("stopped : " + port);
    }

}
