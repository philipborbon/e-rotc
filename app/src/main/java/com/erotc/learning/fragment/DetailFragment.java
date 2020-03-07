package com.erotc.learning.fragment;


import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.erotc.learning.R;
import com.erotc.learning.dao.DictionaryEntry;
import com.erotc.learning.repository.DictionaryRepository;
import com.erotc.learning.util.ApplicationUtil;


public class DetailFragment extends Fragment {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String DICTIONARY_ID = "dictionary-id";

    private int mDictionaryId;

    private DictionaryRepository mDictionaryRepository;
    private DictionaryEntry mDictionaryEntry;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(int dictionaryId) {
        DetailFragment fragment = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(DICTIONARY_ID, dictionaryId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDictionaryId = getArguments().getInt(DICTIONARY_ID);
        }

        mDictionaryRepository = DictionaryRepository.getInstance(getContext());
        mDictionaryEntry = mDictionaryRepository.getDictionaryEntry(mDictionaryId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary_detail, container, false);

        TextView tagalog = view.findViewById(R.id.tagalog);
        TextView hiligaynon = view.findViewById(R.id.hiligaynon);
        TextView ilocano = view.findViewById(R.id.ilocano);
        TextView definition = view.findViewById(R.id.definition);
        TextView tagalogExample = view.findViewById(R.id.example_tagalog);
        TextView hiligaynonExample = view.findViewById(R.id.example_hiligaynon);
        TextView ilocanoExample = view.findViewById(R.id.example_ilocano);
        ImageButton playIlocano = view.findViewById(R.id.play_ilocano);
        ImageButton playHiligaynon = view.findViewById(R.id.play_hiligaynon);

        final String ilocanoAudio = ApplicationUtil.getIlocanoAudioFileName(mDictionaryEntry.getTagalog());
        int ilocanoAudioId = getResourceId(ilocanoAudio);

        if (ilocanoAudioId != 0) {
            playIlocano.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playAudio(ilocanoAudio);
                }
            });
        } else {
            playIlocano.setVisibility(View.GONE);
        }

        final String hiligaynonAudio = ApplicationUtil.getHiligaynonAudioFileName(mDictionaryEntry.getTagalog());
        int hiligaynonAudioId = getResourceId(hiligaynonAudio);

        if (hiligaynonAudioId != 0) {
            playHiligaynon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playAudio(hiligaynonAudio);
                }
            });
        } else {
            playHiligaynon.setVisibility(View.GONE);
        }

        String strTagalog = mDictionaryEntry.getTagalog();
        String strHiligaynon = mDictionaryEntry.getHiligaynon();
        String strIlocano = mDictionaryEntry.getIlocano();

        tagalog.setText(strTagalog);
        hiligaynon.setText(strHiligaynon);
        ilocano.setText(strIlocano);

        definition.setText(mDictionaryEntry.getDefinition());

        tagalogExample.setText(boldText(strTagalog, mDictionaryEntry.getTagalogExample()));
        hiligaynonExample.setText(boldText(strHiligaynon, mDictionaryEntry.getHiligaynonExample()));
        ilocanoExample.setText(boldText(strIlocano, mDictionaryEntry.getIlocanoExample()));

        return view;
    }

    private SpannableStringBuilder boldText(String text, String sentence){
        String comparableText = text.toLowerCase();
        String comparableSentence = sentence.toLowerCase();

        int index = comparableSentence.indexOf(comparableText);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sentence);

        if ( index != -1 ){
            spannableStringBuilder.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), index, index + comparableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableStringBuilder;
    }

    private void playAudio(String filename){
        int resourceId = getResourceId(filename);
        MediaPlayer player = MediaPlayer.create(getContext(), resourceId);
        player.start();
    }

    private int getResourceId(String filename){
        return getResources().getIdentifier(filename, "raw", getActivity().getPackageName());
    }
}
