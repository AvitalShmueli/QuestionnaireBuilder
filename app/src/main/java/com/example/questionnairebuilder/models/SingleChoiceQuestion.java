package com.example.questionnairebuilder.models;

import java.util.ArrayList;

public class SingleChoiceQuestion extends ChoiceQuestion{

    public SingleChoiceQuestion(String question, QuestionType type) {
        super(question, type);
    }

    public SingleChoiceQuestion(String question, QuestionType type, ArrayList<String> choices, boolean other) {
        super(question, type, choices, other);
    }

}
