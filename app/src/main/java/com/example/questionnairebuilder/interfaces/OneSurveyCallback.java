package com.example.questionnairebuilder.interfaces;

import com.example.questionnairebuilder.models.Survey;

public interface OneSurveyCallback {
    void onSurveyLoaded(Survey survey);
    void onError(Exception e);
}
