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

public class VertexAiManager {
    private static VertexAiManager instance;
    private GenerativeModel geminiModel;
    private GenerativeModelFutures model;

    private VertexAiManager() {
        geminiModel = FirebaseVertexAI.getInstance()
                .generativeModel("gemini-2.0-flash");
        model = GenerativeModelFutures.from(geminiModel);
    }

    public static synchronized VertexAiManager getInstance() {
        if (instance == null)
            instance = new VertexAiManager();
        return instance;
    }

    public void analyzeOpenAnswer(String userResponse, OnAnalysisCompleteListener listener) {
        //TODO: Dummy data for now — later this would fetch actual responses from Firestore.
        String combinedText = "User response 1.\nUser response 2.\nUser response 3.";

        //String prompt = "Analyze the following survey response and summarize the user's sentiment and suggestions:\n\n" + userResponse;
        String prompt = "Analyze the following survey responses:\n\n" + combinedText;

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
