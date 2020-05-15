package com.erotc.learning.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.erotc.learning.R
import com.erotc.learning.repository.LearnRepository
import com.google.gson.Gson
import java.io.IOException

/**
 * Created on 11/12/2018.
 */
object ApplicationUtil {
    private val LOG_TAG = ApplicationUtil::class.java.simpleName

    const val FILE_TOPIC = "topic.json"
    const val FILE_LECTURE = "lecture.json"
    const val FILE_TUTORIAL = "tutorial.json"
    const val FILE_ASSESSMENT = "assessment.json"

    val gson = Gson()

    fun shouldInitialize(context: Context): Boolean {
        val repository = LearnRepository.getInstance(context)
        return repository.hasMissingData
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

    fun inflateChoice(layoutInflater: LayoutInflater, label: String?, onClickListener: View.OnClickListener?): View {
        val view = layoutInflater.inflate(R.layout.layout_choice, null)
        val viewLabel: TextView = view.findViewById(R.id.label)
        val button:View = view.findViewById(R.id.button)

        viewLabel.text = label
        button.setOnClickListener(onClickListener)

        return view
    }

    fun inflateFill(layoutInflater: LayoutInflater, onValidate: (String?) -> Unit): View {
        val view = layoutInflater.inflate(R.layout.layout_fill, null)
        val input: EditText = view.findViewById(R.id.answer)

        view.findViewById<ImageButton>(R.id.validate).setOnClickListener {
            onValidate(input.text.toString().trim())
        }

        return view
    }

    fun createSpacer(context: Context?): View {
        val view = View(context)
        view.layoutParams = LinearLayout.LayoutParams (
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        )

        return view
    }
}