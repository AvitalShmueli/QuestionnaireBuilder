package com.example.questionnairebuilder.models;

import java.util.ArrayList;
import java.util.List;

public abstract class ChoiceQuestion extends Question{
    private ArrayList<String> choices;
    private boolean other;
    private List<String> responses = new ArrayList<>();

    public ChoiceQuestion() {
        super();
    }

    public ChoiceQuestion(String question, QuestionTypeEnum type) {
        super(question, type);
        this.choices = new ArrayList<>();
        this.other = false;
    }

    public ChoiceQuestion(String question, QuestionTypeEnum type, ArrayList<String> choices, boolean other) {
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

    public List<String> getResponses() {
        return responses;
    }

    public ChoiceQuestion setResponses(List<String> responses) {
        this.responses = responses;
        return this;
    }

    public ChoiceQuestion addResponse(String response) {
        this.responses.add(response);
        return this;
    }
}
