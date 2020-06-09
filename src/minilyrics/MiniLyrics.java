/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minilyrics;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

/**
 *
 * @author Sky
 */
public class MiniLyrics {

    public static void main(String[] args)
            throws Exception {
        int port = Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/all", new GetAll());
        server.createContext("/single", new GetSingle());
        server.setExecutor(null);
        System.out.println("server listening on port " + port);
        System.out.println("methods => '/all', '/single'");
        server.start();
    }

    public static void getResults(String artist, String title, HttpExchange t)
            throws ClientProtocolException, IOException {
        try {
            String response = "", prefix = "{\"lyrics\": [", suffix = "]}", seperate = ",";
            List<String> list = new ArrayList<>();

            for (Lyrics lyrics : LyricsSearch.search(artist, title, 1).getLyricsInfo()) {
                list.add(lyrics.dump());
            }

            response = prefix + String.join(seperate, list) + suffix;

            t.sendResponseHeaders(200, response.length());

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } catch (NoSuchAlgorithmException | SAXException | ParserConfigurationException ex) {
            Logger.getLogger(MiniLyrics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static class GetAll
            implements HttpHandler {

        public void handle(HttpExchange t)
                throws IOException {
            String[] queryArray = t.getRequestURI().getQuery().split("&");
            Hashtable query = new Hashtable();
            for (int i = 0; i < queryArray.length; i++) {
                String[] param = queryArray[i].split("=");
                query.put(param[0], param[1]);
                System.out.println(param[0] + "->" + param[1]);
            }
            String artist = (String) query.get("artist");
            String title = (String) query.get("title");
            System.out.println(t.getRequestMethod() + " " + t.getRequestURI());
            MiniLyrics.getResults(artist, title, t);
        }
    }

    static class GetSingle
            implements HttpHandler {

        public void handle(HttpExchange t)
                throws IOException {
            String[] queryArray = t.getRequestURI().getQuery().split("&");
            Hashtable query = new Hashtable();
            for (int i = 0; i < queryArray.length; i++) {
                String[] param = queryArray[i].split("=");
                query.put(param[0], param[1]);
                System.out.println(param[0] + "->" + param[1]);
            }
            String artist = (String) query.get("artist");
            String title = (String) query.get("title");
            System.out.println(t.getRequestMethod() + " " + t.getRequestURI());
            String response = "";
            try {
                for (Lyrics lyrics : LyricsSearch.search(artist, title, 1).getLyricsInfo()) {
                    response = lyrics.dump();
                }
            } catch (ClientProtocolException ex) {
                Logger.getLogger(MiniLyrics.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(MiniLyrics.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(MiniLyrics.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(MiniLyrics.class.getName()).log(Level.SEVERE, null, ex);
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
