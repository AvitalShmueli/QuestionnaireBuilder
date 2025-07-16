package com.example.questionnairebuilder.utilities;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class AuthenticationManager {

    private static AuthenticationManager instance;
    private FirebaseAuth auth;

    private AuthenticationManager() {
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthenticationManager getInstance() {
        if (instance == null)
            instance = new AuthenticationManager();
        return instance;
    }

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GrafanaLogger.info("FirestoreManager", "User registered: " + email);
                        listener.onComplete(task);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            JSONObject errLog = new JSONObject();
                            errLog.put("email", email);
                            errLog.put("error", e.getMessage());
                            GrafanaLogger.error("FirestoreManager", "Failed to register user", errLog);
                        } catch (Exception ex) {
                            GrafanaLogger.error("FirestoreManager", "Failed to log error JSON");
                        }
                    }
                });
    }

    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        GrafanaLogger.info("FirestoreManager", "User logged in: " + email);
                        listener.onComplete(task);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            JSONObject errLog = new JSONObject();
                            errLog.put("email", email);
                            errLog.put("error", e.getMessage());
                            GrafanaLogger.error("FirestoreManager", "Failed to log in user", errLog);
                        } catch (Exception ex) {
                            GrafanaLogger.error("FirestoreManager", "Failed to log error JSON");
                        }
                    }
                });
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void logout() {
        auth.signOut();
    }
}
