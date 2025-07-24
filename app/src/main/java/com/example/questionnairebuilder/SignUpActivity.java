package com.example.questionnairebuilder;

import static com.example.questionnairebuilder.models.User.USERNAME;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.example.questionnairebuilder.databinding.ActivitySignUpBinding;
import com.example.questionnairebuilder.models.User;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.example.questionnairebuilder.utilities.SharedPreferencesManager;
import com.google.android.material.appbar.MaterialToolbar;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private Uri selectedImageUri;
    private MaterialToolbar toolBar;
    private ContentLoadingProgressBar progressBar;
    private View loadingOverlay;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.signUpIMGProfile.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolBar = findViewById(R.id.topAppBar);
        progressBar = findViewById(R.id.signUp_progressBar);
        loadingOverlay = findViewById(R.id.signUp_loadingOverlay);

        SharedPreferencesManager.init(this);

        initViews();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
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
        binding.signUpIMGProfile.setOnClickListener(v -> openImagePicker());
        binding.signUpBTNRegister.setOnClickListener(v -> registerUser());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void registerUser() {
        String username = binding.signUpTIETUsername.getText().toString().trim();
        String email = binding.signUpTIETEmail.getText().toString().trim();
        String password = binding.signUpTIETPassword.getText().toString().trim();

        if (username.isEmpty()) {
            binding.signUpTILUsername.setError("Username required");
            return;
        } else {
            binding.signUpTILUsername.setError(null);
        }

        if (email.isEmpty()) {
            binding.signUpTILEmail.setError("Email required");
            return;
        } else {
            binding.signUpTILEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.signUpTILPassword.setError("Password required");
            return;
        } else {
            binding.signUpTILPassword.setError(null);
        }

        binding.signUpBTNRegister.setEnabled(false);
        loadingOverlay.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        // Register user with Firebase Auth
        AuthenticationManager.getInstance().registerUser(email, password, task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();

                // Upload profile image if selected
                if (selectedImageUri != null) {
                    FirestoreManager.getInstance().uploadUserProfileImage(uid, selectedImageUri, imageUrl -> {
                        saveUserToFirestore(uid, username, email, imageUrl);
                    });
                } else { // No profile image selected
                    saveUserToFirestore(uid, username, email, "");
                }
            } else {
                binding.signUpBTNRegister.setEnabled(true);
                loadingOverlay.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                // Show error message on label
                binding.signUpLBLError.setText(task.getException().getMessage());
                binding.signUpLBLError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveUserToFirestore(String uid, String username, String email, String imageUrl) {
        User user = new User()
                .setUid(uid)
                .setUsername(username)
                .setEmail(email)
                .setProfileImageUrl(imageUrl);

        FirestoreManager.getInstance().saveUser(user, success -> {
            binding.signUpBTNRegister.setEnabled(true);
            if (success) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                SharedPreferencesManager.getInstance().putString(USERNAME, user.getUsername());
                Intent intent;
                if (getIntent().getBooleanExtra("launched_from_link", false)) {
                    intent = new Intent(this, QuestionsActivity.class);
                    intent.putExtra("surveyID", getIntent().getStringExtra("surveyID"));
                    intent.putExtra("launched_from_link", true);
                } else {
                    intent = new Intent(this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            } else {
                loadingOverlay.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                binding.signUpBTNRegister.setEnabled(true);
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
            }
        });
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