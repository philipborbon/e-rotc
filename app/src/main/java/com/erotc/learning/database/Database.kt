package com.erotc.learning.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erotc.learning.data.DictionaryEntry
import com.erotc.learning.data.QuestionEntry
import com.erotc.learning.data.QuestionSet
import com.erotc.learning.data.RecentSearchResult

/**
 * Created on 3/8/2020.
 */
@Database(entities = [
    DictionaryEntry::class,
    QuestionEntry::class,
    QuestionSet::class,
    RecentSearchResult::class
], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun dictionaryEntryDao(): DictionaryEntryDao

    abstract fun questionEntryDao(): QuestionEntryDao

    abstract fun questionSetDao(): QuestionSetDao

    abstract fun recentSearchResultDao(): RecentSearchResultDao

}