package com.example.questionnairebuilder.utilities;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.interfaces.SurveysCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseFirestore database;
    private FirebaseAuth auth;
    private CollectionReference surveysRef;
    private CollectionReference usersRef;
    private CollectionReference questionsRef;

    private FirebaseManager() {
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        surveysRef = database.collection("Surveys");
        usersRef = database.collection("Users");
        questionsRef = database.collection("Questions");
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null)
            instance = new FirebaseManager();
        return instance;
    }

    // =================== DATABASE ===================

    public void addSurvey(Survey survey) {
        surveysRef.document(survey.getID()).set(survey);
    }

    public ListenerRegistration listenToAllSurveys(SurveysCallback callback) {
        return surveysRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    callback.onError(e);
                    return;
                }

                List<Survey> surveyList = new ArrayList<>();
                if (snapshots != null) {
                    for (DocumentSnapshot document : snapshots) {
                        Survey survey = document.toObject(Survey.class);
                        if (survey != null) {
                            survey.setID(document.getId());
                            surveyList.add(survey);
                        }
                    }
                }
                callback.onSurveysLoaded(surveyList);
            }
        });
    }

    public ListenerRegistration listenToMySurveys(String currentUserId, SurveysCallback callback) {
        return surveysRef.whereEqualTo("author.uid", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError(error);
                        return;
                    }

                    if (value != null) {
                        List<Survey> surveyList = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Survey survey = document.toObject(Survey.class);
                            if (survey != null) {
                                survey.setID(document.getId());
                                surveyList.add(survey);
                            }
                        }
                        callback.onSurveysLoaded(surveyList);
                    }
                });
    }

    public void getSurveyById(String surveyId, OneSurveyCallback callback) {
        surveysRef.document(surveyId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Survey survey = documentSnapshot.toObject(Survey.class);
                        if (survey != null) {
                            survey.setID(documentSnapshot.getId()); // set ID manually
                            callback.onSurveyLoaded(survey);
                        } else {
                            callback.onError(new Exception("Survey is null"));
                        }
                    } else {
                        callback.onError(new Exception("Survey not found"));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e));
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

    public void uploadUserProfileImage(String uid, Uri imageUri, OnImageUploadListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("ProfileImages").child(uid + ".jpg");
        storageRef.putFile(imageUri)
                .continueWithTask(task -> storageRef.getDownloadUrl())
                .addOnSuccessListener(uri -> listener.onUploaded(uri.toString()));
    }

    public interface OnImageUploadListener {
        void onUploaded(String imageUrl);
    }

    public interface OnUserSaveListener {
        void onSaved(boolean success);
    }

    public void saveUser(User user, OnUserSaveListener listener) {
        database.collection("Users")
                .document(user.getUid())
                .set(user)
                .addOnCompleteListener(task -> listener.onSaved(task.isSuccessful()));
    }

    public void getUserData(String uid, OnUserFetchListener listener) {
        database.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onFetched(user);
                    } else {
                        listener.onFetched(null);
                    }
                })
                .addOnFailureListener(e -> listener.onFetched(null));
    }

    public interface OnUserFetchListener {
        void onFetched(User user);
    }

}
