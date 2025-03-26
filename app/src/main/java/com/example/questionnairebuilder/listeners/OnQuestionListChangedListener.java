package com.example.questionnairebuilder.listeners;

import com.example.questionnairebuilder.models.Question;

import java.util.List;

public interface OnQuestionListChangedListener {
    void onNoteListChanged(List<Question> questions);

}
