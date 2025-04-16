package com.example.questionnairebuilder;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.questionnairebuilder.databinding.ActivityQuestionResponseBinding;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.ui.response_types.ChoiceQuestionResponseFragment;
import com.example.questionnairebuilder.ui.response_types.OpenQuestionResponseFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class QuestionResponseActivity extends AppCompatActivity {
    public static final String KEY_QUESTION_HEADER = "KEY_QUESTION_HEADER";
    public static final String KEY_QUESTION_ARGS = "KEY_QUESTION_ARGS";
    private ActivityQuestionResponseBinding binding;

    private OpenQuestionResponseFragment openQuestionResponseFragment;
    private ChoiceQuestionResponseFragment choiceQuestionResponseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQuestionResponseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        QuestionTypeManager.init(this);

        Intent previousIntent = getIntent();
        String title = previousIntent.getStringExtra(KEY_QUESTION_HEADER);
        Bundle args = previousIntent.getBundleExtra(KEY_QUESTION_ARGS);
        if(args != null){
            String type = args.getString("questionType");
            QuestionTypeEnum selectedType = QuestionTypeEnum.valueOf(type);
            type = QuestionTypeManager.getValueByKey(selectedType);

            initView(title);

            if(type != null) {
                switch (selectedType) {
                    case OPEN_ENDED_QUESTION:
                        openQuestionResponseFragment = OpenQuestionResponseFragment.newInstance(args);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.questionResponse_FRAME_question,openQuestionResponseFragment)
                                .commit();
                        break;
                    case SINGLE_CHOICE:
                    case DROPDOWN:
                    case YES_NO:
                    case MULTIPLE_CHOICE:
                        choiceQuestionResponseFragment = ChoiceQuestionResponseFragment.newInstance(args);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.questionResponse_FRAME_question,choiceQuestionResponseFragment)
                                .commit();
                        break;
                    case DATE:
                        break;
                    case RATING_SCALE:
                        break;
                }
            }
        }
    }

    private void initView(String title){
        MaterialToolbar myToolbar = binding.topAppBar;
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            if (title == null)
                getSupportActionBar().setTitle(R.string.new_question);
            else
                getSupportActionBar().setTitle(title);
        }

        // listeners
        myToolbar.setNavigationOnClickListener(v -> onBack());
    }

    private void onBack(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editQuestion_FRAME_question);
        if (fragment instanceof UnsavedChangesHandler) {
            if (((UnsavedChangesHandler) fragment).hasUnsavedChanges()) {
                showCancelConfirmationDialog();
            } else {
                finish();
            }
        } else {
            finish(); // If the fragment doesn't care about unsaved changes
        }
    }

    public void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.discard_changes_title)
                .setMessage(R.string.discard_changes_msg)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}