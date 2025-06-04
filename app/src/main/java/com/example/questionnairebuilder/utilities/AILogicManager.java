package com.example.questionnairebuilder.utilities;

import com.example.questionnairebuilder.listeners.OnAnalysisCompleteListener;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.vertexai.FirebaseVertexAI;
import com.google.firebase.vertexai.GenerativeModel;
import com.google.firebase.vertexai.java.GenerativeModelFutures;
import com.google.firebase.vertexai.type.Content;
import com.google.firebase.vertexai.type.GenerateContentResponse;

import java.util.concurrent.Executors;

public class AILogicManager {
    private static AILogicManager instance;
    private GenerativeModel geminiModel;
    private GenerativeModelFutures model;

    private AILogicManager() {
        geminiModel = FirebaseVertexAI.getInstance()
                .generativeModel("gemini-2.0-flash");
        model = GenerativeModelFutures.from(geminiModel);
    }

    public static synchronized AILogicManager getInstance() {
        if (instance == null)
            instance = new AILogicManager();
        return instance;
    }

    public void analyzeOpenAnswer(String userResponse, OnAnalysisCompleteListener listener) {
        String prompt = "As an AI survey analyst, analyze the following open-ended responses written by different users. " +
                "Identify the most common themes, trends, and sentiments. " +
                "Write one short paragraph summarizing the insights in natural English, up to 50 words. " +
                "Do not use lists or quote the users directly.\n\nResponses:\n" + userResponse;


        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> future = model.generateContent(content);

        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse response) {
                String result = response.getText();
                listener.onAnalysisComplete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                listener.onError(new Exception(t));
            }
        }, Executors.newSingleThreadExecutor());
    }
}
