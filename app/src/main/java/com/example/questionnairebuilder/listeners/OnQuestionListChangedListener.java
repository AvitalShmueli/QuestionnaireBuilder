package com.example.questionnairebuilder.listeners;

import com.example.questionnairebuilder.Question;

import java.util.List;

public interface OnQuestionListChangedListener {
    void onNoteListChanged(List<Question> questions);

}
