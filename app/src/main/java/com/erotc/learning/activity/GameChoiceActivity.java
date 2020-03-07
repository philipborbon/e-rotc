package com.erotc.learning.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erotc.learning.R;
import com.erotc.learning.dao.QuestionEntry;
import com.erotc.learning.dao.QuestionSet;
import com.erotc.learning.repository.DictionaryRepository;
import com.erotc.learning.util.ApplicationUtil;
import com.erotc.learning.util.ScoreUtil;
import com.erotc.learning.util.hourglass.Hourglass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameChoiceActivity extends AppCompatActivity {
    private static final String LOG_TAG = GameChoiceActivity.class.getSimpleName();

    private static final int QUESTION_DURATION_IN_SEC = 10;
    private static final int QUESTION_OPTION_COUNT = 3;

    public static final String QUESTION_SET_ID = "question-set-id";

    private DictionaryRepository mRepository;

    private ImageButton mButtonPause;
    private TextView mLabelQuestionCount;
    private TextView mLabelScoreRunningCount;
    private TextView mLabelTimer;
    private TextView mLabelQuestion;
    private LinearLayout mContainerChoices;

    private TextView mTextScore;
    private TextView mTextHighScore;
    private TextView mTextCorrectCount;
    private TextView mTextUnlockScore;

    private Button mButtonRestart;
    private Button mButtonExit;
    private Button mButtonNextQuestionSet;

    private int mQuestionSetId;
    private QuestionSet mQuestionSet;
    private List<QuestionEntry> mQuestionEntries;

    private int mCurrentIndex;
    private QuestionEntry mCurrentQuestion;
    private int mRunningScore;
    private int mTimerTime;
    private Hourglass mCurrentTimer;

    private ArrayList<AnswerTrackerModel> mAnswerTracker;

    private boolean mGameFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_choice);

        mQuestionSetId = getIntent().getIntExtra(QUESTION_SET_ID, -1);
        mRepository = DictionaryRepository.getInstance(this);

        mButtonPause = findViewById(R.id.button_pause);
        mLabelQuestionCount = findViewById(R.id.label_question_count);
        mLabelTimer = findViewById(R.id.label_time);
        mLabelScoreRunningCount = findViewById(R.id.label_score);
        mLabelQuestion = findViewById(R.id.question);
        mContainerChoices = findViewById(R.id.container_choices);

        mTextScore = findViewById(R.id.text_score);
        mTextHighScore = findViewById(R.id.text_high_score);
        mTextCorrectCount = findViewById(R.id.text_correct_count);
        mTextUnlockScore = findViewById(R.id.text_unlock_score);

        mButtonRestart = findViewById(R.id.button_restart);
        mButtonExit = findViewById(R.id.button_exit);
        mButtonNextQuestionSet = findViewById(R.id.button_next);

        mButtonNextQuestionSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestionSet();
            }
        });

        mButtonRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartQuestionSet();
            }
        });

        mButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        mButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause();
            }
        });

        clear();

        initQuestionSet();
    }

    private void clear(){
        mContainerChoices.removeAllViews();
        mLabelScoreRunningCount.setText("");
        mLabelQuestion.setText("");
        mLabelTimer.setText("");
        mLabelQuestionCount.setText("");

        mTextScore.setText("");
        mTextHighScore.setText("");
        mTextCorrectCount.setText(getString(R.string.text_correct_count, 0, 0));
        mTextUnlockScore.setText("");
        mTextUnlockScore.setVisibility(View.GONE);
        mButtonNextQuestionSet.setVisibility(View.GONE);

        mCurrentIndex = -1;
        mCurrentQuestion = null;
        mRunningScore = 0;
        mTimerTime = 0;
        mCurrentTimer = null;

        mAnswerTracker = new ArrayList<>();
        mGameFinished = false;

        hideGameView();
        hideGameSummary();
    }

    private void showGameView(){
        findViewById(R.id.container_main).setVisibility(View.VISIBLE);
    }

    private void hideGameView(){
        findViewById(R.id.container_main).setVisibility(View.GONE);
    }

    private void showGameSummary(){
        findViewById(R.id.container_summary).setVisibility(View.VISIBLE);
    }

    private void hideGameSummary(){
        findViewById(R.id.container_summary).setVisibility(View.GONE);
    }

    @SuppressLint("StaticFieldLeak")
    private void initQuestionSet(){
        showGameView();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mQuestionSet = mRepository.getQuestionSet(mQuestionSetId);
                try {
                    mQuestionEntries = mRepository.getRandomQuestionEntry(mQuestionSet);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, Log.getStackTraceString(e));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                nextQuestion();
            }
        }.execute();
    }

    private void nextQuestion(){
        mCurrentIndex = mCurrentIndex + 1;

        if ( mCurrentIndex > mQuestionEntries.size() - 1 ){
            try {
                endOfQuestionSet();
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }

            return;
        }

        showCountLabel();

        mCurrentQuestion = mQuestionEntries.get(mCurrentIndex);

        mLabelQuestion.setText(mCurrentQuestion.getQuestion());

        showCountLabel();
        showScore();
        showOptions();
        startTimer();
    }

    private void startTimer(){
        int oneSecondTime = 1000;

        if ( mCurrentTimer != null ){
            mCurrentTimer.stopTimer();
            mCurrentTimer = null;
        }

        mLabelTimer.setText(String.valueOf(QUESTION_DURATION_IN_SEC));

        mCurrentTimer = new Hourglass(QUESTION_DURATION_IN_SEC * oneSecondTime, oneSecondTime) {
            @Override
            public void onTimerTick(long timeRemaining) {
                int second = (int) Math.ceil(timeRemaining / 1000);
                mTimerTime = QUESTION_DURATION_IN_SEC - second;
                mLabelTimer.setText(String.valueOf(second));
            }

            @Override
            public void onTimerFinish() {
                timesUp();
            }
        };

        mCurrentTimer.startTimer();
    }

    private void showOptions(){
        mContainerChoices.removeAllViews();

        ArrayList<String> randomAnswerSet = mQuestionSet.getRandomAnswerSet(QUESTION_OPTION_COUNT - 1); // minus the real answer
        randomAnswerSet.add(mCurrentQuestion.getAnswer());

        Collections.shuffle(randomAnswerSet);

        int index = 0;
        for (final String answer : randomAnswerSet){
            if ( index != 0 ){
                mContainerChoices.addView(ApplicationUtil.createSpacer(this));
            }

            View view = ApplicationUtil.inflateButton(getLayoutInflater(),
                    answer,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                validateAnswer(answer, mTimerTime);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(LOG_TAG, Log.getStackTraceString(e));
                            }
                        }
                    });

            mContainerChoices.addView(view);

            if ( index == (randomAnswerSet.size() - 1) ){
                mContainerChoices.addView(ApplicationUtil.createSpacer(this));
            }

            index++;
        }
    }

    private void validateAnswer(String answer, int time) throws Exception {
        if ( mCurrentTimer != null ){
            mCurrentTimer.stopTimer();
            mCurrentTimer = null;
        }

        answer = answer == null ? "" : answer;

        int score;
        if ( time != -1 && answer.toLowerCase().equals(mCurrentQuestion.getAnswer().toLowerCase()) ){
            score = ScoreUtil.getScore(mQuestionSet.getLevel(), time);
        } else {
            score = 0;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if ( time == -1 ){
            builder.setIcon(R.drawable.ic_time_up);
            builder.setTitle(R.string.dialog_times_up_title);
            builder.setMessage(getString(R.string.dialog_times_up_message, mCurrentQuestion.getAnswer()));
        } else {
            if ( score == 0 ){
                builder.setIcon(R.drawable.ic_wrong);
                builder.setTitle(R.string.dialog_answer_wrong_title);
                builder.setMessage(getString(R.string.dialog_answer_message, answer, mCurrentQuestion.getAnswer()));
            } else {
                builder.setIcon(R.drawable.ic_correct);
                builder.setTitle(R.string.dialog_answer_correct_title);
            }
        }

        builder.setPositiveButton(R.string.button_text_next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nextQuestion();
            }
        });
        builder.setNegativeButton(R.string.button_text_start_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restartQuestionSet();
            }
        });
        builder.setNeutralButton(R.string.button_text_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        });
        builder.setCancelable(false);

        AnswerTrackerModel model = new AnswerTrackerModel(mCurrentQuestion);
        model.setAnswer(answer);
        model.setScore(score);

        mAnswerTracker.add(model);

        mRunningScore += score;

        builder.show();
    }

    private void exit(){
        mGameFinished = true;
        finish();
    }

    private void resume(){
        if ( mGameFinished ){
            return;
        }

        mCurrentTimer.resumeTimer();
    }

    private void pause(){
        if ( mGameFinished ){
            return;
        }

        mCurrentTimer.pauseTimer();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_quiz_paused_title);
        builder.setMessage(R.string.dialog_quiz_paused_message);

        builder.setPositiveButton(R.string.button_text_resume, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resume();
            }
        });
        builder.setNegativeButton(R.string.button_text_start_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restartQuestionSet();
            }
        });
        builder.setNeutralButton(R.string.button_text_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                resume();
            }
        });

        builder.show();
    }

    private void timesUp(){
        try {
            validateAnswer(null, -1);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }
    }

    private void endOfQuestionSet() throws SQLException {
        mGameFinished = true;

        hideGameView();
        showGameSummary();

        QuestionSet nextLevel = mQuestionSet.getNextLevel(mRepository);

        mTextScore.setText(String.valueOf(mRunningScore));

        int counter = 0;
        for (AnswerTrackerModel answerTrackerModel : mAnswerTracker){
            if ( answerTrackerModel.getScore() > 0 ){
                counter++;
            }
        }

        if ( mRunningScore > mQuestionSet.getHighScore() ){
            mQuestionSet.setHighScore(mRunningScore);
            mRepository.updateQuestionSet(mQuestionSet);

            if ( mQuestionSet.shouldUnlockNextLevel() && nextLevel != null ){
                mRepository.unlockNextLevel(mQuestionSet);
            }
        }

        mTextHighScore.setText(String.valueOf(mQuestionSet.getHighScore()));
        mTextCorrectCount.setText(getString(R.string.text_correct_count, counter, mQuestionEntries.size()));

        if ( nextLevel != null && mQuestionSet.getHighScore() < mQuestionSet.getPointsToProceed() ){
            int neededScore = mQuestionSet.getPointsToProceed() - mRunningScore;
            mTextUnlockScore.setText(getString(R.string.text_unlock_score, neededScore, nextLevel.getLabel()));
            mTextUnlockScore.setVisibility(View.VISIBLE);
        } else if ( nextLevel != null && mQuestionSet.getHighScore() >= mQuestionSet.getPointsToProceed() ){
            mButtonNextQuestionSet.setText(getString(R.string.text_play_next, nextLevel.getLabel()));
            mButtonNextQuestionSet.setVisibility(View.VISIBLE);
        }
    }

    private void restartQuestionSet(){
        clear();
        initQuestionSet();
    }

    private void nextQuestionSet(){
        try {
            QuestionSet nextLevel = mQuestionSet.getNextLevel(mRepository);

            if ( nextLevel != null ){
                Intent intent = new Intent(this, GameChoiceActivity.class);
                intent.putExtra(QUESTION_SET_ID, nextLevel.getId());

                startActivity(intent);
                finish();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }
    }

    private void showCountLabel(){
        int count = mCurrentIndex + 1;
        int total = mQuestionEntries.size();

        String countLabel = count + "/" + total;

        mLabelQuestionCount.setText(countLabel);
    }

    private void showScore(){
        mLabelScoreRunningCount.setText(String.valueOf(mRunningScore));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if ( !mGameFinished ){
            pause();
        }
    }

    @Override
    public void onBackPressed() {
        if ( !mGameFinished ){
            pause();
        }
    }

    private static class AnswerTrackerModel {
        private QuestionEntry questionEntry;
        private String answer;
        private int score;

        public AnswerTrackerModel(QuestionEntry questionEntry) {
            this.questionEntry = questionEntry;
        }

        public QuestionEntry getQuestionEntry() {
            return questionEntry;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
