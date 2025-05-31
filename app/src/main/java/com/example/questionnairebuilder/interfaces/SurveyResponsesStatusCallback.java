package com.example.questionnairebuilder.interfaces;

import com.example.questionnairebuilder.models.SurveyResponseStatus;

import java.util.List;

public interface SurveyResponsesStatusCallback {
    void onResponseStatusesLoaded(List<SurveyResponseStatus> responseStatuses);
    void onError(Exception e);
}
