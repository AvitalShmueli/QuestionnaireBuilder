package com.example.questionnairebuilder.interfaces;

import com.example.questionnairebuilder.models.Question;

import java.util.List;

public interface QuestionsCallback {
    void onQuestionsLoaded(List<Question> questions);
    void onError(Exception e);
}
