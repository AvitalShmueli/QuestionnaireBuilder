package com.example.questionnairebuilder.models;

public class OpenEndedQuestion extends Question{
    boolean multipleLineAnswer;

    public OpenEndedQuestion(String question) {
        super(question, QuestionTypeEnum.OPEN_ENDED_QUESTION);
        multipleLineAnswer = true;
    }

    public boolean isMultipleLineAnswer() {
        return multipleLineAnswer;
    }

    public OpenEndedQuestion setMultipleLineAnswer(boolean multipleLineAnswer) {
        this.multipleLineAnswer = multipleLineAnswer;
        return this;
    }
}
