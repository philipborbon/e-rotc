package com.erotc.learning.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erotc.learning.R;
import com.erotc.learning.repository.DictionaryRepository;

/**
 * Created on 11/12/2018.
 */
public class ApplicationUtil {
    private static final String LOG_TAG = ApplicationUtil.class.getSimpleName();

    public static boolean shouldInitialize(Context context){
        DictionaryRepository repository = DictionaryRepository.getInstance(context);
        return ( repository.isDictionaryEmpty() || repository.isQuestionSetEmpty() );
    }

    public static View inflateButton(LayoutInflater layoutInflater, String label, View.OnClickListener onClickListener){
        View view = layoutInflater.inflate(R.layout.layout_button, null);

        TextView viewLabel = view.findViewById(R.id.label);
        viewLabel.setText(label);

        view.setOnClickListener(onClickListener);

        return view;
    }

    public static View inflateLockedButton(LayoutInflater layoutInflater, String label){
        View view = layoutInflater.inflate(R.layout.layout_button_locked, null);

        TextView viewLabel = view.findViewById(R.id.label);
        viewLabel.setText(label);

        return view;
    }

    public static View createSpacer(Context context){
        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        ));

        return view;
    }

    public static String getIlocanoAudioFileName(String string){
        return "i_" + cleanFileNameString(string);
    }

    public static String getHiligaynonAudioFileName(String string){
        return "h_" + cleanFileNameString(string);
    }

    private static String cleanFileNameString(String string){
        return string.toLowerCase()
                .replace(" ", "_")
                .replace("-", "_")
                .replace("\"", "")
                .replace(",", "_")
                .replace("__", "_");
    }
}
