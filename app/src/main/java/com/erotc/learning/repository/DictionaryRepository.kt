package com.erotc.learning.repository

import android.content.Context
import com.erotc.learning.data.DictionaryEntry
import com.erotc.learning.data.QuestionEntry
import com.erotc.learning.data.QuestionSet
import com.erotc.learning.data.RecentSearchResult
import com.erotc.learning.database.DatabaseHelper
import com.j256.ormlite.dao.RawRowMapper
import com.j256.ormlite.dao.RuntimeExceptionDao
import com.j256.ormlite.misc.TransactionManager
import java.sql.SQLException
import java.util.*

/**
 * Created on 11/7/2018.
 */
class DictionaryRepository private constructor(context: Context) {
    private val databaseHelper: DatabaseHelper = DatabaseHelper.getInstance(context)
    private val dictionaryEntryDao: RuntimeExceptionDao<DictionaryEntry, Int>
    private val recentSearchResultDao: RuntimeExceptionDao<RecentSearchResult?, Int>
    private val questionSetDao: RuntimeExceptionDao<QuestionSet, Int>
    private val questionEntryDao: RuntimeExceptionDao<QuestionEntry, Int>

    @Throws(SQLException::class)
    fun search(keyword: String): List<DictionaryEntry> {
        var keyword = keyword
        keyword = "%$keyword%"
        val builder = dictionaryEntryDao.queryBuilder()
        builder.where()
                .like("tagalog", keyword)
                .or()
                .like("hiligaynon", keyword)
                .or()
                .like("ilocano", keyword)
        return builder.query()
    }

    fun getDictionaryEntry(dictionaryId: Int): DictionaryEntry {
        return dictionaryEntryDao.queryForId(dictionaryId)
    }

    @Throws(SQLException::class)
    fun getRecentSearchResults(ascending: Boolean): List<DictionaryEntry> {
        val builder = recentSearchResultDao.queryBuilder()
        builder.orderBy("id", ascending)
        val recentResults = builder.query()
        val recentEntries: MutableList<DictionaryEntry> = ArrayList()
        for (recentResult in recentResults) {
            recentEntries.add(recentResult!!.entry)
        }
        return recentEntries
    }

    fun saveDictionaryEntry(dictionaryEntry: DictionaryEntry): DictionaryEntry {
        dictionaryEntryDao.create(dictionaryEntry)
        return dictionaryEntry
    }

    fun saveDictionaryEntries(dictionaryEntries: List<DictionaryEntry>): List<DictionaryEntry> {
        dictionaryEntryDao.create(dictionaryEntries)
        return dictionaryEntries
    }

    fun updateQuestionSet(questionSet: QuestionSet): Int {
        return questionSetDao.update(questionSet)
    }

    @Throws(SQLException::class)
    fun getNextLevel(questionSet: QuestionSet): QuestionSet {
        val nextLevel = questionSet.level + 1
        val builder = questionSetDao.queryBuilder()
        builder.where().eq("level", nextLevel)
        return builder.queryForFirst()
    }

    @Throws(SQLException::class)
    fun unlockNextLevel(questionSet: QuestionSet): QuestionSet? {
        val nextQuestionSet = getNextLevel(questionSet)
        if (nextQuestionSet.isLocked) {
            nextQuestionSet.isLocked = false
            questionSetDao.update(nextQuestionSet)
        }
        return nextQuestionSet
    }

    @Throws(SQLException::class)
    fun saveQuestionSets(questionSets: List<QuestionSet>): List<QuestionSet> {
        return TransactionManager.callInTransaction(databaseHelper.connectionSource) {
            for (questionSet in questionSets) {
                questionSetDao.create(questionSet)
                val questionEntries = questionSet.questionArray
                for (questionEntry in questionEntries) {
                    questionEntry.questionSet = questionSet
                    questionEntryDao.create(questionEntry)
                }
            }
            questionSets
        }
    }

    @Throws(SQLException::class)
    fun saveRecent(recentSearchResult: RecentSearchResult) {
        if (recentSearchResultDao.countOf() >= RECENT_LIMIT_COUNT) {
            val firstRecent = firstRecentEntry
            recentSearchResultDao.delete(firstRecent)
        }

//        QueryBuilder<DictionaryEntry, Integer> dictionaryQueryBuilder = dictionaryEntryDao.queryBuilder();
//        dictionaryQueryBuilder.where().eq("id", recentSearchResult.getEntry().getId());
        val recentQueryBuilder = recentSearchResultDao.queryBuilder()
        recentQueryBuilder.where().eq("entry_id", recentSearchResult.entry.id)
        val duplicateEntry = recentQueryBuilder.query()
        recentSearchResultDao.delete(duplicateEntry)
        recentSearchResultDao.create(recentSearchResult)
    }

    @get:Throws(SQLException::class)
    val firstRecentEntry: RecentSearchResult?
        get() {
            val builder = recentSearchResultDao.queryBuilder()
            builder.orderBy("id", true)
            val results = builder.query()
            return if (results.size > 0) {
                results[0]
            } else null
        }

    @get:Throws(SQLException::class)
    val questionSets: List<QuestionSet>
        get() {
            val builder = questionSetDao.queryBuilder()
            builder.orderBy("level", true)
            return builder.query()
        }

    @Throws(SQLException::class)
    fun getRandomQuestionEntry(questionSet: QuestionSet): List<QuestionEntry> {
        val query = "SELECT id, question, answer FROM questionentry WHERE id IN (SELECT id FROM questionentry WHERE questionSet_id = ? ORDER BY RANDOM() LIMIT $QUESTION_ENTRY_LIMIT_COUNT)"
        val rawResults = questionEntryDao.queryRaw(query, RawRowMapper { columnNames, resultColumns ->
            val questionEntry = QuestionEntry()
            questionEntry.id = resultColumns[0].toInt()
            questionEntry.question = resultColumns[1]
            questionEntry.answer = resultColumns[2]
            questionEntry.questionSet = questionSet
            questionEntry
        }, questionSet.id.toString())
        return rawResults.results
    }

    fun getQuestionSet(id: Int): QuestionSet {
        return questionSetDao.queryForId(id)
    }

    val isDictionaryEmpty: Boolean
        get() = dictionaryEntryDao.countOf() == 0L

    val isQuestionSetEmpty: Boolean
        get() = questionSetDao.countOf() == 0L

    val isRecentResultEmpty: Boolean
        get() = recentSearchResultDao.countOf() == 0L

    companion object {
        private const val RECENT_LIMIT_COUNT = 100
        private const val QUESTION_ENTRY_LIMIT_COUNT = 10
        private var instance: DictionaryRepository? = null
        @JvmStatic
        fun getInstance(context: Context): DictionaryRepository? {
            if (instance == null) {
                instance = DictionaryRepository(context)
            }
            return instance
        }
    }

    init {
        dictionaryEntryDao = databaseHelper.daoDictionaryEntry
        recentSearchResultDao = databaseHelper.daoRecentSearchResult
        questionSetDao = databaseHelper.daoQuestionSet
        questionEntryDao = databaseHelper.daoQuestionEntry
    }
}