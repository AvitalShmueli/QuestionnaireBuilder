package com.example.questionnairebuilder;

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

import com.example.questionnairebuilder.databinding.ActivitySignUpBinding;
import com.example.questionnairebuilder.models.User;
import com.example.questionnairebuilder.utilities.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private Uri selectedImageUri;
    private MaterialToolbar toolBar;
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

        // Register user with Firebase Auth
        FirebaseManager.getInstance().registerUser(email, password, task -> {
            if (task.isSuccessful()) {
                String uid = task.getResult().getUser().getUid();

                // Upload profile image if selected
                if (selectedImageUri != null) {
                    FirebaseManager.getInstance().uploadUserProfileImage(uid, selectedImageUri, imageUrl -> {
                        saveUserToFirestore(uid, username, email, imageUrl);
                    });
                } else { // No profile image selected
                    saveUserToFirestore(uid, username, email, "");
                }
            } else {
                binding.signUpBTNRegister.setEnabled(true);

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

        FirebaseManager.getInstance().saveUser(user, success -> {
            binding.signUpBTNRegister.setEnabled(true);
            if (success) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}