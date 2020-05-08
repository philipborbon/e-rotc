package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.erotc.learning.data.Assessment

/**
 * Created on 3/14/2020.
 */
@Dao
interface AssessmentDao {

    @Query("SELECT * FROM assessment WHERE id IN (SELECT id FROM assessment WHERE topicid=:topic ORDER BY RANDOM() LIMIT :limit)")
    fun getRandomQuestions(topic: Long, limit: Int): List<Assessment>

    @Insert
    fun create(questions: List<Assessment>): List<Long>

    @Query("SELECT COUNT(*) FROM assessment")
    fun countOf(): Int

}