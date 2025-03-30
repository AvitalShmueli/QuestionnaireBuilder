package com.example.questionnairebuilder.models;

public enum QuestionTypeEnum {
    OPEN_ENDED_QUESTION,
    SINGLE_CHOICE,
    YES_NO,
    DROPDOWN,
    MULTIPLE_CHOICE,
    DATE,
    RATING_SCALE,
    MATRIX_QUESTION;

    public boolean isSingleSelection() {
        return this == SINGLE_CHOICE || this == YES_NO || this == DROPDOWN || this == RATING_SCALE;
    }

}


