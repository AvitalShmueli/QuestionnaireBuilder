package com.example.questionnairebuilder.utilities;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
}
