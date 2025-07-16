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

import org.json.JSONObject;

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

    public void analyzeOpenAnswer(String questionText, String usersResponses, OnAnalysisCompleteListener listener) {
        String prompt = "You are an AI analyst for surveys. Analyze the following open-ended responses to the question below. " +
                "Your task is to identify shared themes, overall sentiment, and key trends in how users responded. " +
                "Summarize the findings in natural English in one short paragraph (maximum 50 words). " +
                "Avoid quoting users or inventing responses. Only use the responses provided.\n\n" +
                "Question:\n" + questionText + "\n\n" +
                "Responses:\n" + usersResponses;

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> future = model.generateContent(content);

        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse response) {
                String result = response.getText();
                try {
                    JSONObject details = new JSONObject();
                    details.put("question_text", questionText);
                    details.put("analyzed_result", result);
                    GrafanaLogger.info("AILogicManager", "Analyzed open question responses", details);
                } catch (Exception e) {
                    GrafanaLogger.error("AILogicManager", "Failed to log success JSON");
                }
                listener.onAnalysisComplete(result);
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    JSONObject errLog = new JSONObject();
                    errLog.put("question_text", questionText);
                    errLog.put("error", t.getMessage());
                    GrafanaLogger.error("AILogicManager", "Failed to analyzed open question responses", errLog);
                } catch (Exception ex) {
                    GrafanaLogger.error("AILogicManager", "Failed to log error JSON");
                }
                listener.onError(new Exception(t));
            }
        }, Executors.newSingleThreadExecutor());
    }
}
