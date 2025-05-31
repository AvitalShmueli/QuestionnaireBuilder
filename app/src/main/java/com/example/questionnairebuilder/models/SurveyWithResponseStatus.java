package com.example.questionnairebuilder.models;

public class SurveyWithResponseStatus {
    private Survey survey;
    private SurveyResponseStatus responseStatus;

    public SurveyWithResponseStatus(Survey survey, SurveyResponseStatus responseStatus) {
        this.survey = survey;
        this.responseStatus = responseStatus;
    }

    public Survey getSurvey() {
        return survey;
    }

    public SurveyResponseStatus getResponseStatus() {
        return responseStatus;
    }
}
