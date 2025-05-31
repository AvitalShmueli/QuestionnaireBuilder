package com.example.questionnairebuilder.interfaces;

import java.util.Map;

public interface ResponsesCallback {
    void onResponsesLoaded(Map<String, Boolean> answeredQuestions);
    void onError(Exception e);
}
