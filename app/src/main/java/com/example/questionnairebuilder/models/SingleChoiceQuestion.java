package com.example.questionnairebuilder.models;

import java.util.ArrayList;

public class SingleChoiceQuestion extends ChoiceQuestion{

    public SingleChoiceQuestion() {
        super();
    }

    public SingleChoiceQuestion(String question, QuestionTypeEnum type) {
        super(question, type);
    }

    public SingleChoiceQuestion(String question, QuestionTypeEnum type, ArrayList<String> choices, boolean other) {
        super(question, type, choices, other);
    }
}
