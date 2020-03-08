package com.erotc.learning.data;

import com.erotc.learning.repository.DictionaryRepository;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created on 10/15/2018.
 */
@DatabaseTable
public class QuestionSet {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String label;

    @DatabaseField
    private int level;

    @DatabaseField
    private boolean locked;

    @DatabaseField
    private int pointsToProceed;

    @DatabaseField
    private int highScore;

    @DatabaseField
    private String answerSet;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<QuestionEntry> questionEntries;

    private ArrayList<QuestionEntry> questionArray;

    private List<String> answerSetInList;
    private Random random;

    private boolean nextPulled;
    private QuestionSet nextLevel;

    public QuestionSet() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getPointsToProceed() {
        return pointsToProceed;
    }

    public void setPointsToProceed(int pointsToProceed) {
        this.pointsToProceed = pointsToProceed;
    }

    public ForeignCollection<QuestionEntry> getQuestionEntries() {
        return questionEntries;
    }

    public void setQuestionEntries(ForeignCollection<QuestionEntry> questionEntries) {
        this.questionEntries = questionEntries;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public String getAnswerSet() {
        return answerSet;
    }

    public void setAnswerSet(String answerSet) {
        this.answerSet = answerSet;
    }

    public ArrayList<QuestionEntry> getQuestionArray() {
        return questionArray;
    }

    public void setQuestionArray(ArrayList<QuestionEntry> questionArray) {
        this.questionArray = questionArray;
    }

    public boolean shouldUnlockNextLevel(){
        return (highScore >= pointsToProceed);
    }

    public ArrayList<String> getRandomAnswerSet(int count){
        if ( answerSetInList == null ){
            answerSetInList = Arrays.asList(this.answerSet.split("\\s*,\\s*"));
        }

        if ( random == null ){
            random = new Random();
        }

        ArrayList<Integer> selectedRandomIndex = new ArrayList<>();

        while (selectedRandomIndex.size() < count){
            int index = random.nextInt(answerSetInList.size());

            if ( selectedRandomIndex.size() != 0 ) {
                int lastIndex = selectedRandomIndex.get(selectedRandomIndex.size() - 1);

                if ( lastIndex == index ){
                    continue; // prevent duplicate random
                }
            }

            selectedRandomIndex.add(index);
        }

        ArrayList<String> randomAnswerSet = new ArrayList<>();

        for (Integer integer : selectedRandomIndex) {
            String answer = answerSetInList.get(integer);
            randomAnswerSet.add(answer);
        }

        return randomAnswerSet;
    }

    public QuestionSet getNextLevel(DictionaryRepository repository) throws SQLException {
        if ( nextLevel == null && !nextPulled ){
            nextLevel = repository.getNextLevel(this);
            nextPulled = true;
        }

        return nextLevel;
    }
}
