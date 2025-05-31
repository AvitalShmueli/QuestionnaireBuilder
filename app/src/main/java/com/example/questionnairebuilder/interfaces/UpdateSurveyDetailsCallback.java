package com.example.questionnairebuilder.interfaces;

import com.example.questionnairebuilder.models.Survey;

public interface UpdateSurveyDetailsCallback {
    void onSuccess(Survey updatedSurvey);
    void onFailure(Exception e);
}
