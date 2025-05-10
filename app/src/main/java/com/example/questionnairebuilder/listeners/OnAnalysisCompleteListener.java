package com.example.questionnairebuilder.listeners;

public interface OnAnalysisCompleteListener {
    void onAnalysisComplete(String result);
    void onError(Exception e);
}
