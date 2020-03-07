package com.erotc.learning.fragment;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erotc.learning.R;
import com.erotc.learning.dao.DictionaryEntry;
import com.erotc.learning.dao.QuestionSet;
import com.erotc.learning.parser.DictionaryParser;
import com.erotc.learning.parser.QuestionParser;
import com.erotc.learning.repository.DictionaryRepository;

import org.json.JSONException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InitializeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InitializeFragment extends Fragment {
    private static final String LOG_TAG = InitializeFragment.class.getSimpleName();

    private TextView mLabel;

    public interface InitializeListener {
        void onDone();
    }

    private InitializeListener listener;

    public InitializeFragment() {
        // Required empty public constructor
    }

    public static InitializeFragment newInstance() {
        return new InitializeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initialize, container, false);

        mLabel = view.findViewById(R.id.label);

        initialize();

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void initialize(){
        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    DictionaryRepository repository = DictionaryRepository.getInstance(getContext());

                    if ( repository.isDictionaryEmpty() ){
                        publishProgress(getString(R.string.message_preparing_dictionary));

                        DictionaryParser dictionaryParser = new DictionaryParser (
                                getResources().openRawResource(R.raw.dictionary)
                        );

                        ArrayList<DictionaryEntry> entryList = dictionaryParser.parse();
                        repository.saveDictionaryEntries(entryList);
                    }

                    if ( repository.isQuestionSetEmpty() ){
                        publishProgress(getString(R.string.message_preparing_game));

                        QuestionParser questionParser = new QuestionParser(
                                getResources().openRawResource(R.raw.questionset)
                        );

                        ArrayList<QuestionSet> questionSetList = questionParser.parse();
                        repository.saveQuestionSets(questionSetList);
                    }
                } catch (SQLException | IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, Log.getStackTraceString(e));
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                mLabel.setText(values[0]);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if ( listener != null ){
                    listener.onDone();
                }
            }
        }.execute();
    }

    public void setListener(InitializeListener listener) {
        this.listener = listener;
    }
}
