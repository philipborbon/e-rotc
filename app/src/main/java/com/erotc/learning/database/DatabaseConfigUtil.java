package com.erotc.learning.database;

import com.erotc.learning.dao.DictionaryEntry;
import com.erotc.learning.dao.QuestionEntry;
import com.erotc.learning.dao.QuestionSet;
import com.erotc.learning.dao.RecentSearchResult;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * Created on 1/25/2018.
 */

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[] {
            DictionaryEntry.class,
            QuestionEntry.class,
            QuestionSet.class,
            RecentSearchResult.class
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
