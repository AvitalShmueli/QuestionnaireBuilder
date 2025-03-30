package com.example.questionnairebuilder.models;

import java.util.UUID;

public abstract class Question {
    private UUID questionID;
    private String surveyID;
    private String questionTitle;
    private QuestionType type;
    private boolean mandatory;
    private int order;
    private String image;

    public Question() {
    }

    public Question(String question, QuestionType type) {
        this.questionTitle = question;
        this.type = type;
    }

    public Question setQuestionTitle(Question q){
        this.questionTitle = q.questionTitle;
        this.type = q.type;
        this.order = q.order;
        return this;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public Question setQuestion(String question) {
        this.questionTitle = question;
        return this;
    }

    public QuestionType getType() {
        return type;
    }

    public Question setType(QuestionType type) {
        this.type = type;
        return this;
    }

    public UUID getQuestionID() {
        return questionID;
    }

    public Question setQuestionID(UUID questionID) {
        this.questionID = questionID;
        return this;
    }

    public String getSurveyID() {
        return surveyID;
    }

    public Question setSurveyID(String surveyID) {
        this.surveyID = surveyID;
        return this;
    }

    public Question setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
        return this;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public Question setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public Question setOrder(int order) {
        this.order = order;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Question setImage(String image) {
        this.image = image;
        return this;
    }

    public boolean save(){
        // TODO: save to firebase
        return true;
    }
}
