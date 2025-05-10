package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.questionnairebuilder.databinding.ActivityLoginBinding;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private MaterialToolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolBar = findViewById(R.id.topAppBar);

        initViews();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initViews() {
        toolBar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        binding.loginLBLToSignup.setOnClickListener(v -> { // Go to SignUpActivity
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        binding.loginBTNLogin.setOnClickListener(v -> attemptLogin()); // Login button click

        binding.loginLBLToSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        String email = binding.loginTIETEmail.getText().toString().trim();
        String password = binding.loginTIETPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.loginTILEmail.setError("Email is required");
            return;
        }
        else
            binding.loginTILEmail.setError(null);

        if (password.isEmpty()) {
            binding.loginTILPassword.setError("Password is required");
            return;
        }
        else
            binding.loginTILPassword.setError(null);

        binding.loginBTNLogin.setEnabled(false);
        AuthenticationManager.getInstance().loginUser(email, password, this::onLoginComplete);
    }

    private void onLoginComplete(Task<AuthResult> task) {
        binding.loginBTNLogin.setEnabled(true);
        if (task.isSuccessful()) {
            String uid = task.getResult().getUser().getUid();

            FirestoreManager.getInstance().getUserData(uid, user -> {
                if (user != null) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("username", user.getUsername()); // Pass username
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
        if (ev.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            android.view.View v = getCurrentFocus();
            if (v instanceof android.widget.EditText) {
                android.graphics.Rect outRect = new android.graphics.Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    android.view.inputmethod.InputMethodManager imm =
                            (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}