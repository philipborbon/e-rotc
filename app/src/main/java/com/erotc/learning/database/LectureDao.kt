package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.erotc.learning.data.Lecture

/**
 * Created on 3/14/2020.
 */
@Dao
interface LectureDao {
    @Query("SELECT * FROM lecture WHERE id = :id")
    fun queryForId(id: Long): Lecture?

    @Query("SELECT COUNT(*) FROM lecture")
    fun countOf(): Int

    @Query("SELECT * FROM lecture WHERE title LIKE :keyword")
    fun search(keyword: String): List<Lecture>

    @Insert
    fun create(entry: Lecture): Long

    @Insert
    fun create(lectures: List<Lecture>): List<Long>

    @Query("SELECT * FROM lecture ORDER BY title")
    fun getAll(): List<Lecture>
}