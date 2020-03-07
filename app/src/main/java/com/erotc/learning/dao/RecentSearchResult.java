package com.erotc.learning.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created on 11/7/2018.
 */
@DatabaseTable
public class RecentSearchResult {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private DictionaryEntry entry;

    public RecentSearchResult() {
    }

    public DictionaryEntry getEntry() {
        return entry;
    }

    public void setEntry(DictionaryEntry entry) {
        this.entry = entry;
    }
}
