package com.erotc.learning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.erotc.learning.data.Leaderboard

/**
 * Created on 5/8/2020.
 */
@Dao
interface LeaderboardDao {

    @Query("SELECT * FROM leaderboard ORDER BY score DESC, id DESC")
    fun getAll(): List<Leaderboard>

    @Insert
    fun create(leaderboard: Leaderboard): Long

    @Query("SELECT * FROM leaderboard ORDER BY score DESC LIMIT 1")
    fun getHighScore(): Leaderboard

}