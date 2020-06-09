/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minilyrics;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.TextUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Sky
 */
class LyricsSearch {

    private static final String url = "http://search.crintsoft.com/searchlyrics.htm";
    private static final String clientUserAgent = "MiniLyrics";
    private static final String clientTag = "client=\"ViewLyricsOpenSearcher\"";
    private static final String searchQueryBase = "<?xml version='1.0' encoding='utf-8' ?><searchV1 artist=\"%s\" title=\"%s\" OnlyMatched=\"1\" %s/>";
    private static final String searchQueryPage = " RequestPage='%d'";
    private static final byte[] magickey = "Mlv1clt4.0".getBytes();

    public static LyricsResult search(String artist, String title, int page)
            throws ClientProtocolException, IOException, NoSuchAlgorithmException, SAXException, ParserConfigurationException {
        return searchQuery(
                String.format("<?xml version='1.0' encoding='utf-8' ?><searchV1 artist=\"%s\" title=\"%s\" OnlyMatched=\"1\" %s/>", new Object[]{artist, title, "client=\"ViewLyricsOpenSearcher\""
                    + String.format(" RequestPage='%d'", new Object[]{Integer.valueOf(page)})
                }));
    }

    private static LyricsResult searchQuery(String searchQuery)
            throws ClientProtocolException, IOException,
            NoSuchAlgorithmException, SAXException,
            ParserConfigurationException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://search.crintsoft.com/searchlyrics.htm");

        request.setHeader("User-Agent", "MiniLyrics");
        client.getParams().setBooleanParameter("http.protocol.expect-continue", true);

        request.setEntity(new ByteArrayEntity(assembleQuery(searchQuery.getBytes("UTF-8"))));

        HttpResponse response = client.execute(request);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "ISO_8859_1"));

        StringBuilder builder = new StringBuilder();
        char[] buffer = new char['?'];
        int read;
        while ((read = rd.read(buffer, 0, buffer.length)) > 0) {
            builder.append(buffer, 0, read);
        }
        String full = builder.toString();

        return parseResultXML(decryptResultXML(full));
    }

    public static byte[] assembleQuery(byte[] value)
            throws NoSuchAlgorithmException, IOException {
        byte[] pog = new byte[value.length + magickey.length];

        System.arraycopy(value, 0, pog, 0, value.length);
        System.arraycopy(magickey, 0, pog, value.length, magickey.length);

        byte[] pog_md5 = MessageDigest.getInstance("MD5").digest(pog);

        int j = 0;
        for (int i = 0; i < value.length; i++) {
            j += value[i];
        }
        int k = (byte) (j / value.length);
        for (int m = 0; m < value.length; m++) {
            value[m] = ((byte) (k ^ value[m]));
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        result.write(2);
        result.write(k);
        result.write(4);
        result.write(0);
        result.write(0);
        result.write(0);

        result.write(pog_md5);

        result.write(value);

        return result.toByteArray();
    }

    public static String decryptResultXML(String value) {
        char magickey = value.charAt(1);

        ByteArrayOutputStream neomagic = new ByteArrayOutputStream();
        for (int i = 22; i < value.length(); i++) {
            neomagic.write((byte) (value.charAt(i) ^ magickey));
        }
        return neomagic.toString();
    }

    private static int readIntFromAttr(Element elem, String attr, int def) {
        String data = elem.getAttribute(attr);
        try {
            if (!TextUtils.isEmpty(data)) {
                return Integer.valueOf(data).intValue();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return def;
    }

    private static double readFloatFromAttr(Element elem, String attr, float def) {
        String data = elem.getAttribute(attr);
        try {
            if (!TextUtils.isEmpty(data)) {
                return Double.valueOf(data).doubleValue();
            }
        } catch (NumberFormatException localNumberFormatException) {
        }
        return def;
    }

    private static String readStrFromAttr(Element elem, String attr, String def) {
        String data = elem.getAttribute(attr);
        try {
            if (!TextUtils.isEmpty(data)) {
                return data;
            }
        } catch (NumberFormatException localNumberFormatException) {
        }
        return def;
    }

    public static LyricsResult parseResultXML(String resultXML)
            throws SAXException, IOException, ParserConfigurationException {
        LyricsResult result = new LyricsResult();

        ArrayList<Lyrics> availableLyrics = new ArrayList();

        ByteArrayInputStream resultBA = new ByteArrayInputStream(resultXML.getBytes("UTF-8"));
        Element resultRootElem = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(resultBA).getDocumentElement();

        result.setCurrentPage(readIntFromAttr(resultRootElem, "CurPage", 0));
        result.setPageCount(readIntFromAttr(resultRootElem, "PageCount", 1));
        String server_url = readStrFromAttr(resultRootElem, "server_url", "http://www.viewlyrics.com/");

        NodeList resultItemList = resultRootElem.getElementsByTagName("fileinfo");
        for (int i = 0; i < resultItemList.getLength(); i++) {
            Element itemElem = (Element) resultItemList.item(i);
            Lyrics itemInfo = new Lyrics();

            itemInfo.setLyricURL(server_url + readStrFromAttr(itemElem, "link", ""));
            itemInfo.setMusicArtist(readStrFromAttr(itemElem, "artist", ""));
            itemInfo.setMusicTitle(readStrFromAttr(itemElem, "title", ""));
            itemInfo.setMusicAlbum(readStrFromAttr(itemElem, "album", ""));
            itemInfo.setLyricsFileName(readStrFromAttr(itemElem, "filename", ""));
            itemInfo.setLyricUploader(readStrFromAttr(itemElem, "uploader", ""));
            itemInfo.setLyricRate(Double.valueOf(readFloatFromAttr(itemElem, "rate", 0.0F)));
            itemInfo.setLyricRatesCount(Integer.valueOf(readIntFromAttr(itemElem, "ratecount", 0)));
            itemInfo.setLyricDownloadsCount(Integer.valueOf(readIntFromAttr(itemElem, "downloads", 0)));
            availableLyrics.add(itemInfo);
        }
        result.setLyricsInfo(availableLyrics);

        return result;
    }
}
