package com.example.questionnairebuilder.models;

public class OpenEndedQuestion extends Question{
    boolean multipleLineAnswer;
    private String analysisResult;

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
}
