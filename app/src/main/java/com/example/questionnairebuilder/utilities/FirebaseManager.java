package com.example.questionnairebuilder.utilities;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private FirebaseAuth auth;
    private DatabaseReference surveysRef;
    private DatabaseReference usersRef;
    private DatabaseReference questionsRef;

    private FirebaseManager() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        surveysRef = database.getReference("Surveys");
        usersRef = database.getReference("Users");
        questionsRef = database.getReference("Questions");
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null)
            instance = new FirebaseManager();
        return instance;
    }

    // =================== DATABASE ===================

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

    // =================== AUTH ===================

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void logout() {
        auth.signOut();
    }

    // =================== SIGN-UP ===================

    public void uploadUserProfileImage(String uid, Uri imageUri, OnImageUploadListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("ProfileImages").child(uid + ".jpg");
        storageRef.putFile(imageUri)
                .continueWithTask(task -> storageRef.getDownloadUrl())
                .addOnSuccessListener(uri -> listener.onUploaded(uri.toString()));
    }

    public void saveUser(User user, OnUserSaveListener listener) {
        usersRef.child(user.getUid()).setValue(user)
                .addOnCompleteListener(task -> listener.onSaved(task.isSuccessful()));
    }

    public interface OnImageUploadListener {
        void onUploaded(String imageUrl);
    }

    public interface OnUserSaveListener {
        void onSaved(boolean success);
    }

}
