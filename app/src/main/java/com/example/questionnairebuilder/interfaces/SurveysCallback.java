package com.example.questionnairebuilder.interfaces;

import com.example.questionnairebuilder.models.Survey;

import java.util.List;

public interface SurveysCallback {
    void onSurveysLoaded(List<Survey> surveys);
    void onError(Exception e);
}
