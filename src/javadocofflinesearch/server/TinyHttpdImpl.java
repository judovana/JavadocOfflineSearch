/* TinyHttpdImpl.java
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.StringTokenizer;
import javadocofflinesearch.formatters.SearchableHtmlFormatter;
import javadocofflinesearch.lucene.MainIndex;
import javadocofflinesearch.tools.HardcodedDefaults;
import javadocofflinesearch.tools.LevenshteinDistance;
import javadocofflinesearch.tools.Setup;

/**
 * based on http://www.mcwalter.org/technology/java/httpd/tiny/index.html Very
 * small implementation of http return headers for our served resources
 * Originally Licenced under GPLv2.0
 *
 * When resource starts with XslowX prefix, then resouce (without XslowX) is
 * returned, but its delivery is delayed
 */
public class TinyHttpdImpl extends Thread {

    private static final String CRLF = "\r\n";
    private static final String HTTP_NOT_IMPLEMENTED = "HTTP/1.0 " + HttpURLConnection.HTTP_NOT_IMPLEMENTED + " Not Implemented" + CRLF;
    private static final String HTTP_NOT_FOUND = "HTTP/1.0 " + HttpURLConnection.HTTP_NOT_FOUND + " Not Found" + CRLF;
    private static final String HTTP_OK = "HTTP/1.0 " + HttpURLConnection.HTTP_OK + " OK" + CRLF;

    boolean canRun = true;
    private static long requests = 0;

    private Socket socket;
    private MainIndex mainIndex;

    public TinyHttpdImpl(Socket socket) {

    }

    public TinyHttpdImpl(Socket socket, boolean start, MainIndex mainIndex) {
        this.socket = socket;
        this.mainIndex = mainIndex;
        if (start) {
            start();
        }
    }

    @Override
    public void run() {
        try {
            requests++;
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintStream writer = new PrintStream(this.socket.getOutputStream());
            try {
                while (canRun) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    System.out.println(": " + line);
                    if (line.length() < 1) {
                        break;
                    }

                    StringTokenizer t = new StringTokenizer(line, " ");
                    String request = t.nextToken();

                    boolean isHeadRequest = request.equals("HEAD");
                    boolean isGetRequest = request.equals("GET");

                    if (!isHeadRequest && !isGetRequest) {
                        System.out.println("Received unknown request type " + request);
                        continue;
                    }

                    String filePath = t.nextToken();

                    System.out.println(request + ": " + filePath);

//css, hml, images.. rather dont include and hope browser will do the best                        
//                        String contentType = "Content-Type: ";
//                        contentType += "text/html";
                    String contentType = "";
                    contentType += CRLF;

                    String command = filePath.replaceAll("\\?.*", "");
                    String query = filePath.replaceFirst(".*?\\?", "");

                    if (isGetRequest) {
                        if (filePath.trim().length() <= 1) {
                            SearchableHtmlFormatter xr = new SearchableHtmlFormatter(writer);
                            contentType = "Content-Type: ";
                            contentType += "text/html";
                            contentType += CRLF;
                            writer.print(HTTP_OK + contentType + CRLF);
                            xr.haders();
                            xr.tail();

                        } else {
                            String potentionalFile = URLDecoder.decode(command, "utf-8");
                            InputStream decodingStream = null;
                            URL l = null;
                            try {
                                if (potentionalFile.contains("!")) {
                                    l = new URL("jar:file://" + potentionalFile);
                                } else {
                                    l = new URL("file://" + potentionalFile);
                                }
                                decodingStream = l.openStream();
                            } catch (Exception ex) {
                                //consuming, is check
                            }
                            if (decodingStream != null) {
                                try {
                                    if (!Setup.getSetup().isFileValid(potentionalFile)) {
                                        System.out.println("Security blocked " + potentionalFile + " from showing.");
                                        throw new SecurityException("Sorry, security is on");
                                    }
                                    System.out.println("Returning file: " + potentionalFile);
                                    if (potentionalFile.toLowerCase().endsWith(".html") || potentionalFile.toLowerCase().endsWith(".htm")) {
                                        contentType = "Content-Type: ";
                                        contentType += "text/html";
                                        contentType += CRLF;
                                    }
                                    if (potentionalFile.toLowerCase().endsWith(".css")) {
                                        contentType = "Content-Type: ";
                                        contentType += "text/css";
                                        contentType += CRLF;
                                    }
                                    if (potentionalFile.toLowerCase().endsWith(".pdf")) {
                                        contentType = "Content-Type: ";
                                        contentType += "application/pdf";
                                        contentType += CRLF;
                                    }
                                    //this is learning, more this hreff is clicked, more is recorded
                                    mainIndex.clickedHrefTo(LevenshteinDistance.sanitizeFileUrl(l));
                                    byte[] buff = streamToBYteArray(decodingStream);

                                    String lastModified = "Last-Modified: " + new Date() + CRLF;
                                    writer.print(HTTP_OK + "Content-Length:" + buff.length + CRLF + lastModified + contentType + CRLF);
                                    writer.write(buff);
                                } finally {
                                    decodingStream.close();
                                }
                            } else if (command.toLowerCase().equals("/search")) {
                                WebParams cmds = new WebParams(query);
                                contentType = "Content-Type: ";
                                contentType += "text/html";
                                contentType += CRLF;
                                writer.print(HTTP_OK + contentType + CRLF);
                                if (!mainIndex.checkInitialized()) {
                                    cmds.createFormatter(writer).initializationFailed(mainIndex.printInitialized());
                                } else {

                                    mainIndex.search(cmds.getQuery(), cmds, writer);
                                }
                            } else {
                                writer.print(HTTP_NOT_FOUND);
                            }

                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (Exception e) {
                writer.print(HTTP_NOT_IMPLEMENTED + "" + CRLF);
                e.printStackTrace(writer);
                e.printStackTrace();
            } finally {
                System.out.println("Request done - " + requests);
                reader.close();
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] streamToBYteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
}
