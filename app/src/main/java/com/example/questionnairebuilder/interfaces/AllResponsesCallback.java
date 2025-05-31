package com.example.questionnairebuilder.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface AllResponsesCallback {
    void onResponsesLoaded(List<DocumentSnapshot> documents);
    void onError(Exception e);
}
