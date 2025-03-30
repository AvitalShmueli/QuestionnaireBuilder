package com.example.questionnairebuilder.models;

public class OpenEndedQuestion extends Question{
    public OpenEndedQuestion(String question) {
        super(question, QuestionTypeEnum.OPEN_ENDED_QUESTION);
    }
}
