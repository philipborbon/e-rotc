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
import com.erotc.learning.repository.LearnRepository
import com.erotc.learning.util.hourglass.Hourglass
import kotlinx.android.synthetic.main.activity_assessment.*
import kotlinx.android.synthetic.main.layout_assessment_choice.*
import kotlinx.android.synthetic.main.layout_assessment_summary.*
import java.util.*
import kotlin.math.ceil

class AssessmentActivity : AppCompatActivity() {
    private lateinit var repository: LearnRepository

    private var assessments: List<Assessment>? = null
    private var currentIndex = 0
    private var currentQuestion: Assessment? = null
    private var runningScore = 0
    private var timerTime = 0
    private var currentTimer: Hourglass? = null
    private var answerTracker: ArrayList<AnswerTrackerModel>? = null
    private var assessmentFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment)

        repository = LearnRepository.getInstance(this)

        button_restart.setOnClickListener { restart() }
        button_exit.setOnClickListener { exit() }
        button_pause.setOnClickListener { pause() }

        clear()
        initAssessment()
    }

    private fun clear() {
        container_choices.removeAllViews()
        label_score.text = ""
        label_question.text = ""
        label_time.text = ""
        label_question_count.text = ""
        text_score.text = ""
        text_high_score.text = ""
        text_correct_count.text = getString(R.string.text_correct_count, 0, 0)
        text_unlock_score.text = ""
        text_unlock_score.visibility = View.GONE

        currentIndex = -1
        currentQuestion = null
        runningScore = 0
        timerTime = 0
        currentTimer = null
        answerTracker = ArrayList()
        assessmentFinished = false
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
    private fun initAssessment() {
        showAssessmentView()

        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                assessments = repository.getRandomQuestions()
                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                nextQuestion()
            }
        }.execute()
    }

    private fun nextQuestion() {
        currentIndex += 1

        if (currentIndex > assessments?.size ?: 0 - 1) {
            endOfQuestionSet()
            return
        }

        showCountLabel()

        currentQuestion = assessments?.get(currentIndex)
        label_question.text = currentQuestion?.question

        showCountLabel()
        showScore()
        showChoices()
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

    private fun showChoices() {
        // TODO: show assessment choices
//        container_choices?.removeAllViews()
//        val randomAnswerSet = questionSet?.getRandomAnswerSet(QUESTION_OPTION_COUNT - 1) // minus the real answer
//        randomAnswerSet?.add(currentQuestion?.answer)
//
//        randomAnswerSet?.shuffle()
//
//        var index = 0
//        randomAnswerSet?.forEach { answer ->
//            if (index != 0) {
//                container_choices?.addView(ApplicationUtil.createSpacer(this))
//            }
//            val view = ApplicationUtil.inflateButton(layoutInflater, answer, View.OnClickListener {
//                try {
//                    validateAnswer(answer, timerTime)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Log.e(LOG_TAG, Log.getStackTraceString(e))
//                }
//            })
//
//            container_choices?.addView(view)
//
//            if (index == randomAnswerSet.size - 1) {
//                container_choices?.addView(ApplicationUtil.createSpacer(this))
//            }
//
//            index++
//        }
    }

    private fun validateAnswer(_answer: String?, time: Int) {
        var answer = _answer

        currentTimer?.stopTimer()
        currentTimer = null

        answer = answer ?: ""
        val score: Int
        score = if (time != -1 && answer.toLowerCase() == currentQuestion?.answer?.toLowerCase()) {
            // ScoreUtil.getScore(questionSet?.level ?: 0, time)
            0 // TODO: determine score
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
        builder.setNegativeButton(R.string.button_text_try_again) { dialogInterface, i -> restart() }
        builder.setNeutralButton(R.string.button_text_exit) { dialogInterface, i -> exit() }
        builder.setCancelable(false)
        val model = AnswerTrackerModel(currentQuestion)
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
        builder.setNegativeButton(R.string.button_text_try_again) { dialogInterface, i -> restart() }
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

    private fun endOfQuestionSet() {
        assessmentFinished = true
        hideAssessmentView()
        showAssessmentSummary()

        // TODO: what to show at end of assessment
//        val questionSet = questionSet ?: return
//
//        val nextLevel = questionSet.getNextLevel(repository)
//        text_score.text = runningScore.toString()
//        var counter = 0
//
//        answerTracker?.forEach { answerTrackerModel ->
//            if (answerTrackerModel.score > 0) {
//                counter++
//            }
//        }
//
//        if (runningScore > questionSet.highScore) {
//            questionSet.highScore = runningScore
//            repository.updateQuestionSet(questionSet)
//
//            if (questionSet.shouldUnlockNextLevel() && nextLevel != null) {
//                repository.unlockNextLevel(questionSet)
//            }
//        }
//
//        text_high_score.text = questionSet.highScore.toString()
//        text_correct_count.text = getString(R.string.text_correct_count, counter, assessments?.size)
//
//        if (nextLevel != null && questionSet.highScore < questionSet.pointsToProceed) {
//            val neededScore = questionSet.pointsToProceed - runningScore
//            text_unlock_score?.text = getString(R.string.text_unlock_score, neededScore, nextLevel.label)
//            text_unlock_score?.visibility = View.VISIBLE
//        } else if (nextLevel != null && questionSet.highScore >= questionSet.pointsToProceed) {
//            button_next?.text = getString(R.string.text_play_next, nextLevel.label)
//            button_next?.visibility = View.VISIBLE
//        }
    }

    private fun restart() {
        clear()
        initAssessment()
    }

    private fun showCountLabel() {
        val count = currentIndex + 1
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

    private class AnswerTrackerModel(val assessment: Assessment?) {
        var answer: String? = null
        var score = 0
    }

    companion object {
        private val LOG_TAG = AssessmentActivity::class.java.simpleName
        private const val QUESTION_DURATION_IN_SEC = 10
        private const val QUESTION_OPTION_COUNT = 3
        const val QUESTION_SET_ID = "question-set-id"
    }
}