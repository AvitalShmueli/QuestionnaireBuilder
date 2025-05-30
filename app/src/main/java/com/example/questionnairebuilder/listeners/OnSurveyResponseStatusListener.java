package com.example.questionnairebuilder.listeners;

import com.example.questionnairebuilder.models.SurveyResponseStatus;

public interface OnSurveyResponseStatusListener {
    void onSuccess(SurveyResponseStatus status);
    void onFailure(Exception e);
}
