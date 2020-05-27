package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.erotc.learning.data.Tutorial

/**
 * Created on 5/15/2020.
 */
@Dao
interface TutorialDao {
    @Query("SELECT * FROM tutorial WHERE id = :id")
    fun queryForId(id: Long): Tutorial?

    @Query("SELECT COUNT(*) FROM tutorial")
    fun countOf(): Int

    @Query("SELECT * FROM tutorial WHERE title LIKE :keyword LIKE :keyword ORDER BY title")
    fun search(keyword: String): List<Tutorial>

    @Insert
    fun create(entry: Tutorial): Long

    @Insert
    fun create(tutorials: List<Tutorial>): List<Long>

    @Query("SELECT * FROM tutorial ORDER BY sort")
    fun getAll(): List<Tutorial>
}