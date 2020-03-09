package com.erotc.learning.repository

import android.content.Context
import androidx.room.Room
import com.erotc.learning.data.*
import com.erotc.learning.database.Database

/**
 * Created on 11/7/2018.
 */
class DictionaryRepository private constructor(context: Context) {
    private val database = Room.databaseBuilder(context, Database::class.java, "com.rotc.learning.database")
            .allowMainThreadQueries()
            .build()

    private val dictionaryEntryDao = database.dictionaryEntryDao()
    private val recentSearchResultDao = database.recentSearchResultDao()
    private val questionSetDao = database.questionSetDao()
    private val questionEntryDao = database.questionEntryDao()

    fun search(keyword: String): List<DictionaryEntry> {
        return dictionaryEntryDao.search("%$keyword%")
    }

    fun getDictionaryEntry(dictionaryId: Long): DictionaryEntry? {
        return dictionaryEntryDao.queryForId(dictionaryId)
    }

    fun getRecentSearchResults(ascending: Boolean): List<DictionaryEntry> {
        return recentSearchResultDao.recentSearches(ascending)
    }

    fun saveDictionaryEntry(dictionaryEntry: DictionaryEntry): DictionaryEntry {
        dictionaryEntry.id = dictionaryEntryDao.create(dictionaryEntry)
        return dictionaryEntry
    }

    fun saveDictionaryEntries(dictionaryEntries: List<DictionaryEntry>): List<DictionaryEntry> {
        dictionaryEntryDao.create(dictionaryEntries).forEachIndexed { index, value ->
            dictionaryEntries[index].id = value
        }

        return dictionaryEntries
    }

    fun updateQuestionSet(questionSet: QuestionSet): Int {
        return questionSetDao.update(questionSet)
    }

    fun getNextLevel(questionSet: QuestionSet): QuestionSet? {
        val nextLevel = questionSet.level + 1
        return questionSetDao.getQuestionSetForLevel(nextLevel)
    }

    fun unlockNextLevel(questionSet: QuestionSet): QuestionSet? {
        val nextQuestionSet = getNextLevel(questionSet)

        if (nextQuestionSet?.isLocked == true) {
            nextQuestionSet.isLocked = false
            questionSetDao.update(nextQuestionSet)
        }

        return nextQuestionSet
    }

    fun saveQuestionSets(questionSets: List<QuestionSetEntryList>, completion: (() -> Unit)? = null) {
        database.runInTransaction {
            for (questionSet in questionSets) {
                questionSet.questionSet.id = questionSetDao.create(questionSet.questionSet)

                questionSet.questionEntries?.let { questionEntries ->
                    for (questionEntry in questionEntries) {
                        questionEntry.questionSetId = questionSet.questionSet.id
                        questionEntry.id = questionEntryDao.create(questionEntry)
                    }
                }
            }

            completion?.invoke()
        }
    }

    fun saveRecent(recentSearchResult: RecentSearchResult) {
        if (recentSearchResultDao.countOf() >= RECENT_LIMIT_COUNT) {
            firstRecentEntry?.let {
                recentSearchResultDao.delete(it)
            }
        }

        recentSearchResultDao.getEntry(recentSearchResult.entryId)?.let { duplicateEntry ->
            recentSearchResultDao.delete(duplicateEntry)
        }

        recentSearchResult.id = recentSearchResultDao.create(recentSearchResult)
    }

    val firstRecentEntry: RecentSearchResult?
        get() = recentSearchResultDao.mostRecentEntry()

    val questionSets: List<QuestionSet>
        get() = questionSetDao.getQuestionSets()

    fun getRandomQuestionEntry(questionSet: QuestionSet): List<QuestionEntry> {
        return questionEntryDao.getRandomQuestionEntry(questionSet.id, QUESTION_ENTRY_LIMIT_COUNT)
    }

    fun getQuestionSet(id: Long): QuestionSet? {
        return questionSetDao.queryForId(id)
    }

    val isDictionaryEmpty: Boolean
        get() = dictionaryEntryDao.countOf() == 0

    val isQuestionSetEmpty: Boolean
        get() = questionSetDao.countOf() == 0

    val isRecentResultEmpty: Boolean
        get() = recentSearchResultDao.countOf() == 0

    companion object {
        private const val RECENT_LIMIT_COUNT = 100
        private const val QUESTION_ENTRY_LIMIT_COUNT = 10

        private var dictionaryRepository: DictionaryRepository? = null

        fun getInstance(context: Context): DictionaryRepository {
            if (dictionaryRepository == null) {
                dictionaryRepository = DictionaryRepository(context)
            }

            return dictionaryRepository!!
        }
    }
}