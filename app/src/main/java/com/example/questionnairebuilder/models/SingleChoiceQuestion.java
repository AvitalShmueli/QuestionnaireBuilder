package com.example.questionnairebuilder.models;

import java.util.ArrayList;

public class SingleChoiceQuestion extends ChoiceQuestion{
    private SingleChoiceType choiceType;

    public SingleChoiceQuestion(String question, SingleChoiceType choiceType) {
        super(question, "Single choice");
        this.choiceType = choiceType;
    }

    public SingleChoiceQuestion(String question, ArrayList<String> choices, boolean other, SingleChoiceType choiceType) {
        super(question, "Single choice", choices, other);
        this.choiceType = choiceType;
    }

    public SingleChoiceType getChoiceType() {
        return choiceType;
    }

    public SingleChoiceQuestion setChoiceType(SingleChoiceType choiceType) {
        this.choiceType = choiceType;
        return this;
    }
}
