package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleChoiceQuestion extends ChoiceQuestion implements AnalyzableQuestion {

    private Map<String, Integer> answerDistribution = new HashMap<>();
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
        return answerDistribution;
    }

    @Override
    public void accumulateAnswers(List<String> values) {
        for (String value : values) {
            answerDistribution.merge(value, 1, Integer::sum);
        }
    }
}
