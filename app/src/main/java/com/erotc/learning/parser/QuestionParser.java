package com.erotc.learning.parser;

import com.erotc.learning.dao.QuestionEntry;
import com.erotc.learning.dao.QuestionSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created on 11/26/2018.
 */
public class QuestionParser {
    private static final String FIELD_LABEL = "label";
    private static final String FIELD_LEVEL = "level";
    private static final String FIELD_LOCKED = "locked";
    private static final String FIELD_POINTS_TO_PROCEED = "pointsToProceed";
    private static final String FIELD_ANSWER_SET = "answerSet";
    private static final String FIELD_QUESTION_ENTRIES = "questionEntries";
    private static final String FIELD_QUESTION = "question";
    private static final String FIELD_ANSWER = "answer";

    private InputStream inputStream;
    private ByteArrayOutputStream byteArrayOutputStream;

    public QuestionParser(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;

        loadDataToByteArrayOutputStream();
    }

    private void loadDataToByteArrayOutputStream() throws IOException {
        byteArrayOutputStream = new ByteArrayOutputStream();

        int dataTye = inputStream.read();

        try {
            while (dataTye != -1) {
                byteArrayOutputStream.write(dataTye);
                dataTye = inputStream.read();
            }
        } finally {
            inputStream.close();
        }
    }

    public ArrayList<QuestionSet> parse() throws JSONException {
        ArrayList<QuestionSet> questionSets = new ArrayList<>();

        JSONArray jArray = new JSONArray(byteArrayOutputStream.toString());

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jObject = jArray.getJSONObject(i);
            questionSets.add(readQuestionSet(jObject));
        }

        return questionSets;
    }

    private QuestionSet readQuestionSet(JSONObject jObject) throws JSONException {
        QuestionSet questionSet = new QuestionSet();
        questionSet.setLabel(jObject.getString(FIELD_LABEL));
        questionSet.setLevel(jObject.getInt(FIELD_LEVEL));
        questionSet.setLocked(jObject.getBoolean(FIELD_LOCKED));
        questionSet.setPointsToProceed(jObject.getInt(FIELD_POINTS_TO_PROCEED));
        questionSet.setAnswerSet(jObject.getString(FIELD_ANSWER_SET));
        questionSet.setQuestionArray(readQuestionEntryArray(jObject.getJSONArray(FIELD_QUESTION_ENTRIES)));

        return questionSet;
    }

    private ArrayList<QuestionEntry> readQuestionEntryArray(JSONArray jArray) throws JSONException {
        ArrayList<QuestionEntry> questionEntries = new ArrayList<>();

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jObject = jArray.getJSONObject(i);
            questionEntries.add(readQuestionEntry(jObject));
        }

        return questionEntries;
    }

    private QuestionEntry readQuestionEntry(JSONObject jObject) throws JSONException {
        QuestionEntry questionEntry = new QuestionEntry();
        questionEntry.setQuestion(jObject.getString(FIELD_QUESTION));
        questionEntry.setAnswer(jObject.getString(FIELD_ANSWER));

        return questionEntry;
    }
}
