package com.erotc.learning.data

/**
 * Created on 3/18/2020.
 */
enum class QuestionType(val key: String) {
    TRUE_OR_FALSE("tof"),
    FILL_IN_BLANK_QUESTION("fill-q"),
    FILL_IN_BLANK_IMAGE("fill-i"),
    MULTIPLE_CHOICE_QUESTION("multi-q"),
    MULTIPLE_CHOICE_IMAGE("multi-i")
}