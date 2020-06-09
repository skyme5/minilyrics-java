/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minilyrics;

import org.json.JSONObject;

/**
 *
 * @author Sky
 */
class Lyrics {

    private String type;
    private String url;
    private String name;
    private String title;
    private String artist;
    private String album;
    private String uploader;
    private String timelength;
    private Double rate;
    private int downloadscount;
    private int ratecount;

    public String getLyricURL() {
        return this.url;
    }

    public String getLyricsFileName() {
        return this.name;
    }

    public String getMusicTitle() {
        return this.title;
    }

    public String getMusicArtist() {
        return this.artist;
    }

    public String getMusicAlbum() {
        return this.album;
    }

    public String getLyricUploader() {
        return this.uploader;
    }

    public Double getLyricRate() {
        return this.rate;
    }

    public Integer getLyricRatesCount() {
        return Integer.valueOf(this.ratecount);
    }

    public Integer getLyricDownloadsCount() {
        return Integer.valueOf(this.downloadscount);
    }

    public String getMusicLenght() {
        return this.timelength;
    }

    public void setLyricURL(String value) {
        this.url = value;
        this.type = value.substring(value.length() - 3, value.length());
    }

    public void setLyricsFileName(String value) {
        String[] array = this.url.split("/");
        this.name = array[(array.length - 1)];
    }

    public void setMusicTitle(String value) {
        this.title = value;
    }

    public void setMusicArtist(String value) {
        this.artist = value;
    }

    public void setMusicAlbum(String value) {
        this.album = value;
    }

    public void setLyricUploader(String value) {
        this.uploader = value;
    }

    public void setLyricRate(Double value) {
        this.rate = value;
    }

    public void setLyricRatesCount(Integer i) {
        this.ratecount = i.intValue();
    }

    public void setLyricDownloadsCount(Integer value) {
        this.downloadscount = value.intValue();
    }

    public void setMusicLenght(String value) {
        this.timelength = value;
    }

    public String dump() {
        String jsonString = new JSONObject()
                .put("album", this.album)
                .put("artist", this.artist)
                .put("downloaded", this.downloadscount)
                .put("filename", this.name)
                .put("rate", this.rate)
                .put("ratecount", this.ratecount)
                .put("timelength", this.timelength)
                .put("title", this.title)
                .put("type", this.type)
                .put("uploader", this.uploader)
                .put("url", this.url).toString();        /*return String.format(
         "<fileinfo filetype=\"%s\" link=\"%s\" filename=\"%s\" artist=\"%s\" title=\"%s\" album=\"%s\" uploader=\"%s\" rate=\"%f\" ratecount=\"%d\" downloads=\"%d\" timelength=\"%s\"/>", 
         new Object[]{this.type, this.url, this.name, this.artist, this.title, this.album, this.uploader, this.rate,
         Integer.valueOf(this.ratecount), Integer.valueOf(this.downloadscount), this.timelength});*/

        return jsonString;
    }
}
