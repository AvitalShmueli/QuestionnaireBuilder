package com.example.questionnairebuilder.interfaces;

import com.example.questionnairebuilder.models.SurveyWithResponseCount;

import java.util.List;

public interface SurveysWithCountCallback {
    void onSurveysLoaded(List<SurveyWithResponseCount> surveysWithCount);
    void onSurveyCountUpdated(int position, int count);
    void onError(Exception error);
}
