package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questionnairebuilder.databinding.ActivityEditQuestionBinding;
import com.example.questionnairebuilder.ui.question_types.ChoiceQuestionFragment;
import com.example.questionnairebuilder.ui.question_types.OpenQuestionFragment;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class EditQuestionActivity extends AppCompatActivity {
    public static final String KEY_TYPE = "KEY_TYPE";
    private ActivityEditQuestionBinding binding;
    private MaterialTextView editQuestion_LBL_type;

    private FrameLayout editQuestion_FRAME_question;
    private OpenQuestionFragment openQuestionFragment;
    private ChoiceQuestionFragment choiceQuestionFragment;



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

        initView();
        editQuestion_LBL_type.setText(type);

        if(type != null) {
            switch (type) {
                case "Open question":
                    openQuestionFragment = new OpenQuestionFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.editQuestion_FRAME_question,openQuestionFragment).commit();
                    break;
                case "Single choice":
                case "Multiple choice":
                    choiceQuestionFragment = new ChoiceQuestionFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.editQuestion_FRAME_question,choiceQuestionFragment).commit();
                    break;
            }
        }



    }

    private void initView(){
        editQuestion_LBL_type = binding.editQuestionLBLType;
        editQuestion_FRAME_question = binding.editQuestionFRAMEQuestion;


        Toolbar myToolbar = binding.editQuestionToolbar;
        MaterialTextView toolbar_LBL_title = binding.toolbarLBLTitle;
        setSupportActionBar(myToolbar);
        toolbar_LBL_title.setText(R.string.new_question);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // listeners
        myToolbar.setNavigationOnClickListener(v -> finish());
    }

    private void changeActivity(String type) {
        Intent editQuestionActivity = new Intent(this, EditQuestionActivity.class);
        editQuestionActivity.putExtra(EditQuestionActivity.KEY_TYPE,type);
        startActivity(editQuestionActivity);
        //requireActivity().finish();
    }
}