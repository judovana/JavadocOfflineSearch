/* ServerLauncher.java
 Copyright (C) 2011,2012 Red Hat, Inc.

 This file is part of IcedTea.

 IcedTea is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as published by
 the Free Software Foundation, version 2.

 IcedTea is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with IcedTea; see the file COPYING.  If not, write to
 the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 02110-1301 USA.

 Linking this library statically or dynamically with other modules is
 making a combined work based on this library.  Thus, the terms and
 conditions of the GNU General Public License cover the whole
 combination.

 As a special exception, the copyright holders of this library give you
 permission to link this library with independent modules to produce an
 executable, regardless of the license terms of these independent
 modules, and to copy and distribute the resulting executable under
 terms of your choice, provided that you also meet, for each linked
 independent module, the terms and conditions of the license of that
 module.  An independent module is a module which is not derived from
 or based on this library.  If you modify this library, you may extend
 this exception to your version of the library, but you are not
 obligated to do so.  If you do not wish to do so, delete this
 exception statement from your version.
 */
package javadocofflinesearch.server;

import java.io.File;
import java.net.ServerSocket;
import javadocofflinesearch.lucene.MainIndex;

/**
 * wrapper around tiny http server to separate lunch configurations and servers.
 * to allow terminations and stuff around.
 */
public class ServerLauncher implements Runnable {

    private boolean running;
    private final Integer port;
    private ServerSocket serverSocket;
    private final File cache;
    private final File config;

    public ServerLauncher(int port, File cache, File config) {
        this.cache = cache;
        this.config = config;
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
