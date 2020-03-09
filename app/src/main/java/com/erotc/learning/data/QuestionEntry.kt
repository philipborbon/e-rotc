package com.erotc.learning.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created on 10/15/2018.
 */
@Entity(foreignKeys = [
    ForeignKey (
        entity = QuestionSet::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("questionSetId"),
        onDelete = ForeignKey.CASCADE
    )
])
data class QuestionEntry (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var questionSetId: Long = 0,
    var question: String? = null,
    var answer: String? = null
)