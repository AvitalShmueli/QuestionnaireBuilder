package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SingleChoiceQuestion extends ChoiceQuestion implements AnalyzableQuestion {

    public SingleChoiceQuestion() {
        super();
    }

    public SingleChoiceQuestion(String question, QuestionTypeEnum type) {
        super(question, type);
    }

    public SingleChoiceQuestion(String question, QuestionTypeEnum type, ArrayList<String> choices, boolean other) {
        super(question, type, choices, other);
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
