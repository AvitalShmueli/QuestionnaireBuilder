package com.example.questionnairebuilder.models;

public class DateQuestion extends Question{
    private DateSelectionModeEnum dateMode;

    public DateQuestion() {
        super();
    }

    public DateQuestion(String question) {
        super(question, QuestionTypeEnum.DATE);
    }

    public DateQuestion(String question, DateSelectionModeEnum dateMode) {
        super(question, QuestionTypeEnum.DATE);
        this.dateMode = dateMode;
    }

    public DateSelectionModeEnum getDateMode() {
        return dateMode;
    }

    public DateQuestion setDateMode(DateSelectionModeEnum dateMode) {
        this.dateMode = dateMode;
        return this;
    }
}
