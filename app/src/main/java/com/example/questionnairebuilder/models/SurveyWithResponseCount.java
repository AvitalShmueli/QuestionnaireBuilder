package com.example.questionnairebuilder.models;

public class SurveyWithResponseCount {
    private Survey survey;
    private int responseCount;
    private boolean isLoading;

    public SurveyWithResponseCount(Survey survey) {
        this.survey = survey;
        this.responseCount = 0;
        this.isLoading = true;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public int getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(int responseCount) {
        this.responseCount = responseCount;
        this.isLoading = false;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
}
