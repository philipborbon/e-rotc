package com.erotc.learning.parser

import com.erotc.learning.data.QuestionEntry
import com.erotc.learning.data.QuestionSet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Created on 11/26/2018.
 */
class QuestionParser(private val inputStream: InputStream) {
    private var byteArrayOutputStream: ByteArrayOutputStream? = null

    @Throws(IOException::class)
    private fun loadDataToByteArrayOutputStream() {
        byteArrayOutputStream = ByteArrayOutputStream()
        var dataTye = inputStream.read()

        inputStream.use { inputStream ->
            while (dataTye != -1) {
                byteArrayOutputStream!!.write(dataTye)
                dataTye = inputStream.read()
            }
        }
    }

    @Throws(JSONException::class)
    fun parse(): ArrayList<QuestionSet> {
        val questionSets = ArrayList<QuestionSet>()
        val jArray = JSONArray(byteArrayOutputStream.toString())
        for (i in 0 until jArray.length()) {
            val jObject = jArray.getJSONObject(i)
            questionSets.add(readQuestionSet(jObject))
        }
        return questionSets
    }

    @Throws(JSONException::class)
    private fun readQuestionSet(jObject: JSONObject): QuestionSet {
        val questionSet = QuestionSet()
        questionSet.label = jObject.getString(FIELD_LABEL)
        questionSet.level = jObject.getInt(FIELD_LEVEL)
        questionSet.isLocked = jObject.getBoolean(FIELD_LOCKED)
        questionSet.pointsToProceed = jObject.getInt(FIELD_POINTS_TO_PROCEED)
        questionSet.answerSet = jObject.getString(FIELD_ANSWER_SET)
        questionSet.questionArray = readQuestionEntryArray(jObject.getJSONArray(FIELD_QUESTION_ENTRIES))
        return questionSet
    }

    @Throws(JSONException::class)
    private fun readQuestionEntryArray(jArray: JSONArray): ArrayList<QuestionEntry> {
        val questionEntries = ArrayList<QuestionEntry>()
        for (i in 0 until jArray.length()) {
            val jObject = jArray.getJSONObject(i)
            questionEntries.add(readQuestionEntry(jObject))
        }
        return questionEntries
    }

    @Throws(JSONException::class)
    private fun readQuestionEntry(jObject: JSONObject): QuestionEntry {
        val questionEntry = QuestionEntry()
        questionEntry.question = jObject.getString(FIELD_QUESTION)
        questionEntry.answer = jObject.getString(FIELD_ANSWER)
        return questionEntry
    }

    companion object {
        private const val FIELD_LABEL = "label"
        private const val FIELD_LEVEL = "level"
        private const val FIELD_LOCKED = "locked"
        private const val FIELD_POINTS_TO_PROCEED = "pointsToProceed"
        private const val FIELD_ANSWER_SET = "answerSet"
        private const val FIELD_QUESTION_ENTRIES = "questionEntries"
        private const val FIELD_QUESTION = "question"
        private const val FIELD_ANSWER = "answer"
    }

    init {
        loadDataToByteArrayOutputStream()
    }
}