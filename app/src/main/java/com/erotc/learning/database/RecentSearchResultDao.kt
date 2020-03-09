package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.erotc.learning.data.DictionaryEntry
import com.erotc.learning.data.RecentSearchResult

/**
 * Created on 3/8/2020.
 */
@Dao
interface RecentSearchResultDao {

    @Query("SELECT COUNT(*) FROM recentsearchresult")
    fun countOf(): Int

    @Delete
    fun delete(result: RecentSearchResult): Int

    @Query("SELECT * FROM recentsearchresult WHERE entryId = :entryId")
    fun getEntry(entryId: Long): RecentSearchResult?

    @Insert
    fun create(result: RecentSearchResult): Long

    @Query("SELECT * FROM recentsearchresult ORDER BY id DESC LIMIT 1")
    fun mostRecentEntry(): RecentSearchResult?

    @Query("SELECT de.* FROM recentsearchresult rs INNER JOIN dictionaryentry de ON rs.entryId = de.id ORDER BY CASE WHEN :ascending = 1 THEN rs.id END ASC, CASE WHEN :ascending = 0 THEN rs.id END DESC")
    fun recentSearches(ascending: Boolean): List<DictionaryEntry>

}