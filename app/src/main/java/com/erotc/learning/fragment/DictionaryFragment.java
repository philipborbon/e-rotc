package com.erotc.learning.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.erotc.learning.R;
import com.erotc.learning.activity.RecentResultActivity;
import com.erotc.learning.adapter.ResultAdapter;
import com.erotc.learning.dao.DictionaryEntry;
import com.erotc.learning.dao.RecentSearchResult;
import com.erotc.learning.repository.DictionaryRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DictionaryFragment extends Fragment {
    private static final String LOG_TAG = DictionaryFragment.class.getSimpleName();

    private DictionaryRepository mDictionaryRepository;

    private List<DictionaryEntry> mResultList;
    private ResultAdapter mResultAdapter;
    private AsyncTask mPreviousTask;
    private View mNoResultView;
    private TextView mLabelRecent;

    private EditText mInputSearch;
    private RecyclerView mResultListRecyclerView;
    private Button mButtonSearch;

    public DictionaryFragment() {
        // Required empty public constructor
    }

    public static DictionaryFragment newInstance() {
        return new DictionaryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);

        mInputSearch = view.findViewById(R.id.inputSearchKey);
        mResultListRecyclerView = view.findViewById(R.id.resultList);
        mNoResultView = view.findViewById(R.id.label_no_results);
        mLabelRecent = view.findViewById(R.id.label_recent_result);
        mButtonSearch = view.findViewById(R.id.button_search);

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchClick();
            }
        });

        mInputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ( i == EditorInfo.IME_ACTION_SEARCH ){
                    onSearchClick();
                    return true;
                }

                return false;
            }
        });

        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDictionaryRepository = DictionaryRepository.getInstance(getActivity());

        initRecyclerView();

        hideNoResults();

        hideRecentLabel();

        return view;
    }

    private void initRecyclerView(){
        mResultAdapter = new ResultAdapter(new ResultAdapter.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemIndex = mResultListRecyclerView.getChildLayoutPosition(view);

                DictionaryEntry dictionaryEntry = mResultList.get(itemIndex);

                addAndShowRecent(dictionaryEntry);
            }
        });

        mResultListRecyclerView.setAdapter(mResultAdapter);
        mResultListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public boolean isSearchEmpty(){
        return mInputSearch.getText().toString().isEmpty();
    }

    public void clearInputSearch(){
        mInputSearch.setText("");
        mInputSearch.clearFocus();
    }

    public void onSearchClick(){
        hideKeyboard();
        mInputSearch.clearFocus();

        if ( mResultList.size() > 0 ){
            int indexOfMatch = DictionaryEntry.getIndexOfMatch(mInputSearch.getText().toString(), mResultList);
            DictionaryEntry dictionaryEntry = mResultList.get(indexOfMatch);
            addAndShowRecent(dictionaryEntry);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void showRecentResults() {
        if ( !mDictionaryRepository.isRecentResultEmpty() ) {
            showRecentLabel();

            hideNoResults();

            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        mResultList = mDictionaryRepository.getRecentSearchResults(false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Log.e(LOG_TAG, Log.getStackTraceString(e));
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mResultAdapter.setResultList(mResultList);
                    mResultAdapter.notifyDataSetChanged();

                    mResultListRecyclerView.scrollToPosition(0);
                }
            };

            task.execute();

            mPreviousTask = task;
        } else {
            mResultList = new ArrayList<>();
            mResultAdapter.setResultList(mResultList);
            mResultAdapter.notifyDataSetChanged();
        }
    }

    private void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void search(){
        hideRecentLabel();

        if ( mPreviousTask != null ) {
            mPreviousTask.cancel(true);
        }

        final String keyword = mInputSearch.getText().toString();

        if ( keyword.isEmpty() ){
            showRecentResults();
            return;
        }

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                hideNoResults();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mResultList = mDictionaryRepository.search(keyword);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, Log.getStackTraceString(e));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mResultAdapter.setResultList(mResultList);
                mResultAdapter.notifyDataSetChanged();

                if ( mResultList.size() == 0 ){
                    showNoResults();
                } else {
                    mResultListRecyclerView.scrollToPosition(0);
                }
            }
        };

        task.execute();

        mPreviousTask = task;
    }

    private void showNoResults(){
        mNoResultView.setVisibility(View.VISIBLE);
    }

    private void hideNoResults(){
        mNoResultView.setVisibility(View.GONE);
    }

    private void showRecentLabel(){
        mLabelRecent.setVisibility(View.VISIBLE);
    }

    private void hideRecentLabel(){
        mLabelRecent.setVisibility(View.GONE);
    }

    private void addAndShowRecent(DictionaryEntry dictionaryEntry){
        RecentSearchResult recent = new RecentSearchResult();
        recent.setEntry(dictionaryEntry);

        try {
            mDictionaryRepository.saveRecent(recent);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }

        Intent intent = new Intent(getActivity(), RecentResultActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        String keyword = mInputSearch.getText().toString();
        if ( keyword.isEmpty() ){
            showRecentResults();
        }
    }
}
