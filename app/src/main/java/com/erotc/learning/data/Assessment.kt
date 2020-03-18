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
    var type: String? = null,
    var choices: ArrayList<String>? = null,
    var answer: String? = null
) {
    val questionType: QuestionType
        get() = when(type) {
            QuestionType.TRUE_OR_FALSE.key
                -> QuestionType.TRUE_OR_FALSE

            QuestionType.FILL_IN_BLANK_QUESTION.key
                -> QuestionType.FILL_IN_BLANK_QUESTION

            QuestionType.FILL_IN_BLANK_IMAGE.key
                -> QuestionType.FILL_IN_BLANK_IMAGE

            QuestionType.MULTIPLE_CHOICE_QUESTION.key
                -> QuestionType.MULTIPLE_CHOICE_QUESTION

            QuestionType.MULTIPLE_CHOICE_IMAGE.key
                -> QuestionType.MULTIPLE_CHOICE_IMAGE

            else -> QuestionType.TRUE_OR_FALSE
        }

    val imagePath: String?
        get() = when(questionType) {
            QuestionType.TRUE_OR_FALSE,
            QuestionType.FILL_IN_BLANK_QUESTION,
            QuestionType.MULTIPLE_CHOICE_QUESTION -> null

            QuestionType.MULTIPLE_CHOICE_IMAGE,
            QuestionType.FILL_IN_BLANK_IMAGE -> "file:///android_asset/$question"
        }
}
