package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.erotc.learning.data.Topic

/**
 * Created on 3/18/2020.
 */
@Dao
interface TopicDao {
    @Query("SELECT * FROM topic WHERE id = :id")
    fun queryForId(id: Long): Topic?

    @Query("SELECT COUNT(*) FROM topic")
    fun countOf(): Int

    @Insert
    fun create(topics: List<Topic>): List<Long>
}