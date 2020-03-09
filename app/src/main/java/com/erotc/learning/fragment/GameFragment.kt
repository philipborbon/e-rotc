package com.erotc.learning.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.erotc.learning.R
import com.erotc.learning.activity.GameChoiceActivity
import com.erotc.learning.data.QuestionSet
import com.erotc.learning.repository.DictionaryRepository
import com.erotc.learning.util.ApplicationUtil
import kotlinx.android.synthetic.main.fragment_game.*

/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {
    private lateinit var repository: DictionaryRepository
    private var questionSets: List<QuestionSet>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = context ?: return
        repository = DictionaryRepository.getInstance(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchAndShowQuestionSets()
    }

    @SuppressLint("StaticFieldLeak")
    private fun fetchAndShowQuestionSets() {
        container_question_set.removeAllViews()

        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                questionSets = repository.questionSets
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                layoutQuestionSets()
            }
        }.execute()
    }

    private fun layoutQuestionSets() {
        val questionSets = questionSets ?: return;

        for ((index, questionSet) in questionSets.withIndex()) {
            container_question_set.addView(ApplicationUtil.createSpacer(context))
            if (questionSet.isLocked) {
                val view = ApplicationUtil.inflateLockedButton(layoutInflater, questionSet.label)
                container_question_set.addView(view)
            } else {
                val view = ApplicationUtil.inflateButton(layoutInflater, questionSet.label, View.OnClickListener {
                    val intent = Intent(context, GameChoiceActivity::class.java)
                    intent.putExtra(GameChoiceActivity.QUESTION_SET_ID, questionSet.id)
                    startActivity(intent)
                })

                container_question_set.addView(view)
            }
            if (index == questionSets.size ?: 0 - 1) {
                container_question_set.addView(ApplicationUtil.createSpacer(context))
            }
        }
    }

    companion object {
        private val LOG_TAG = GameFragment::class.java.simpleName
        fun newInstance(): GameFragment {
            return GameFragment()
        }
    }
}