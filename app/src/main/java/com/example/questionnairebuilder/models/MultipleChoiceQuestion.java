package com.example.questionnairebuilder.models;

import java.util.ArrayList;

public class MultipleChoiceQuestion extends ChoiceQuestion{
    private int allowedSelectionNum;

    public MultipleChoiceQuestion(String question) {
        super(question, "Multiple choice");
    }

    public MultipleChoiceQuestion(String question, ArrayList<String> choices, boolean other) {
        super(question, "Multiple choice", choices, other);
    }

    public int getAllowedSelectionNum() {
        return allowedSelectionNum;
    }

    public MultipleChoiceQuestion setAllowedSelectionNum(int allowedSelectionNum) {
        this.allowedSelectionNum = allowedSelectionNum;
        return this;
    }
}
