package com.example.questionnairebuilder.utilities;

import android.net.Uri;

import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private FirebaseManager() {
        // database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null)
            instance = new FirebaseManager();
        return instance;
    }

    public interface SurveyCallback {
        void onSurveysLoaded(List<Survey> surveys);
    }

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

    public void addSurvey(Survey survey) {
    }

    public void getAllSurveys(SurveyCallback callback) {
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
        firestore.collection("Users")
                .document(user.getUid())
                .set(user)
                .addOnCompleteListener(task -> listener.onSaved(task.isSuccessful()));
    }

    public void getUserData(String uid, OnUserFetchListener listener) {
        firestore.collection("Users")
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
