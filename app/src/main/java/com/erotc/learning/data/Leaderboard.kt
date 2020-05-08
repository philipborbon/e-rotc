package com.erotc.learning.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created on 5/8/2020.
 */
@Entity
data class Leaderboard (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String? = null,
    var score: Int = 0,
    var date: Date? = null
)