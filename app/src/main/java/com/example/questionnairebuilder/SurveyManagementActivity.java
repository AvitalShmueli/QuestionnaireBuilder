package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.utilities.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textview.MaterialTextView;

public class SurveyManagementActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private LinearLayout management_LL_edit;
    private MaterialTextView management_LBL_status;
    private MaterialSwitch management_SW_status;
    private Survey survey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_management);

        findViews();
        initViews();

        getAllSurveyData();
    }

    private void getAllSurveyData(){
        String surveyID = getIntent().getStringExtra("surveyID");
        String title = getIntent().getStringExtra("survey_title");
        if (title != null) {
            toolbar.setTitle(title);
        }

        FirebaseManager.getInstance().getSurveyById(surveyID, new OneSurveyCallback() {
            @Override
            public void onSurveyLoaded(Survey loadedSurvey) {
                survey = loadedSurvey;
                // TODO: set labels based on the survey's data
            }

            @Override
            public void onError(Exception e) {
                Log.e("pttt","Failed to load survey: " + e.getMessage());
            }
        });

    }

    private void initViews() {
        setSupportActionBar(toolbar);
        initStatusSwitch();
        setupEditClick();
    }

    private void setupEditClick() {
        management_LL_edit.setOnClickListener(v -> {
            Intent intent = new Intent(SurveyManagementActivity.this, QuestionsActivity.class);
            intent.putExtra("surveyID",survey.getID());
            intent.putExtra("survey_title",survey.getSurveyTitle());
            startActivity(intent);
        });
    }

    private void initStatusSwitch() {
        updateStatusLabel(management_SW_status.isChecked()); // Initialize current state
        management_SW_status.setOnCheckedChangeListener((buttonView, isChecked) -> { // Listen for future changes
            updateStatusLabel(isChecked);
        });
    }

    private void updateStatusLabel(boolean isChecked) {
        if (isChecked) {
            management_LBL_status.setText(R.string.open);
            management_LBL_status.setTextColor(getColor(R.color.theme_circle_green));
        }
        else {
            management_LBL_status.setText(R.string.closed);
            management_LBL_status.setTextColor(getColor(R.color.theme_circle_red));
        }
    }

    private void findViews() {
        toolbar = findViewById(R.id.topAppBar);
        management_LL_edit = findViewById(R.id.management_LL_edit);
        management_LBL_status = findViewById(R.id.management_LBL_status);
        management_SW_status = findViewById(R.id.management_SW_status);
    }
}