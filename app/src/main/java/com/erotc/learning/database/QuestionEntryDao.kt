package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.erotc.learning.data.QuestionEntry

/**
 * Created on 3/8/2020.
 */
@Dao
interface QuestionEntryDao {

    @Insert
    fun create(entry: QuestionEntry): Long

    @Query("SELECT * FROM questionentry WHERE id IN (SELECT id FROM questionentry WHERE questionSetId = :questionSetId ORDER BY RANDOM() LIMIT :limit)")
    fun getRandomQuestionEntry(questionSetId: Long, limit: Int): List<QuestionEntry>

}