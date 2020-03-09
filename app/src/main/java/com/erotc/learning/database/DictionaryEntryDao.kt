package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.erotc.learning.data.DictionaryEntry

/**
 * Created on 3/8/2020.
 */
@Dao
interface DictionaryEntryDao {

    @Query("SELECT * FROM dictionaryentry WHERE id = :id")
    fun queryForId(id: Long): DictionaryEntry?

    @Query("SELECT COUNT(*) FROM dictionaryentry")
    fun countOf(): Int

    @Query("SELECT * FROM dictionaryentry WHERE tagalog LIKE :keyword OR hiligaynon LIKE :keyword OR ilocano LIKE :keyword")
    fun search(keyword: String): List<DictionaryEntry>

    @Insert
    fun create(entry: DictionaryEntry): Long

    @Insert
    fun create(entries: List<DictionaryEntry>): List<Long>

}