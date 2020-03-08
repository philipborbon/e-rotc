package com.erotc.learning.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.erotc.learning.R
import com.erotc.learning.repository.DictionaryRepository

/**
 * Created on 11/12/2018.
 */
object ApplicationUtil {
    private val LOG_TAG = ApplicationUtil::class.java.simpleName
    
    fun shouldInitialize(context: Context): Boolean {
        val repository = DictionaryRepository.getInstance(context)
        return repository?.isDictionaryEmpty ?: true || repository?.isQuestionSetEmpty ?: true
    }

    fun inflateButton(layoutInflater: LayoutInflater, label: String?, onClickListener: View.OnClickListener?): View {
        val view = layoutInflater.inflate(R.layout.layout_button, null)
        val viewLabel = view.findViewById<TextView>(R.id.label)
        viewLabel.text = label
        view.setOnClickListener(onClickListener)
        return view
    }

    fun inflateLockedButton(layoutInflater: LayoutInflater, label: String?): View {
        val view = layoutInflater.inflate(R.layout.layout_button_locked, null)
        val viewLabel = view.findViewById<TextView>(R.id.label)
        viewLabel.text = label
        return view
    }

    fun createSpacer(context: Context?): View {
        val view = View(context)
        view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        )
        return view
    }

    fun getIlocanoAudioFileName(string: String): String {
        return "i_" + cleanFileNameString(string)
    }

    fun getHiligaynonAudioFileName(string: String): String {
        return "h_" + cleanFileNameString(string)
    }

    private fun cleanFileNameString(string: String): String {
        return string.toLowerCase()
                .replace(" ", "_")
                .replace("-", "_")
                .replace("\"", "")
                .replace(",", "_")
                .replace("__", "_")
    }
}