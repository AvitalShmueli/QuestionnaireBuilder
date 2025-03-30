package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questionnairebuilder.databinding.ActivityEditQuestionBinding;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.ui.question_types.ChoiceQuestionFragment;
import com.example.questionnairebuilder.ui.question_types.DateQuestionFragment;
import com.example.questionnairebuilder.ui.question_types.OpenQuestionFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class EditQuestionActivity extends AppCompatActivity {
    public static final String KEY_TYPE = "KEY_TYPE";
    private ActivityEditQuestionBinding binding;

    private FrameLayout editQuestion_FRAME_question;
    private OpenQuestionFragment openQuestionFragment;
    private ChoiceQuestionFragment choiceQuestionFragment;
    private DateQuestionFragment dateQuestionFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        QuestionTypeManager.init(this);

        Intent previousIntent = getIntent();

        String type = previousIntent.getStringExtra(KEY_TYPE);
        QuestionTypeEnum selectedType = QuestionTypeEnum.valueOf(type);
        type = QuestionTypeManager.getValueByKey(selectedType);

        initView(type);

        if(type != null) {
            switch (selectedType) {
                case OPEN_ENDED_QUESTION:
                    openQuestionFragment = new OpenQuestionFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.editQuestion_FRAME_question,openQuestionFragment)
                            .commit();
                    break;
                case SINGLE_CHOICE:
                case DROPDOWN:
                case YES_NO:
                case MULTIPLE_CHOICE:
                    choiceQuestionFragment = ChoiceQuestionFragment.newInstance(type);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.editQuestion_FRAME_question, choiceQuestionFragment)
                            .commit();
                    break;
                case DATE:
                    dateQuestionFragment = new DateQuestionFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.editQuestion_FRAME_question,dateQuestionFragment)
                            .commit();
                    break;
            }
        }

    }

    private void initView(String title){
        editQuestion_FRAME_question = binding.editQuestionFRAMEQuestion;

        MaterialToolbar myToolbar = binding.topAppBar;
        setSupportActionBar(myToolbar);
        if(title == null)
            myToolbar.setTitle(R.string.new_question);
        else
            myToolbar.setTitle(title);

        // listeners
        myToolbar.setNavigationOnClickListener(v -> finish());
    }

    private void changeActivity(String type) {
        Intent editQuestionActivity = new Intent(this, EditQuestionActivity.class);
        editQuestionActivity.putExtra(EditQuestionActivity.KEY_TYPE,type);
        startActivity(editQuestionActivity);
    }
}