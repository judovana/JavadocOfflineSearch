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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javadocofflinesearch.formatters.SearchableHtmlFormatter;
import javadocofflinesearch.formatters.StaticHtmlFormatter;
import javadocofflinesearch.htmlprocessing.PdfAttempter;
import javadocofflinesearch.htmlprocessing.StreamCrossroad;
import javadocofflinesearch.lucene.InfoExtractor;
import javadocofflinesearch.lucene.MainIndex;
import javadocofflinesearch.tools.LevenshteinDistance;
import javadocofflinesearch.tools.LibraryManager;

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

    private final Socket socket;
    private static final Map<String, MainIndex> mainIndexes = new HashMap<>();

    public TinyHttpdImpl(Socket socket, boolean start) {
        this.socket = socket;
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
                            SearchableHtmlFormatter xr = new SearchableHtmlFormatter(writer, LibraryManager.getLibraryManager().getLibrarySetup(null));
                            contentType = getHtmlContetnType();
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
                                    if (!LibraryManager.getLibraryManager().isFileValid(potentionalFile)) {
                                        System.out.println("Security blocked " + potentionalFile + " from showing.");
                                        throw new SecurityException("Sorry, security is on");
                                    }
                                    System.out.println("Returning file: " + potentionalFile);
                                    if (potentionalFile.toLowerCase().endsWith(".html") || potentionalFile.toLowerCase().endsWith(".htm")) {
                                        contentType = getHtmlContetnType();
                                    }
                                    if (potentionalFile.toLowerCase().endsWith(".css")) {
                                        contentType = getContetnType("text/css");
                                    }
                                    if (potentionalFile.toLowerCase().endsWith(".pdf")) {
                                        contentType = getContetnType("application/pdf");
                                    }
                                    //this is learning, more this hreff is clicked, more is recorded
                                    LibraryManager.getLibraryManager().clickedHrefTo(LevenshteinDistance.sanitizeFileUrl(l));

                                    WebParams cmds = new WebParams(query);
                                    byte[] buff = null;
                                    String decodedPdf = null;//this is saving one byte-string (loong) transformation)
                                    if (cmds.isPdf2txt() && potentionalFile.toLowerCase().endsWith(".pdf")) {
                                        PdfAttempter pdfAttempter = new PdfAttempter(null);
                                        decodedPdf = pdfAttempter.pdftoText(decodingStream, false);
                                        decodedPdf = decodedPdf.replaceAll("\n", "<br/>");
                                        contentType = getHtmlContetnType();
                                    } else {
                                        buff = streamToBYteArray(decodingStream);
                                    }

                                    if (cmds.isHighlight() || cmds.isJump() || cmds.isPdf2txt()) {
                                        String ll = potentionalFile.toLowerCase();
                                        if (!ll.endsWith(".pdf") || cmds.isPdf2txt()) {
                                            String s = decodedPdf;
                                            if (s == null) {
                                                s = new String(buff, "utf-8");
                                            }
                                            if (cmds.isHighlight() || cmds.isJump()) {
                                                String highlighted = InfoExtractor.highlightInString(s, cmds.getQuery(), new StaticHtmlFormatter(null, null), cmds.isHighlight(), cmds.isJump(), potentionalFile.toLowerCase().endsWith(".html") || potentionalFile.toLowerCase().endsWith(".htm"));
                                                buff = highlighted.getBytes("utf-8");
                                            } else {
                                                buff = s.getBytes("utf-8");
                                            }
                                        }

                                    }
                                    String lastModified = "Last-Modified: " + createDay() + CRLF;
                                    writer.print(HTTP_OK + "Content-Length:" + buff.length + CRLF + lastModified + contentType + CRLF);
                                    writer.write(buff);
                                } finally {
                                    decodingStream.close();
                                }
                            } else if (command.toLowerCase().equals("/search")) {
                                WebParams cmds = new WebParams(query);
                                contentType = getHtmlContetnType();
                                writer.print(HTTP_OK + contentType + CRLF);
                                MainIndex mainIndex = mainIndexes.get(cmds.getLibrary());
                                if (mainIndex == null) {
                                    mainIndex = new MainIndex(cmds);
                                    mainIndexes.put(cmds.getLibrary(), mainIndex);
                                }
                                if (!mainIndex.checkInitialized()) {
                                    cmds.createFormatter(System.out).printLibrary(cmds.getLibrary());
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

    private static String getHtmlContetnType() {
        return getContetnType("text/html");

    }

    private static String getContetnType(String type) {
        String contentType = "Content-Type: ";
        contentType += type;
        contentType += CRLF;
        return contentType;
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

    private String createDay() {
        //lets cahce pages for ... month?
        Date d = new Date();
        Calendar c1 = new GregorianCalendar();
        c1.setTime(d);
        Calendar c2 = new GregorianCalendar();
        c2.set(Calendar.YEAR, c1.get(Calendar.YEAR));
        c2.set(Calendar.MONTH, c1.get(Calendar.MONTH));
        return c2.getTime().toString();
    }
}
