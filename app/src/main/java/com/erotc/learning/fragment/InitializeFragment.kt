package com.erotc.learning.fragment

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.erotc.learning.R
import com.erotc.learning.parser.DictionaryParser
import com.erotc.learning.parser.QuestionParser
import com.erotc.learning.repository.DictionaryRepository
import kotlinx.android.synthetic.main.fragment_initialize.*

/**
 * A simple [Fragment] subclass.
 * Use the [InitializeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InitializeFragment : Fragment() {
    interface InitializeListener {
        fun onDone()
    }

    private var listener: InitializeListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_initialize, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()
    }

    @SuppressLint("StaticFieldLeak")
    private fun initialize() {
        val context = context ?: return

        object : AsyncTask<Void?, String?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                val repository = DictionaryRepository.getInstance(context)
                if (repository.isDictionaryEmpty) {
                    publishProgress(getString(R.string.message_preparing_dictionary))
                    val dictionaryParser = DictionaryParser(
                            resources.openRawResource(R.raw.dictionary)
                    )
                    val entryList = dictionaryParser.parse()
                    repository.saveDictionaryEntries(entryList)
                }

                if (repository.isQuestionSetEmpty) {
                    publishProgress(getString(R.string.message_preparing_assessment))
                    val questionParser = QuestionParser(resources.openRawResource(R.raw.questionset))
                    val questionSetList = questionParser.parse()
                    repository.saveQuestionSets(questionSetList)
                }

                return null
            }

            override fun onProgressUpdate(vararg values: String?) {
                label.text = values[0]
            }

            override fun onPostExecute(aVoid: Void?) {
                listener?.onDone()
            }
        }.execute()
    }

    fun setListener(listener: InitializeListener?) {
        this.listener = listener
    }

    companion object {
        private val LOG_TAG = InitializeFragment::class.java.simpleName
        fun newInstance(): InitializeFragment {
            return InitializeFragment()
        }
    }
}