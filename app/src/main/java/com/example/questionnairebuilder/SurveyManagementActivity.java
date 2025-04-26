package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.utilities.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SurveyManagementActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private LinearLayout management_LL_edit;
    private MaterialTextView management_LBL_status;
    private MaterialSwitch management_SW_status;
    private MaterialTextView management_LBL_totalResponses;
    private MaterialTextView management_LBL_completedResponses;
    private MaterialTextView management_LBL_createdDate;
    private MaterialTextView management_LBL_modifiedDate;
    private MaterialTextView management_LBL_questionCount;
    private MaterialTextView management_LBL_pageCount;
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

                // Total Responses
                int totalResponses = survey.getSurveyViewers() != null ? survey.getSurveyViewers().size() : 0;
                management_LBL_totalResponses.setText(String.valueOf(totalResponses));

                // Completed Responses
                management_LBL_completedResponses.setText(String.valueOf(totalResponses));

                // Created Date
                if (survey.getCreated() != null) {
                    management_LBL_createdDate.setText(formatDate(survey.getCreated()));
                }

                // Modified Date
                if (survey.getModified() != null) {
                    management_LBL_modifiedDate.setText(formatDate(survey.getModified()));
                }

                // Question Count
                int questionCount = survey.getQuestions() != null ? survey.getQuestions().size() : 0;
                management_LBL_questionCount.setText(String.valueOf(questionCount));

                // Page Count
                management_LBL_pageCount.setText("1");

                // Status Switch & Label
                boolean isPublished = survey.getStatus() == Survey.SurveyStatus.Published;
                management_SW_status.setChecked(isPublished);
                updateStatusLabel(isPublished); // Already implemented
            }

            @Override
            public void onError(Exception e) {
                Log.e("pttt","Failed to load survey: " + e.getMessage());
            }
        });
    }

    @NonNull
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        initStatusSwitch();
        setupEditClick();
    }

    private void setupEditClick() {
        management_LL_edit.setOnClickListener(v -> {
            Intent intent = new Intent(SurveyManagementActivity.this, QuestionsActivity.class);
            intent.putExtra("surveyID",survey.getID());
            startActivity(intent);
        });
    }

    private void initStatusSwitch() {

        management_SW_status.setChecked(false); // Initialize current state default to Closed (unchecked)
        updateStatusLabel(false);
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
        management_LBL_totalResponses = findViewById(R.id.management_LBL_totalResponses);
        management_LBL_completedResponses = findViewById(R.id.management_LBL_completedResponses);
        management_LBL_createdDate = findViewById(R.id.management_LBL_createdDate);
        management_LBL_modifiedDate = findViewById(R.id.management_LBL_modifiedDate);
        management_LBL_questionCount = findViewById(R.id.management_LBL_questionCount);
        management_LBL_pageCount = findViewById(R.id.management_LBL_pageCount);
    }
}