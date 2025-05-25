package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.questionnairebuilder.listeners.OnCountListener;
import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class SurveyManagementActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private LinearLayout management_LL_edit;
    private MaterialTextView management_LBL_isOpen;
    private MaterialSwitch management_SW_isOpen;
    private MaterialTextView management_LBL_totalResponses;
    private MaterialTextView management_LBL_completedResponses;
    private MaterialTextView management_LBL_createdDate;
    private MaterialTextView management_LBL_modifiedDate;
    private MaterialTextView management_LBL_dueDate;
    private MaterialTextView management_LBL_questionCount;
    //private MaterialTextView management_LBL_pageCount;
    private AppCompatSpinner management_SP_status;
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

        // Completed Responses
        FirestoreManager.getInstance().getSurveyResponseStatusCount(
                surveyID,
                Collections.singletonList(SurveyResponseStatus.ResponseStatus.COMPLETED),
                new OnCountListener() {
                    @Override
                    public void onCountSuccess(int count) {
                        management_LBL_completedResponses.setText(String.valueOf(count));
                    }
                    @Override
                    public void onCountFailure(Exception e) {
                        Log.e("Survey", "Failed to get completed responses count", e);
                    }
                }
        );

        // Total Responses
        FirestoreManager.getInstance().getSurveyResponseStatusCount(
                surveyID,
                Arrays.asList(SurveyResponseStatus.ResponseStatus.IN_PROGRESS, SurveyResponseStatus.ResponseStatus.COMPLETED),
                new OnCountListener() {
                    @Override
                    public void onCountSuccess(int count) {
                        management_LBL_totalResponses.setText(String.valueOf(count));
                    }
                    @Override
                    public void onCountFailure(Exception e) {
                        Log.e("Survey", "Failed to get total responses count", e);
                    }
                }
        );

        FirestoreManager.getInstance().getSurveyById(surveyID, new OneSurveyCallback() {
            @Override
            public void onSurveyLoaded(Survey loadedSurvey) {
                survey = loadedSurvey;

                /*
                // Total Responses
                int totalResponses = survey.getSurveyViewers() != null ? survey.getSurveyViewers().size() : 0;
                management_LBL_totalResponses.setText(String.valueOf(totalResponses));

                // Completed Responses
                management_LBL_completedResponses.setText(String.valueOf(totalResponses));
                */

                // Created Date
                if (survey.getCreated() != null) {
                    management_LBL_createdDate.setText(formatDate(survey.getCreated()));
                }

                // Modified Date
                if (survey.getModified() != null) {
                    management_LBL_modifiedDate.setText(formatDate(survey.getModified()));
                }

                // Due Date
                if (survey.getDueDate() != null) {
                    management_LBL_dueDate.setText(formatDate(survey.getDueDate()));
                }

                // Page Count
                //management_LBL_pageCount.setText("1");

                // Status Switch & Label
                boolean isPublished = survey.getStatus() == Survey.SurveyStatus.Published;
                management_SW_isOpen.setChecked(isPublished);
                updateStatusLabel(isPublished); // Already implemented
            }

            @Override
            public void onError(Exception e) {
                Log.e("pttt","Failed to load survey: " + e.getMessage());
            }
        });

        // Question Count
        FirestoreManager.getInstance().countSurveysQuestions(surveyID, new OnCountListener() {
            @Override
            public void onCountSuccess(int count) {
                //int questionCount = survey.getQuestions() != null ? survey.getQuestions().size() : 0;
                management_LBL_questionCount.setText(String.valueOf(count));
            }

            @Override
            public void onCountFailure(Exception e) {
                Log.e("pttt",e.getMessage());
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
        initIsOpenSwitch();
        setupEditClick();
        initSpinner();
    }

    private void initSpinner() {
        String[] statuses = {getString(R.string.draft), getString(R.string.published), getString(R.string.close)};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                statuses
        );
        statusAdapter.setDropDownViewResource(R.layout.spinner_item);
        management_SP_status.setAdapter(statusAdapter);
    }

    private void setupEditClick() {
        management_LL_edit.setOnClickListener(v -> {
            Intent intent = new Intent(SurveyManagementActivity.this, QuestionsActivity.class);
            intent.putExtra(QuestionsActivity.KEY_EDIT_MODE, true);
            intent.putExtra("surveyID",survey.getID());
            intent.putExtra("survey_title",survey.getSurveyTitle());
            startActivity(intent);
        });
    }

    private void initIsOpenSwitch() {

        management_SW_isOpen.setChecked(false); // Initialize current state default to Closed (unchecked)
        updateStatusLabel(false);
        management_SW_isOpen.setOnCheckedChangeListener((buttonView, isChecked) -> { // Listen for future changes
            updateStatusLabel(isChecked);
        });
    }

    private void updateStatusLabel(boolean isChecked) {
        if (isChecked) {
            management_LBL_isOpen.setText(R.string.open);
            management_LBL_isOpen.setTextColor(getColor(R.color.theme_circle_green));
        }
        else {
            management_LBL_isOpen.setText(R.string.closed);
            management_LBL_isOpen.setTextColor(getColor(R.color.theme_circle_red));
        }
    }

    private void findViews() {
        toolbar = findViewById(R.id.topAppBar);
        management_LL_edit = findViewById(R.id.management_LL_edit);
        management_LBL_isOpen = findViewById(R.id.management_LBL_isOpen);
        management_SW_isOpen = findViewById(R.id.management_SW_isOpen);
        management_LBL_totalResponses = findViewById(R.id.management_LBL_totalResponses);
        management_LBL_completedResponses = findViewById(R.id.management_LBL_completedResponses);
        management_LBL_createdDate = findViewById(R.id.management_LBL_createdDate);
        management_LBL_modifiedDate = findViewById(R.id.management_LBL_modifiedDate);
        management_LBL_dueDate = findViewById(R.id.management_LBL_dueDate);
        management_LBL_questionCount = findViewById(R.id.management_LBL_questionCount);
        //management_LBL_pageCount = findViewById(R.id.management_LBL_pageCount);
        management_SP_status = findViewById(R.id.management_SP_status);
    }
}