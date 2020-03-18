package com.erotc.learning.util

import android.content.Context
import android.util.Log
import com.erotc.learning.repository.LearnRepository
import com.google.gson.Gson
import java.io.IOException

/**
 * Created on 11/12/2018.
 */
object ApplicationUtil {
    private val LOG_TAG = ApplicationUtil::class.java.simpleName

    const val FILE_LECTURE = "lecture.json"
    const val FILE_ASSESSMENT = "assessment.json"

    val gson = Gson()

    fun shouldInitialize(context: Context): Boolean {
        val repository = LearnRepository.getInstance(context)
        return repository.isLectureEmpty || repository.isAssessmentEmpty
    }

    fun getStringFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e(LOG_TAG, Log.getStackTraceString(e))
            return null
        }

        return jsonString
    }
}