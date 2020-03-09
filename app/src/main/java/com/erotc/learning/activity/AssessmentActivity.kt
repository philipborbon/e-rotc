package com.erotc.learning.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.erotc.learning.R
import com.erotc.learning.data.QuestionEntry
import com.erotc.learning.data.QuestionSet
import com.erotc.learning.repository.DictionaryRepository
import com.erotc.learning.util.ApplicationUtil
import com.erotc.learning.util.ScoreUtil
import com.erotc.learning.util.hourglass.Hourglass
import kotlinx.android.synthetic.main.activity_game_choice.*
import kotlinx.android.synthetic.main.layout_game_choice.*
import kotlinx.android.synthetic.main.layout_game_summary.*
import java.util.*
import kotlin.math.ceil

class AssessmentActivity : AppCompatActivity() {
    private lateinit var repository: DictionaryRepository
    private var questSetId: Long = 0

    private var questionSet: QuestionSet? = null
    private var questionEntries: List<QuestionEntry>? = null
    private var currentIndex = 0
    private var currentQuestion: QuestionEntry? = null
    private var runningScore = 0
    private var timerTime = 0
    private var currentTimer: Hourglass? = null
    private var answerTracker: ArrayList<AnswerTrackerModel>? = null
    private var gameFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_choice)

        questSetId = intent.getLongExtra(QUESTION_SET_ID, -1)
        repository = DictionaryRepository.getInstance(this)

        button_next.setOnClickListener { nextQuestionSet() }
        button_restart.setOnClickListener { restartQuestionSet() }
        button_exit.setOnClickListener { exit() }
        button_pause.setOnClickListener { pause() }

        clear()
        initQuestionSet()
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
        button_next.visibility = View.GONE

        currentIndex = -1
        currentQuestion = null
        runningScore = 0
        timerTime = 0
        currentTimer = null
        answerTracker = ArrayList()
        gameFinished = false
        hideGameView()
        hideGameSummary()
    }

    private fun showGameView() {
        container_main.visibility = View.VISIBLE
    }

    private fun hideGameView() {
        container_main.visibility = View.GONE
    }

    private fun showGameSummary() {
        container_summary.visibility = View.VISIBLE
    }

    private fun hideGameSummary() {
        container_summary.visibility = View.GONE
    }

    @SuppressLint("StaticFieldLeak")
    private fun initQuestionSet() {
        showGameView()

        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                repository.getQuestionSet(questSetId)?.let { it
                    questionSet = it
                    questionEntries = repository.getRandomQuestionEntry(it)
                }

                return null
            }

            override fun onPostExecute(aVoid: Void?) {
                nextQuestion()
            }
        }.execute()
    }

    private fun nextQuestion() {
        currentIndex += 1

        if (currentIndex > questionEntries?.size ?: 0 - 1) {
            endOfQuestionSet()
            return
        }

        showCountLabel()

        currentQuestion = questionEntries?.get(currentIndex)
        label_question.text = currentQuestion?.question

        showCountLabel()
        showScore()
        showOptions()
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

    private fun showOptions() {
        container_choices?.removeAllViews()
        val randomAnswerSet = questionSet?.getRandomAnswerSet(QUESTION_OPTION_COUNT - 1) // minus the real answer
        randomAnswerSet?.add(currentQuestion?.answer)

        randomAnswerSet?.shuffle()

        var index = 0
        randomAnswerSet?.forEach { answer ->
            if (index != 0) {
                container_choices?.addView(ApplicationUtil.createSpacer(this))
            }
            val view = ApplicationUtil.inflateButton(layoutInflater, answer, View.OnClickListener {
                try {
                    validateAnswer(answer, timerTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(LOG_TAG, Log.getStackTraceString(e))
                }
            })

            container_choices?.addView(view)

            if (index == randomAnswerSet.size - 1) {
                container_choices?.addView(ApplicationUtil.createSpacer(this))
            }

            index++
        }
    }

    private fun validateAnswer(_answer: String?, time: Int) {
        var answer = _answer

        currentTimer?.stopTimer()
        currentTimer = null

        answer = answer ?: ""
        val score: Int
        score = if (time != -1 && answer.toLowerCase() == currentQuestion?.answer?.toLowerCase()) {
            ScoreUtil.getScore(questionSet?.level ?: 0, time)
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
        builder.setNegativeButton(R.string.button_text_start_again) { dialogInterface, i -> restartQuestionSet() }
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
        gameFinished = true
        finish()
    }

    private fun resume() {
        if (gameFinished) {
            return
        }
        currentTimer?.resumeTimer()
    }

    private fun pause() {
        if (gameFinished) {
            return
        }
        currentTimer?.pauseTimer()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_quiz_paused_title)
        builder.setMessage(R.string.dialog_quiz_paused_message)
        builder.setPositiveButton(R.string.button_text_resume) { dialogInterface, i -> resume() }
        builder.setNegativeButton(R.string.button_text_start_again) { dialogInterface, i -> restartQuestionSet() }
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
        gameFinished = true
        hideGameView()
        showGameSummary()

        val questionSet = questionSet ?: return

        val nextLevel = questionSet.getNextLevel(repository)
        text_score.text = runningScore.toString()
        var counter = 0

        answerTracker?.forEach { answerTrackerModel ->
            if (answerTrackerModel.score > 0) {
                counter++
            }
        }

        if (runningScore > questionSet.highScore) {
            questionSet.highScore = runningScore
            repository.updateQuestionSet(questionSet)

            if (questionSet.shouldUnlockNextLevel() && nextLevel != null) {
                repository.unlockNextLevel(questionSet)
            }
        }

        text_high_score.text = questionSet.highScore.toString()
        text_correct_count.text = getString(R.string.text_correct_count, counter, questionEntries?.size)

        if (nextLevel != null && questionSet.highScore < questionSet.pointsToProceed) {
            val neededScore = questionSet.pointsToProceed - runningScore
            text_unlock_score?.text = getString(R.string.text_unlock_score, neededScore, nextLevel.label)
            text_unlock_score?.visibility = View.VISIBLE
        } else if (nextLevel != null && questionSet.highScore >= questionSet.pointsToProceed) {
            button_next?.text = getString(R.string.text_play_next, nextLevel.label)
            button_next?.visibility = View.VISIBLE
        }
    }

    private fun restartQuestionSet() {
        clear()
        initQuestionSet()
    }

    private fun nextQuestionSet() {
        questionSet?.getNextLevel(repository)?.let { nextLevel ->
            val intent = Intent(this, AssessmentActivity::class.java)
            intent.putExtra(QUESTION_SET_ID, nextLevel.id)
            startActivity(intent)
            finish()
        }
    }

    private fun showCountLabel() {
        val count = currentIndex + 1
        val total = questionEntries?.size
        val countLabel = "$count/$total"

        label_question_count.text = countLabel
    }

    private fun showScore() {
        label_score.text = runningScore.toString()
    }

    override fun onPause() {
        super.onPause()
        if (!gameFinished) {
            pause()
        }
    }

    override fun onBackPressed() {
        if (!gameFinished) {
            pause()
        }
    }

    private class AnswerTrackerModel(val questionEntry: QuestionEntry?) {
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