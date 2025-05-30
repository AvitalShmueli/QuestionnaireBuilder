package com.example.questionnairebuilder.models;

import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateQuestion extends Question implements AnalyzableQuestion {
    private DateSelectionModeEnum dateMode;
    private final Map<String, Integer> dateDistribution = new HashMap<>();

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

    @Override
    public Map<String, Integer> getAnswerDistribution() {
        return dateDistribution;
    }

    @Override
    public void accumulateAnswers(List<String> values) {
        if (dateMode == DateSelectionModeEnum.DATE_RANGE) {
            for (int i = 0; i < values.size(); i += 2) {
                String from = values.get(i).trim();
                String to = (i + 1 < values.size()) ? values.get(i + 1).trim() : "";
                String key = from + " to " + to;
                dateDistribution.merge(key, 1, Integer::sum);
            }
        } else {
            for (String val : values) {
                String key = val.trim();
                dateDistribution.merge(key, 1, Integer::sum);
            }
        }
    }
}
