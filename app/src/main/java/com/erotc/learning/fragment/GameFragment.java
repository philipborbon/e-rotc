package com.erotc.learning.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.erotc.learning.R;
import com.erotc.learning.activity.GameChoiceActivity;
import com.erotc.learning.dao.QuestionSet;
import com.erotc.learning.repository.DictionaryRepository;
import com.erotc.learning.util.ApplicationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {
    private static final String LOG_TAG = GameFragment.class.getSimpleName();

    private LinearLayout mContainerQuestionSet;

    private DictionaryRepository mRepository;
    private List<QuestionSet> mQuestionSets;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = DictionaryRepository.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        mContainerQuestionSet = view.findViewById(R.id.container_question_set);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchAndShowQuestionSets();
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchAndShowQuestionSets(){
        mContainerQuestionSet.removeAllViews();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mQuestionSets = mRepository.getQuestionSets();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, Log.getStackTraceString(e));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                layoutQuestionSets();
            }
        }.execute();
    }

    private void layoutQuestionSets(){
        int index = 0;
        for (final QuestionSet questionSet : mQuestionSets){
            mContainerQuestionSet.addView(ApplicationUtil.createSpacer(getContext()));

            if ( questionSet.isLocked() ){
                View view = ApplicationUtil.inflateLockedButton(getLayoutInflater(), questionSet.getLabel());
                mContainerQuestionSet.addView(view);
            } else {
                View view = ApplicationUtil.inflateButton(getLayoutInflater(),
                        questionSet.getLabel(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), GameChoiceActivity.class);
                                intent.putExtra(GameChoiceActivity.QUESTION_SET_ID, questionSet.getId());
                                startActivity(intent);
                            }
                        });

                mContainerQuestionSet.addView(view);
            }

            if ( index == (mQuestionSets.size() - 1) ){
                mContainerQuestionSet.addView(ApplicationUtil.createSpacer(getContext()));
            }

            index++;
        }
    }
}
