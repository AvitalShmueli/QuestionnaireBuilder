package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questionnairebuilder.databinding.ActivityEditQuestionBinding;
import com.google.android.material.textview.MaterialTextView;

public class EditQuestionActivity extends AppCompatActivity {
    public static final String KEY_TYPE = "KEY_TYPE";
    private ActivityEditQuestionBinding binding;
    private MaterialTextView editQuestion_LBL_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent previousIntent = getIntent();
        String type = previousIntent.getStringExtra(KEY_TYPE);

        editQuestion_LBL_type = binding.editQuestionLBLType;
        editQuestion_LBL_type.setText(type);

    }
}