package com.example.questionnairebuilder.models;

import java.util.ArrayList;

public abstract class ChoiceQuestion extends Question{
    private ArrayList<String> choices;
    private boolean other;

    public ChoiceQuestion(String question, QuestionType type) {
        super(question, type);
        this.choices = new ArrayList<>();
        this.other = false;
    }

    public ChoiceQuestion(String question, QuestionType type, ArrayList<String> choices, boolean other) {
        super(question, type);
        this.choices = choices;
        this.other = other;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public ChoiceQuestion setChoices(ArrayList<String> choices) {
        this.choices = choices;
        return this;
    }

    public ChoiceQuestion addChoice(String choice){
        choices.add(choice);
        return this;
    }

    public boolean isOther() {
        return other;
    }

    public ChoiceQuestion setOther(boolean other) {
        this.other = other;
        return this;
    }
}
