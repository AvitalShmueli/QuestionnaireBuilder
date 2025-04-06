package com.example.questionnairebuilder.models;

public class RatingScaleQuestion extends Question{
    private int ratingScaleLevel;
    private int iconResourceId;

    public RatingScaleQuestion() {
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
}
