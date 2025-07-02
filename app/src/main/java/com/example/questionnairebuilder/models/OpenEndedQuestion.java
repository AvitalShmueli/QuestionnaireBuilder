package com.example.questionnairebuilder.models;

import java.util.ArrayList;
import java.util.List;

public class OpenEndedQuestion extends Question{
    boolean multipleLineAnswer;
    private String analysisResult;
    private List<String> allResponses = new ArrayList<>();

    public OpenEndedQuestion(){
        super();
    }

    public OpenEndedQuestion(String question) {
        super(question, QuestionTypeEnum.OPEN_ENDED_QUESTION);
        multipleLineAnswer = true;
        analysisResult = "";
    }

    public boolean isMultipleLineAnswer() {
        return multipleLineAnswer;
    }

    public OpenEndedQuestion setMultipleLineAnswer(boolean multipleLineAnswer) {
        this.multipleLineAnswer = multipleLineAnswer;
        return this;
    }

    public void setAnalysisResult(String result) {
        this.analysisResult = result;
    }

    public String getAnalysisResult() {
        return analysisResult;
    }

    public List<String> getAllResponses() {
        return allResponses;
    }

    public void setAllResponses(List<String> allResponses) {
        this.allResponses = allResponses;
    }
}
