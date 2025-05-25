package com.example.questionnairebuilder.interfaces;

import java.util.Set;

public interface ResponsesCallback {
    void onResponsesLoaded(Set<String> answeredQuestionIds);
    void onError(Exception e);
}
