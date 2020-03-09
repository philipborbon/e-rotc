package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.erotc.learning.data.QuestionSet

/**
 * Created on 3/8/2020.
 */
@Dao
interface QuestionSetDao {

    @Insert
    fun create(questionSet: QuestionSet): Long

    @Query("SELECT * FROM questionset WHERE id = :id")
    fun queryForId(id: Long): QuestionSet?

    @Update
    fun update(questionSet: QuestionSet): Int

    @Query("SELECT * FROM questionset ORDER BY level")
    fun getQuestionSets(): List<QuestionSet>

    @Query("SELECT COUNT(*) FROM questionset")
    fun countOf(): Int

    @Query("SELECT * FROM questionset WHERE level = :level")
    fun getQuestionSetForLevel(level: Int): QuestionSet?
}