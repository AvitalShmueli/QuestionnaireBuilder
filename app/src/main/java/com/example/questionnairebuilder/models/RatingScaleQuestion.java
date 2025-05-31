package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingScaleQuestion extends Question implements AnalyzableQuestion {
    private int ratingScaleLevel;
    private int iconResourceId;
    private final Map<String, Integer> ratingDistribution = new HashMap<>();

    public RatingScaleQuestion() {
        super();
    }

    public RatingScaleQuestion(String question) {
        super(question, QuestionTypeEnum.RATING_SCALE);
    }

    public RatingScaleQuestion(int ratingScaleLevel, int iconResourceId) {
        this.ratingScaleLevel = ratingScaleLevel;
        this.iconResourceId = iconResourceId;
    }

    public int getRatingScaleLevel() {
        return ratingScaleLevel;
    }

    public RatingScaleQuestion setRatingScaleLevel(int ratingScaleLevel) {
        this.ratingScaleLevel = ratingScaleLevel;
        return this;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public RatingScaleQuestion setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
        return this;
    }

    @Override
    public Map<String, Integer> getAnswerDistribution() {
        return ratingDistribution;
    }

    @Override
    public void accumulateAnswers(List<String> values) {
        for (String value : values) {
            try {
                int intValue = (int) Float.parseFloat(value);
                String key = String.valueOf(intValue);
                ratingDistribution.merge(key, 1, Integer::sum);
            } catch (NumberFormatException e) {
                // Ignore invalid entries
            }
        }
    }
}
