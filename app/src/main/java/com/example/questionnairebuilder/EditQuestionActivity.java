package com.example.questionnairebuilder;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.questionnairebuilder.databinding.ActivityEditQuestionBinding;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.ui.question_types.ChoiceQuestionFragment;
import com.example.questionnairebuilder.ui.question_types.DateQuestionFragment;
import com.example.questionnairebuilder.ui.question_types.OpenQuestionFragment;
import com.example.questionnairebuilder.ui.question_types.RatingQuestionFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class EditQuestionActivity extends AppCompatActivity {
    public static final String KEY_TYPE = "KEY_TYPE";
    static final String KEY_QUESTION_HEADER = "KEY_QUESTION_HEADER";
    public static final String KEY_QUESTION_ARGS = "KEY_QUESTION_ARGS";
    private ActivityEditQuestionBinding binding;

    private OpenQuestionFragment openQuestionFragment;
    private ChoiceQuestionFragment choiceQuestionFragment;
    private DateQuestionFragment dateQuestionFragment;
    private RatingQuestionFragment ratingQuestionFragment;

    private String surveyID;
    private String type;
    private QuestionTypeEnum selectedType;
    private int currentQuestionOrder = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            QuestionTypeManager.init(this);

            Intent previousIntent = getIntent();

            String title = previousIntent.getStringExtra(KEY_QUESTION_HEADER);
            Bundle args = previousIntent.getBundleExtra(KEY_QUESTION_ARGS);
            if (args != null) {
                type = args.getString("questionType");
                selectedType = QuestionTypeEnum.valueOf(type);
                type = QuestionTypeManager.getValueByKey(selectedType);

                currentQuestionOrder = args.getInt("order");
                surveyID = args.getString("surveyID");

                initView(title);
            }
            else{
                surveyID = getIntent().getStringExtra("surveyID");

                type = previousIntent.getStringExtra(KEY_TYPE);
                selectedType = QuestionTypeEnum.valueOf(type);
                type = QuestionTypeManager.getValueByKey(selectedType);

                initView(type);
            }
            if (type != null) {
                loadQuestionFragment(selectedType, args);
            }
        }


        //QuestionTypeManager.init(this);

        //Intent previousIntent = getIntent();
        //surveyID = getIntent().getStringExtra("surveyID");

        //type = previousIntent.getStringExtra(KEY_TYPE);
        //selectedType = QuestionTypeEnum.valueOf(type);
        //type = QuestionTypeManager.getValueByKey(selectedType);

        //initView(type);

        /*if(type != null) {
            switch (selectedType) {
                case OPEN_ENDED_QUESTION:
                    openQuestionFragment = OpenQuestionFragment.newInstance(surveyID);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.editQuestion_FRAME_question,openQuestionFragment)
                            .commit();
                    break;
                case SINGLE_CHOICE:
                case DROPDOWN:
                case YES_NO:
                case MULTIPLE_CHOICE:
                    choiceQuestionFragment = ChoiceQuestionFragment.newInstance(surveyID, type);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.editQuestion_FRAME_question, choiceQuestionFragment)
                            .commit();
                    break;
                case DATE:
                    dateQuestionFragment = DateQuestionFragment.newInstance(surveyID);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.editQuestion_FRAME_question,dateQuestionFragment)
                            .commit();
                    break;
                case RATING_SCALE:
                    ratingQuestionFragment = RatingQuestionFragment.newInstance(surveyID);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.editQuestion_FRAME_question,ratingQuestionFragment)
                            .commit();
                    break;

            }
        }*/

    }

    private void initView(String title){
        MaterialToolbar myToolbar = binding.topAppBar;
        setSupportActionBar(myToolbar);
        if(title == null)
            myToolbar.setTitle(R.string.new_question);
        else
            myToolbar.setTitle(title);

        // listeners
        myToolbar.setNavigationOnClickListener(v -> onBack());

    }

    private void loadQuestionFragment(QuestionTypeEnum selectedType, Bundle args){
        switch (selectedType) {
            case OPEN_ENDED_QUESTION:
                openQuestionFragment = OpenQuestionFragment.newInstance(surveyID);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.editQuestion_FRAME_question,openQuestionFragment)
                        .commit();
                break;
            case SINGLE_CHOICE:
            case DROPDOWN:
            case YES_NO:
            case MULTIPLE_CHOICE:
                if(args == null)
                    choiceQuestionFragment = ChoiceQuestionFragment.newInstance(surveyID, type);
                else choiceQuestionFragment = ChoiceQuestionFragment.newInstance(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.editQuestion_FRAME_question, choiceQuestionFragment)
                        .commit();
                break;
            case DATE:
                dateQuestionFragment = DateQuestionFragment.newInstance(surveyID);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.editQuestion_FRAME_question,dateQuestionFragment)
                        .commit();
                break;
            case RATING_SCALE:
                ratingQuestionFragment = RatingQuestionFragment.newInstance(surveyID);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.editQuestion_FRAME_question,ratingQuestionFragment)
                        .commit();
                break;

        }
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


    private void changeActivity(String type) {
        Intent editQuestionActivity = new Intent(this, EditQuestionActivity.class);
        editQuestionActivity.putExtra(EditQuestionActivity.KEY_TYPE,type);
        startActivity(editQuestionActivity);
    }
}