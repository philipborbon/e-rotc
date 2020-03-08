package com.erotc.learning.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.erotc.learning.R;
import com.erotc.learning.dao.DictionaryEntry;
import com.erotc.learning.dao.QuestionEntry;
import com.erotc.learning.dao.QuestionSet;
import com.erotc.learning.dao.RecentSearchResult;
import com.erotc.learning.data.DictionaryEntry;
import com.erotc.learning.data.QuestionEntry;
import com.erotc.learning.data.QuestionSet;
import com.erotc.learning.data.RecentSearchResult;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created on 1/25/2018.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "com.erotc.dictionary.db";
    private static final int DATABASE_VERSION = 11;

    private RuntimeExceptionDao<DictionaryEntry, Integer> daoDictionaryEntry = null;
    private RuntimeExceptionDao<QuestionEntry, Integer> daoQuestionEntry = null;
    private RuntimeExceptionDao<QuestionSet, Integer> daoQuestionSet = null;
    private RuntimeExceptionDao<RecentSearchResult, Integer> daoRecentSearchResult = null;

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, DictionaryEntry.class);
            TableUtils.createTable(connectionSource, QuestionEntry.class);
            TableUtils.createTable(connectionSource, QuestionSet.class);
            TableUtils.createTable(connectionSource, RecentSearchResult.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, DictionaryEntry.class, true);
            TableUtils.dropTable(connectionSource, QuestionEntry.class, true);
            TableUtils.dropTable(connectionSource, QuestionSet.class, true);
            TableUtils.dropTable(connectionSource, RecentSearchResult.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public RuntimeExceptionDao<DictionaryEntry, Integer> getDaoDictionaryEntry() {
        if ( daoDictionaryEntry == null ){
            daoDictionaryEntry = getRuntimeExceptionDao(DictionaryEntry.class);
        }

        return daoDictionaryEntry;
    }

    public RuntimeExceptionDao<QuestionEntry, Integer> getDaoQuestionEntry() {
        if ( daoQuestionEntry == null ){
            daoQuestionEntry = getRuntimeExceptionDao(QuestionEntry.class);
        }

        return daoQuestionEntry;
    }

    public RuntimeExceptionDao<QuestionSet, Integer> getDaoQuestionSet() {
        if ( daoQuestionSet == null ){
            daoQuestionSet = getRuntimeExceptionDao(QuestionSet.class);
        }

        return daoQuestionSet;
    }

    public RuntimeExceptionDao<RecentSearchResult, Integer> getDaoRecentSearchResult() {
        if ( daoRecentSearchResult == null ){
            daoRecentSearchResult = getRuntimeExceptionDao(RecentSearchResult.class);
        }

        return daoRecentSearchResult;
    }
    public static DatabaseHelper getInstance(Context context) {
        if ( instance == null ){
            instance = new DatabaseHelper(context);
        }

        return instance;
    }

    @Override
    public void close() {
        super.close();

        daoDictionaryEntry = null;
        daoQuestionEntry = null;
        daoQuestionSet = null;
        daoRecentSearchResult = null;
    }
}
