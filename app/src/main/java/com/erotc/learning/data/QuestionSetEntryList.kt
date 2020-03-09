package com.erotc.learning.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Created on 3/9/2020.
 */
data class QuestionSetEntryList (
    @Embedded val questionSet: QuestionSet,
    @Relation(parentColumn = "id", entityColumn = "id", entity = QuestionEntry::class)
    var questionEntries: ArrayList<QuestionEntry>? = null
)