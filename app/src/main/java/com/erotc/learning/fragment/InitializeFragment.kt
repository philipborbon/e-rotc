package com.erotc.learning.fragment

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.erotc.learning.R
import com.erotc.learning.data.Assessment
import com.erotc.learning.data.Lecture
import com.erotc.learning.data.Topic
import com.erotc.learning.data.Tutorial
import com.erotc.learning.repository.LearnRepository
import com.erotc.learning.util.ApplicationUtil
import com.google.gson.reflect.TypeToken
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
                val repository = LearnRepository.getInstance(context)
                if (repository.isTopicEmpty) {
                    publishProgress(getString(R.string.message_preparing_topics))

                    val topicString = ApplicationUtil.getStringFromAsset(context, ApplicationUtil.FILE_TOPIC)
                    val topicListType = object : TypeToken<List<Topic>>(){}.type

                    repository.saveTopics(ApplicationUtil.gson.fromJson(topicString, topicListType))
                }

                if (repository.isLectureEmpty) {
                    publishProgress(getString(R.string.message_preparing_lectures))

                    val lectureString = ApplicationUtil.getStringFromAsset(context, ApplicationUtil.FILE_LECTURE)
                    val lectureListType = object : TypeToken<List<Lecture>>(){}.type

                    repository.saveLectures(ApplicationUtil.gson.fromJson(lectureString, lectureListType))
                }

                if (repository.isTutorialEmpty) {
                    publishProgress(getString(R.string.message_preparing_tutorials))

                    val tutorialString = ApplicationUtil.getStringFromAsset(context, ApplicationUtil.FILE_TUTORIAL)
                    val tutorialListType = object : TypeToken<List<Tutorial>>(){}.type

                    repository.saveTutorials(ApplicationUtil.gson.fromJson(tutorialString, tutorialListType))
                }

                if (repository.isAssessmentEmpty) {
                    publishProgress(getString(R.string.message_preparing_assessment))

                    val assessmentString = ApplicationUtil.getStringFromAsset(context, ApplicationUtil.FILE_ASSESSMENT)
                    val assessmentListType = object : TypeToken<List<Assessment>>(){}.type

                    repository.saveAssessments(ApplicationUtil.gson.fromJson(assessmentString, assessmentListType))
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