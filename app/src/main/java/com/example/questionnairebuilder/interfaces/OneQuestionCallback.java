package com.example.questionnairebuilder.interfaces;

import com.example.questionnairebuilder.models.Question;

public interface OneQuestionCallback {
    void onQuestionLoaded(Question question);
    void onError(Exception e);
}
