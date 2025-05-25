package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MultipleChoiceQuestion extends ChoiceQuestion implements AnalyzableQuestion {
    private int allowedSelectionNum;

    public MultipleChoiceQuestion() {
        super();
    }

    public MultipleChoiceQuestion(String question) {
        super(question, QuestionTypeEnum.MULTIPLE_CHOICE);
    }

    public MultipleChoiceQuestion(String question, ArrayList<String> choices, boolean other) {
        super(question, QuestionTypeEnum.MULTIPLE_CHOICE, choices, other);
    }

    public int getAllowedSelectionNum() {
        return allowedSelectionNum;
    }

    public MultipleChoiceQuestion setAllowedSelectionNum(int allowedSelectionNum) {
        this.allowedSelectionNum = allowedSelectionNum;
        return this;
    }

    @Override
    public Map<String, Integer> getAnswerDistribution() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        for (String answer : getResponses()) {
            distribution.put(answer, distribution.getOrDefault(answer, 0) + 1);
        }
        return distribution;
    }
}
