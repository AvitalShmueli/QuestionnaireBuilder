package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleChoiceQuestion extends ChoiceQuestion implements AnalyzableQuestion {
    private int allowedSelectionNum;
    private Map<String, Integer> answerDistribution = new HashMap<>();

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
        return answerDistribution;
    }

    @Override
    public void accumulateAnswers(List<String> values) {
        for (String value : values) {
            answerDistribution.merge(value, 1, Integer::sum);
        }
    }
}
