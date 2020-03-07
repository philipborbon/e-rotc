package com.erotc.learning.repository;

import android.content.Context;

import com.erotc.learning.dao.DictionaryEntry;
import com.erotc.learning.dao.QuestionEntry;
import com.erotc.learning.dao.QuestionSet;
import com.erotc.learning.dao.RecentSearchResult;
import com.erotc.learning.database.DatabaseHelper;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created on 11/7/2018.
 */
public class DictionaryRepository {
    private static final int RECENT_LIMIT_COUNT = 100;
    private static final int QUESTION_ENTRY_LIMIT_COUNT = 10;

    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<DictionaryEntry, Integer> dictionaryEntryDao;
    private RuntimeExceptionDao<RecentSearchResult, Integer> recentSearchResultDao;
    private RuntimeExceptionDao<QuestionSet, Integer> questionSetDao;
    private RuntimeExceptionDao<QuestionEntry, Integer> questionEntryDao;

    private static DictionaryRepository instance;

    private DictionaryRepository(Context context) {
        databaseHelper =  DatabaseHelper.getInstance(context);

        dictionaryEntryDao = databaseHelper.getDaoDictionaryEntry();
        recentSearchResultDao = databaseHelper.getDaoRecentSearchResult();
        questionSetDao = databaseHelper.getDaoQuestionSet();
        questionEntryDao = databaseHelper.getDaoQuestionEntry();
    }

    public List<DictionaryEntry> search(String keyword) throws SQLException {
        keyword = "%" + keyword + "%";

        QueryBuilder<DictionaryEntry, Integer> builder = dictionaryEntryDao.queryBuilder();
        builder.where()
                .like("tagalog" , keyword)
                .or()
                .like("hiligaynon", keyword)
                .or()
                .like("ilocano", keyword);

        return builder.query();
    }

    public DictionaryEntry getDictionaryEntry(int dictionaryId) {
        return dictionaryEntryDao.queryForId(dictionaryId);
    }

    public List<DictionaryEntry> getRecentSearchResults(boolean ascending) throws SQLException {
        QueryBuilder<RecentSearchResult, Integer> builder = recentSearchResultDao.queryBuilder();
        builder.orderBy("id", ascending);

        List<RecentSearchResult> recentResults = builder.query();

        List<DictionaryEntry> recentEntries = new ArrayList<>();
        for (RecentSearchResult recentResult : recentResults){
            recentEntries.add(recentResult.getEntry());
        }

        return recentEntries;
    }

    public DictionaryEntry saveDictionaryEntry(DictionaryEntry dictionaryEntry) {
        dictionaryEntryDao.create(dictionaryEntry);

        return dictionaryEntry;
    }

    public List<DictionaryEntry> saveDictionaryEntries(List<DictionaryEntry> dictionaryEntries) {
        dictionaryEntryDao.create(dictionaryEntries);

        return dictionaryEntries;
    }

    public int updateQuestionSet(QuestionSet questionSet){
        return questionSetDao.update(questionSet);
    }

    public QuestionSet getNextLevel(QuestionSet questionSet) throws SQLException {
        int nextLevel = questionSet.getLevel() + 1;

        QueryBuilder<QuestionSet, Integer> builder = questionSetDao.queryBuilder();
        builder.where().eq("level", nextLevel);

        return builder.queryForFirst();
    }

    public QuestionSet unlockNextLevel(QuestionSet questionSet) throws SQLException {
        QuestionSet nextQuestionSet = getNextLevel(questionSet);

        if ( nextQuestionSet != null ){
            if ( nextQuestionSet.isLocked() ) {
                nextQuestionSet.setLocked(false);
                questionSetDao.update(nextQuestionSet);
            }
        }

        return nextQuestionSet;
    }

    public List<QuestionSet> saveQuestionSets(final List<QuestionSet> questionSets) throws SQLException {
        return TransactionManager.callInTransaction(databaseHelper.getConnectionSource(), new Callable<List<QuestionSet>>() {
            @Override
            public List<QuestionSet> call() {
                for (QuestionSet questionSet : questionSets) {
                    questionSetDao.create(questionSet);

                    ArrayList<QuestionEntry> questionEntries = questionSet.getQuestionArray();
                    for (QuestionEntry questionEntry : questionEntries) {
                        questionEntry.setQuestionSet(questionSet);
                        questionEntryDao.create(questionEntry);
                    }
                }

                return questionSets;
            }
        });
    }

    public void saveRecent(RecentSearchResult recentSearchResult) throws SQLException {
        if ( recentSearchResultDao.countOf() >= RECENT_LIMIT_COUNT ){
            RecentSearchResult firstRecent = getFirstRecentEntry();
            recentSearchResultDao.delete(firstRecent);
        }

//        QueryBuilder<DictionaryEntry, Integer> dictionaryQueryBuilder = dictionaryEntryDao.queryBuilder();
//        dictionaryQueryBuilder.where().eq("id", recentSearchResult.getEntry().getId());

        QueryBuilder<RecentSearchResult, Integer> recentQueryBuilder = recentSearchResultDao.queryBuilder();
        recentQueryBuilder.where().eq("entry_id", recentSearchResult.getEntry().getId());

        List<RecentSearchResult> duplicateEntry = recentQueryBuilder.query();
        recentSearchResultDao.delete(duplicateEntry);

        recentSearchResultDao.create(recentSearchResult);
    }

    public RecentSearchResult getFirstRecentEntry() throws SQLException {
        QueryBuilder<RecentSearchResult, Integer> builder = recentSearchResultDao.queryBuilder();
        builder.orderBy("id", true);

        List<RecentSearchResult> results = builder.query();
        if ( results.size() > 0 ){
            return results.get(0);
        }

        return null;
    }

    public List<QuestionSet> getQuestionSets() throws SQLException {
        QueryBuilder<QuestionSet, Integer> builder = questionSetDao.queryBuilder();
        builder.orderBy("level", true);

        return builder.query();
    }

    public List<QuestionEntry> getRandomQuestionEntry(final QuestionSet questionSet) throws SQLException {
        String query = "SELECT id, question, answer FROM questionentry WHERE id IN (SELECT id FROM questionentry WHERE questionSet_id = ? ORDER BY RANDOM() LIMIT " + QUESTION_ENTRY_LIMIT_COUNT + ")";

        GenericRawResults<QuestionEntry> rawResults = questionEntryDao.queryRaw(query, new RawRowMapper<QuestionEntry>() {
            @Override
            public QuestionEntry mapRow(String[] columnNames, String[] resultColumns) {
                QuestionEntry questionEntry = new QuestionEntry();
                questionEntry.setId(Integer.parseInt(resultColumns[0]));
                questionEntry.setQuestion(resultColumns[1]);
                questionEntry.setAnswer(resultColumns[2]);
                questionEntry.setQuestionSet(questionSet);

                return questionEntry;
            }
        }, String.valueOf(questionSet.getId()));

        return rawResults.getResults();
    }

    public QuestionSet getQuestionSet(int id){
        return questionSetDao.queryForId(id);
    }

    public boolean isDictionaryEmpty() {
        return (dictionaryEntryDao.countOf() == 0);
    }

    public boolean isQuestionSetEmpty() {
        return (questionSetDao.countOf() == 0);
    }

    public boolean isRecentResultEmpty() {
        return ( recentSearchResultDao.countOf() == 0 );
    }

    public static DictionaryRepository getInstance(Context context) {
        if ( instance == null ){
            instance = new DictionaryRepository(context);
        }

        return instance;
    }
}
