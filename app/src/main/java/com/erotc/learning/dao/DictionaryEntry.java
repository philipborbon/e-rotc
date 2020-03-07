package com.erotc.learning.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created on 10/15/2018.
 */
@DatabaseTable
public class DictionaryEntry {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String tagalog;

    @DatabaseField
    private String hiligaynon;

    @DatabaseField
    private String ilocano;

    @DatabaseField
    private String definition;

    @DatabaseField
    private String soundFile;

    @DatabaseField
    private String tagalogExample;

    @DatabaseField
    private String hiligaynonExample;

    @DatabaseField
    private String ilocanoExample;

    public DictionaryEntry() {
    }

    public int getId() {
        return id;
    }

    public String getTagalog() {
        return tagalog;
    }

    public void setTagalog(String tagalog) {
        this.tagalog = tagalog;
    }

    public String getHiligaynon() {
        return hiligaynon;
    }

    public void setHiligaynon(String hiligaynon) {
        this.hiligaynon = hiligaynon;
    }

    public String getIlocano() {
        return ilocano;
    }

    public void setIlocano(String ilocano) {
        this.ilocano = ilocano;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }

    public String getTagalogExample() {
        return tagalogExample;
    }

    public void setTagalogExample(String tagalogExample) {
        this.tagalogExample = tagalogExample;
    }

    public String getHiligaynonExample() {
        return hiligaynonExample;
    }

    public void setHiligaynonExample(String hiligaynonExample) {
        this.hiligaynonExample = hiligaynonExample;
    }

    public String getIlocanoExample() {
        return ilocanoExample;
    }

    public void setIlocanoExample(String ilocanoExample) {
        this.ilocanoExample = ilocanoExample;
    }

    public static int getIndexOfMatch(String keyword, List<DictionaryEntry> dataList){
        keyword = keyword.toLowerCase().trim();

        for (int i = 0; i < dataList.size(); i++) {
            DictionaryEntry data = dataList.get(i);

            if ( data.tagalog.toLowerCase().equals(keyword) ) {
                return i;
            }
        }

        for (int i = 0; i < dataList.size(); i++) {
            DictionaryEntry data = dataList.get(i);

            if ( data.hiligaynon.toLowerCase().equals(keyword) ){
                return i;
            }
        }

        for (int i = 0; i < dataList.size(); i++) {
            DictionaryEntry data = dataList.get(i);

            if ( data.ilocano.toLowerCase().equals(keyword) ){
                return i;
            }
        }

        return 0;
    }
}
