package com.example.questionnairebuilder.models;

import java.util.Date;

public class SurveyResponseStatus {
    public enum ResponseStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }

    private String surveyId;
    private String userId;
    private ResponseStatus status;
    private Date startedAt;
    private Date completedAt;

    public SurveyResponseStatus() {}

    public SurveyResponseStatus(String surveyId, String userId, ResponseStatus status, Date startedAt, Date completedAt) {
        this.surveyId = surveyId;
        this.userId = userId;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
}

