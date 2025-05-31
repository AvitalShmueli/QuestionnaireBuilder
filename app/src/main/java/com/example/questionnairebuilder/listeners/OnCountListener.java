package com.example.questionnairebuilder.listeners;

public interface OnCountListener {
    void onCountSuccess(int count);
    void onCountFailure(Exception e);
}
