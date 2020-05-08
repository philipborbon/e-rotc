package com.erotc.learning.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.erotc.learning.R
import com.erotc.learning.data.Assessment
import com.erotc.learning.data.QuestionType
import com.erotc.learning.data.Topic
import com.erotc.learning.repository.LearnRepository
import com.erotc.learning.util.ApplicationUtil
import com.erotc.learning.util.hourglass.Hourglass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_assessment.*
import kotlinx.android.synthetic.main.layout_assessment_choice.*
import kotlinx.android.synthetic.main.layout_assessment_summary.*
import java.util.*
import kotlin.math.ceil

class AssessmentActivity : AppCompatActivity() {
    private lateinit var repository: LearnRepository

    private var assessments: List<Assessment>? = null
    private var topics: List<Topic>? = null
    private var currentTopicIndex = 0
    private var currentQuestionIndex = 0
    private var currentQuestion: Assessment? = null
    private var runningScore = 0
    private var timerTime = 0
    private var currentTimer: Hourglass? = null
    private var answerTracker: ArrayList<AnswerTracker>? = null
    private var assessmentFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment)

        repository = LearnRepository.getInstance(this)

        button_restart.setOnClickListener { restart() }
        button_exit.setOnClickListener { exit() }
        button_pause.setOnClickListener { pause() }

        topics = repository.getAllTopic()
        initAssessment()
    }

    private fun initAssessment() {
        currentTopicIndex = -1
        runningScore = 0
        answerTracker = ArrayList()
        assessmentFinished = false

        nextTopic()
    }

    private fun clearQuestionnaire() {
        container_choice.removeAllViews()
        label_score.text = ""
        label_question.text = ""
        label_time.text = ""
        label_question_count.text = ""
        text_score.text = ""
        text_high_score.text = ""
        text_correct_count.text = getString(R.string.text_correct_count, 0, 0)

        currentQuestionIndex = -1
        currentQuestion = null
        timerTime = 0
        currentTimer = null

        hideAssessmentView()
        hideAssessmentSummary()
    }

    private fun showAssessmentView() {
        container_main.visibility = View.VISIBLE
    }

    private fun hideAssessmentView() {
        container_main.visibility = View.GONE
    }

    private fun showAssessmentSummary() {
        container_summary.visibility = View.VISIBLE
    }

    private fun hideAssessmentSummary() {
        container_summary.visibility = View.GONE
    }

    @SuppressLint("StaticFieldLeak")
    private fun startAssessmentForCurrentTopic(){
        showAssessmentView()

        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                topics?.get(currentTopicIndex)?.let { topic ->
                    assessments = repository.getRandomQuestions(topic.id)
                }
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                nextQuestion()
            }
        }.execute()
    }

    private fun nextTopic(){
        clearQuestionnaire()
        currentTopicIndex += 1

        if (currentTopicIndex > (topics?.size ?: 0) - 1) {
            endOfAssessment()
            return
        }

        val currentTopic = topics?.get(currentTopicIndex)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_quiz_start_title)
        builder.setMessage(getString(R.string.dialog_quiz_start_message, currentTopic?.name))
        builder.setPositiveButton(R.string.button_text_resume) { dialogInterface, i -> startAssessmentForCurrentTopic() }
        builder.setNeutralButton(R.string.button_text_exit) { dialogInterface, i -> exit() }
        builder.setOnCancelListener { resume() }
        builder.show()
    }

    private fun nextQuestion() {
        currentQuestionIndex += 1

        if (currentQuestionIndex > (assessments?.size ?: 0) - 1) {
            nextTopic()
            return
        }

        showCountLabel()

        currentQuestion = assessments?.get(currentQuestionIndex)
        label_question.text = currentQuestion?.question

        showCountLabel()
        showScore()
        showQuestion()
        startTimer()
    }

    private fun startTimer() {
        val oneSecondTime = 1000

        currentTimer?.stopTimer()
        currentTimer = null

        label_time?.text = QUESTION_DURATION_IN_SEC.toString()
        currentTimer = object : Hourglass((QUESTION_DURATION_IN_SEC * oneSecondTime).toLong(), oneSecondTime.toLong()) {
            override fun onTimerTick(timeRemaining: Long) {
                val second = ceil(timeRemaining / 1000.toDouble()).toInt()
                timerTime = QUESTION_DURATION_IN_SEC - second
                label_time?.text = second.toString()
            }

            override fun onTimerFinish() {
                timesUp()
            }
        }

        currentTimer?.startTimer()
    }

    private fun showQuestion() {
        container_choice?.removeAllViews()

        val question = currentQuestion ?: return

        // show question or image
        when(question.questionType) {
            QuestionType.TRUE_OR_FALSE,
            QuestionType.FILL_IN_BLANK_QUESTION,
            QuestionType.MULTIPLE_CHOICE_QUESTION -> {
                container_image_question.visibility = View.GONE
                label_question.visibility = View.VISIBLE

                label_question.text = question.question
            }

            QuestionType.FILL_IN_BLANK_IMAGE,
            QuestionType.MULTIPLE_CHOICE_IMAGE -> {
                container_image_question.visibility = View.VISIBLE
                label_question.visibility = View.GONE

                Picasso.with(this)
                        .load(question.imagePath)
                        .into(image_question)
            }
        }

        // show question answer input
        when(question.questionType) {
            QuestionType.FILL_IN_BLANK_QUESTION,
            QuestionType.FILL_IN_BLANK_IMAGE -> {
                container_choice?.addView(ApplicationUtil.createSpacer(this))

                val view = ApplicationUtil.inflateFill(layoutInflater) { answer ->
                    validateAnswer(answer, timerTime)
                }

                container_choice?.addView(view)

                container_choice?.addView(ApplicationUtil.createSpacer(this))
            }

            QuestionType.TRUE_OR_FALSE,
            QuestionType.MULTIPLE_CHOICE_QUESTION,
            QuestionType.MULTIPLE_CHOICE_IMAGE -> {
                val answerSet = question.choices?.let { it ->
                    it.shuffled()
                } ?: arrayListOf("TRUE", "FALSE")

                var index = 0
                answerSet.forEach { answer ->
                    container_choice?.addView(ApplicationUtil.createSpacer(this))

                    val view = ApplicationUtil.inflateChoice(layoutInflater, answer, View.OnClickListener {
                        validateAnswer(answer, timerTime)
                    })

                    container_choice?.addView(view)

                    if (index == answerSet.size - 1) {
                        container_choice?.addView(ApplicationUtil.createSpacer(this))
                    }

                    index++
                }
            }
        }
    }

    private fun validateAnswer(_answer: String?, time: Int) {
        var answer = _answer

        currentTimer?.stopTimer()
        currentTimer = null

        answer = answer ?: ""
        val score: Int
        score = if (time != -1 && answer.toLowerCase() == currentQuestion?.answer?.toLowerCase()) {
            1
        } else {
            0
        }

        val builder = AlertDialog.Builder(this)
        if (time == -1) {
            builder.setIcon(R.drawable.ic_time_up)
            builder.setTitle(R.string.dialog_times_up_title)
            builder.setMessage(getString(R.string.dialog_times_up_message, currentQuestion?.answer))
        } else {
            if (score == 0) {
                builder.setIcon(R.drawable.ic_wrong)
                builder.setTitle(R.string.dialog_answer_wrong_title)
                builder.setMessage(getString(R.string.dialog_answer_message, answer, currentQuestion?.answer))
            } else {
                builder.setIcon(R.drawable.ic_correct)
                builder.setTitle(R.string.dialog_answer_correct_title)
            }
        }
        builder.setPositiveButton(R.string.button_text_next) { dialogInterface, i -> nextQuestion() }
        builder.setNeutralButton(R.string.button_text_exit) { dialogInterface, i -> exit() }
        builder.setCancelable(false)

        val model = AnswerTracker()
        model.answer = answer
        model.score = score

        answerTracker?.add(model)
        runningScore += score

        builder.show()
    }

    private fun exit() {
        assessmentFinished = true
        finish()
    }

    private fun resume() {
        if (assessmentFinished) {
            return
        }
        currentTimer?.resumeTimer()
    }

    private fun pause() {
        if (assessmentFinished) {
            return
        }
        currentTimer?.pauseTimer()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_quiz_paused_title)
        builder.setMessage(R.string.dialog_quiz_paused_message)
        builder.setPositiveButton(R.string.button_text_resume) { dialogInterface, i -> resume() }
        builder.setNeutralButton(R.string.button_text_exit) { dialogInterface, i -> exit() }
        builder.setOnCancelListener { resume() }
        builder.show()
    }

    private fun timesUp() {
        try {
            validateAnswer(null, -1)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(LOG_TAG, Log.getStackTraceString(e))
        }
    }

    private fun endOfAssessment() {
        assessmentFinished = true
        hideAssessmentView()
        showAssessmentSummary()

        text_score.text = runningScore.toString()
        var counter = 0

        answerTracker?.forEach { answerTrackerModel ->
            if (answerTrackerModel.score > 0) {
                counter++
            }
        }

        text_high_score.text = "0" // TODO: show high score
        text_correct_count.text = getString(R.string.text_correct_count, counter, answerTracker?.size)
    }

    private fun restart() {
        clearQuestionnaire()
        initAssessment()
    }

    private fun showCountLabel() {
        val count = currentQuestionIndex + 1
        val total = assessments?.size
        val countLabel = "$count/$total"

        label_question_count.text = countLabel
    }

    private fun showScore() {
        label_score.text = runningScore.toString()
    }

    override fun onPause() {
        super.onPause()
        if (!assessmentFinished) {
            pause()
        }
    }

    override fun onBackPressed() {
        if (!assessmentFinished) {
            pause()
        }
    }

    private class AnswerTracker {
        var answer: String? = null
        var score = 0
    }

    companion object {
        private val LOG_TAG = AssessmentActivity::class.java.simpleName
        private const val QUESTION_DURATION_IN_SEC = 20
    }
}