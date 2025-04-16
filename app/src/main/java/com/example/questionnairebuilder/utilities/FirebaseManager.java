package com.example.questionnairebuilder.utilities;

import androidx.annotation.NonNull;

import com.example.questionnairebuilder.models.Survey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseDatabase database;
    private DatabaseReference surveysRef;
    private DatabaseReference usersRef;
    private DatabaseReference questionsRef;

    private FirebaseManager() {
        database = FirebaseDatabase.getInstance();
        surveysRef = database.getReference("Surveys");
        usersRef = database.getReference("Users");
        questionsRef = database.getReference("Questions");
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null)
            instance = new FirebaseManager();
        return instance;
    }

    public void addSurvey(Survey survey) {
        surveysRef.child(survey.getID()).setValue(survey);
    }

    public interface SurveyCallback {
        void onSurveysLoaded(List<Survey> surveys);
    }

    public void getAllSurveys(SurveyCallback callback) {
        surveysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Survey> surveys = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Survey survey = child.getValue(Survey.class);
                    if (survey != null) {
                        surveys.add(survey);
                    }
                }
                callback.onSurveysLoaded(surveys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

}
