/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minilyrics;

import java.util.ArrayList;

/**
 *
 * @author Sky
 */
public class LyricsResult {

    private ArrayList<Lyrics> infos;
    private int pageCount;
    private int curPage;
    private boolean valid;

    public void setLyricsInfo(ArrayList<Lyrics> infos) {
        this.infos = infos;
    }

    public void setPageCount(int value) {
        this.pageCount = value;
    }

    public void setCurrentPage(int value) {
        this.curPage = value;
    }

    public void setValid(boolean value) {
        this.valid = value;
    }

    public ArrayList<Lyrics> getLyricsInfo() {
        return this.infos;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public int getCurrentPage() {
        return this.curPage;
    }

    public boolean isValid() {
        return this.valid;
    }
}
