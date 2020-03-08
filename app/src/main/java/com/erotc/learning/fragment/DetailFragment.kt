package com.erotc.learning.fragment

import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.erotc.learning.R
import com.erotc.learning.data.DictionaryEntry
import com.erotc.learning.repository.DictionaryRepository
import com.erotc.learning.util.ApplicationUtil
import kotlinx.android.synthetic.main.fragment_dictionary_detail.*

class DetailFragment : Fragment() {
    private var dictionaryId = 0
    private lateinit var dictionaryRepository: DictionaryRepository

    private var dictionaryEntry: DictionaryEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            dictionaryId = arguments!!.getInt(DICTIONARY_ID)
        }

        dictionaryRepository = DictionaryRepository.getInstance(context)
        dictionaryEntry = dictionaryRepository.getDictionaryEntry(dictionaryId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dictionary_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ilocanoAudio = ApplicationUtil.getIlocanoAudioFileName(dictionaryEntry?.tagalog)
        val ilocanoAudioId = getResourceId(ilocanoAudio)
        if (ilocanoAudioId != 0) {
            button_play_ilocano.setOnClickListener { playAudio(ilocanoAudio) }
        } else {
            button_play_ilocano.visibility = View.GONE
        }
        val hiligaynonAudio = ApplicationUtil.getHiligaynonAudioFileName(dictionaryEntry!!.tagalog)
        val hiligaynonAudioId = getResourceId(hiligaynonAudio)
        if (hiligaynonAudioId != 0) {
            text_play_hiligaynon.setOnClickListener { playAudio(hiligaynonAudio) }
        } else {
            text_play_hiligaynon.visibility = View.GONE
        }
        val strTagalog = dictionaryEntry!!.tagalog
        val strHiligaynon = dictionaryEntry!!.hiligaynon
        val strIlocano = dictionaryEntry!!.ilocano
        text_tagalog.text = strTagalog
        text_hiligaynon.text = strHiligaynon
        text_ilocano.text = strIlocano
        text_definition.text = dictionaryEntry!!.definition
        text_example_tagalog.text = boldText(strTagalog, dictionaryEntry!!.tagalogExample)
        text_example_hiligaynon.text = boldText(strHiligaynon, dictionaryEntry!!.hiligaynonExample)
        txt_example_ilocano.text = boldText(strIlocano, dictionaryEntry!!.ilocanoExample)
    }

    private fun boldText(text: String, sentence: String): SpannableStringBuilder {
        val comparableText = text.toLowerCase()
        val comparableSentence = sentence.toLowerCase()
        val index = comparableSentence.indexOf(comparableText)
        val spannableStringBuilder = SpannableStringBuilder(sentence)
        if (index != -1) {
            spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), index, index + comparableText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannableStringBuilder
    }

    private fun playAudio(filename: String) {
        val resourceId = getResourceId(filename)
        val player = MediaPlayer.create(context, resourceId)
        player.start()
    }

    private fun getResourceId(filename: String): Int {
        return resources.getIdentifier(filename, "raw", activity!!.packageName)
    }

    companion object {
        private val LOG_TAG = DetailFragment::class.java.simpleName
        private const val DICTIONARY_ID = "dictionary-id"
        fun newInstance(dictionaryId: Int): DetailFragment {
            val fragment = DetailFragment()
            val bundle = Bundle()
            bundle.putInt(DICTIONARY_ID, dictionaryId)
            fragment.arguments = bundle
            return fragment
        }
    }
}