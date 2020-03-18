package com.erotc.learning.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created on 10/15/2018.
 */
@Entity
data class Assessment (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var question: String? = null,
    var answer: String? = null
)