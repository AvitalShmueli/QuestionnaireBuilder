package com.example.questionnairebuilder.interfaces;

import java.util.List;
import java.util.Map;

public interface AnalyzableQuestion {
    public Map<String, Integer> getAnswerDistribution();
    public void accumulateAnswers(List<String> values);
}
