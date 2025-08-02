package com.example.questionnairebuilder;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import com.example.questionnairebuilder.interfaces.UpdateSurveyDetailsCallback;
import com.example.questionnairebuilder.listeners.OnCountListener;
import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class SurveyManagementActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private Menu mMenu;
    private LinearLayout management_LL_edit;
    private LinearLayout management_LL_analyze;
    private LinearLayout management_LL_share;
    private MaterialTextView management_LBL_totalResponses;
    private MaterialTextView management_LBL_completedResponses;
    private MaterialTextView management_LBL_createdDate;
    private MaterialTextView management_LBL_modifiedDate;
    private LinearLayout management_LL_dueDate;
    private MaterialTextView management_LBL_dueDate;
    private MaterialTextView management_LBL_questionCount;
    private AppCompatSpinner management_SP_status;
    private MaterialSwitch management_SW_alert;
    private LinearLayout management_LL_description;
    private MaterialTextView management_LBL_description;
    private LinearLayout management_LL_title;
    private MaterialTextView management_LBL_title;
    private final Map<Survey.SurveyStatus, String> statusesMap = new LinkedHashMap<>();
    private ArrayAdapter<String> statusAdapter;
    private boolean isFirstSelection = true;
    private String surveyID;
    private Survey survey;
    private int totalResponseCount = 0;
    private int totalCompletedResponseCount = 0;
    Map<String, Object> updates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_management);

        surveyID = getIntent().getStringExtra("surveyID");

        findViews();
        initViews();

        getAllSurveyData();
    }

    private void getAllSurveyData() {
        String title = getIntent().getStringExtra("survey_title");
        if (title != null) {
            toolbar.setTitle(title);
        }

        getCompletedResponsesCounter();
        getTotalResponsesCounter();
        getQuestionsCount();
        FirestoreManager.getInstance().getSurveyById(surveyID, new OneSurveyCallback() {
            @Override
            public void onSurveyLoaded(Survey loadedSurvey) {
                survey = loadedSurvey;

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

                if (survey.getStatus() != null) {
                    int position = statusAdapter.getPosition(statusesMap.get(survey.getStatus()));
                    management_SP_status.setSelection(position);
                }

                management_SW_alert.setChecked(survey.isNewResponseAlert());

                management_LBL_description.setText(survey.getDescription());
                management_LBL_title.setText(survey.getSurveyTitle());
            }

            @Override
            public void onError(Exception e) {
                Log.e("pttt", "Failed to load survey: " + e.getMessage());
            }
        });
    }

    private void getQuestionsCount() {
        FirestoreManager.getInstance().countSurveysQuestions(surveyID, new OnCountListener() {
            @Override
            public void onCountSuccess(int count) {
                //int questionCount = survey.getQuestions() != null ? survey.getQuestions().size() : 0;
                management_LBL_questionCount.setText(String.valueOf(count));
            }

            @Override
            public void onCountFailure(Exception e) {
                Log.e("pttt", e.getMessage());
            }
        });
    }

    private void getTotalResponsesCounter() {
        FirestoreManager.getInstance().getSurveyResponseStatusCount(
                surveyID,
                Arrays.asList(SurveyResponseStatus.ResponseStatus.IN_PROGRESS, SurveyResponseStatus.ResponseStatus.COMPLETED),
                new OnCountListener() {
                    @Override
                    public void onCountSuccess(int count) {
                        totalResponseCount = count;
                        management_LBL_totalResponses.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCountFailure(Exception e) {
                        Log.e("Survey", "Failed to get total responses count", e);
                    }
                }
        );
    }

    private void getCompletedResponsesCounter() {
        FirestoreManager.getInstance().getSurveyResponseStatusCount(
                surveyID,
                Collections.singletonList(SurveyResponseStatus.ResponseStatus.COMPLETED),
                new OnCountListener() {
                    @Override
                    public void onCountSuccess(int count) {
                        totalCompletedResponseCount = count;
                        management_LBL_completedResponses.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCountFailure(Exception e) {
                        Log.e("Survey", "Failed to get completed responses count", e);
                    }
                }
        );
    }

    @NonNull
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        });
        setupEditClick();
        setupAnalyzeClick();
        setUpShareClick();
        initSpinner();

        management_SW_alert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (survey != null && isChecked != survey.isNewResponseAlert()) {
                    updates.put("newResponseAlert", isChecked);
                    if (mMenu != null) {
                        MenuItem saveMenuItem = mMenu.findItem(R.id.action_save);
                        if (saveMenuItem != null) {
                            saveMenuItem.setVisible(true);
                        }
                    }

                }
            }
        });

        management_LL_dueDate.setOnClickListener(v -> showMaterialDatePicker());
        management_LL_description.setOnClickListener(v -> showDescriptionDialog());
        management_LL_title.setOnClickListener(v -> showTitleDialog());

        management_LBL_totalResponses.setText(String.valueOf(totalResponseCount));
        management_LBL_completedResponses.setText(String.valueOf(totalCompletedResponseCount));
    }

    private void setUpShareClick() {
        management_LL_share.setOnClickListener(v -> {
            if (survey.getStatus() == Survey.SurveyStatus.Published) {
                String surveyId = survey.getID();
                String surveyLink = "https://questionnairebuilder-7b883.web.app/survey?id=" + surveyId;

                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_share_survey, null);
                ShapeableImageView qrImageView = dialogView.findViewById(R.id.share_qr_image);
                MaterialTextView linkTextView = dialogView.findViewById(R.id.share_link_text);
                AppCompatImageButton copyButton = dialogView.findViewById(R.id.share_copy_button);
                linkTextView.setText(survey.getSurveyTitle());
                linkTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
                linkTextView.setPaintFlags(linkTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                linkTextView.setOnClickListener(v2 -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(surveyLink));
                    startActivity(browserIntent);
                });

                // Handle copy button click
                copyButton.setOnClickListener(v1 -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Survey Link", surveyLink);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Link copied to clipboard", LENGTH_SHORT).show();
                });

                // Generate QR Code
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(surveyLink, BarcodeFormat.QR_CODE, 400, 400);
                    qrImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to generate QR code", LENGTH_SHORT).show();
                    return;
                }

                // Build dialog
                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setCancelable(true)
                        .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "Survey not shared. Set status to 'Published' and click 'Save'.", LENGTH_LONG).show();
            }
        });
    }


    private void setupAnalyzeClick() {
        management_LL_analyze.setOnClickListener(v -> {
            Intent intent = new Intent(SurveyManagementActivity.this, AnalyzeResponsesActivity.class);
            intent.putExtra("surveyID", survey.getID());
            startActivity(intent);
        });
    }

    private void initSpinner() {
        statusesMap.put(Survey.SurveyStatus.Draft, getString(R.string.draft));
        statusesMap.put(Survey.SurveyStatus.Published, getString(R.string.published));
        statusesMap.put(Survey.SurveyStatus.Close, getString(R.string.close));

        statusAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                statusesMap.values().toArray(new String[0])
        );
        statusAdapter.setDropDownViewResource(R.layout.item_spinner);
        management_SP_status.setAdapter(statusAdapter);

        management_SP_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    // Ignore the first trigger
                    isFirstSelection = false;
                    return;
                }

                String selectedStatus = (String) parent.getItemAtPosition(position);
                if (!selectedStatus.equals(survey.getStatus().name())) {
                    Log.d("pttt Spinner", "Selected status: " + selectedStatus);

                    // Map back to enum
                    Survey.SurveyStatus selectedEnum = null;
                    for (Map.Entry<Survey.SurveyStatus, String> entry : statusesMap.entrySet()) {
                        if (entry.getValue().equals(selectedStatus)) {
                            selectedEnum = entry.getKey();
                            break;
                        }
                    }
                    Log.d("pttt Spinner", "Selected enum: " + selectedEnum);
                    if (selectedEnum != null) {
                        updates.put("status", selectedEnum);
                        if (mMenu != null) {
                            MenuItem saveMenuItem = mMenu.findItem(R.id.action_save);
                            if (saveMenuItem != null) {
                                saveMenuItem.setVisible(true); // or true
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle no selection
            }
        });

    }

    private void setupEditClick() {
        management_LL_edit.setOnClickListener(v -> {
            if (totalResponseCount > 0)
                showEditWarningDialog();
            else
                navigateToEditScreen();
        });
    }

    private void navigateToEditScreen() {
        Intent intent = new Intent(SurveyManagementActivity.this, QuestionsActivity.class);
        intent.putExtra(QuestionsActivity.KEY_EDIT_MODE, true);
        intent.putExtra("surveyID", survey.getID());
        intent.putExtra("survey_title", survey.getSurveyTitle());
        startActivity(intent);
    }

    private void findViews() {
        toolbar = findViewById(R.id.topAppBar);
        management_LL_edit = findViewById(R.id.management_LL_edit);
        management_LL_analyze = findViewById(R.id.management_LL_analyze);
        management_LL_share = findViewById(R.id.management_LL_share);
        management_LBL_totalResponses = findViewById(R.id.management_LBL_totalResponses);
        management_LBL_completedResponses = findViewById(R.id.management_LBL_completedResponses);
        management_LBL_createdDate = findViewById(R.id.management_LBL_createdDate);
        management_LBL_modifiedDate = findViewById(R.id.management_LBL_modifiedDate);
        management_LL_dueDate = findViewById(R.id.management_LL_dueDate);
        management_LBL_dueDate = findViewById(R.id.management_LBL_dueDate);
        management_LBL_questionCount = findViewById(R.id.management_LBL_questionCount);
        management_SP_status = findViewById(R.id.management_SP_status);
        management_SW_alert = findViewById(R.id.management_SW_alert);
        management_LL_description = findViewById(R.id.management_LL_description);
        management_LBL_description = findViewById(R.id.management_LBL_description);
        management_LL_title = findViewById(R.id.management_LL_title);
        management_LBL_title = findViewById(R.id.management_LBL_title);
    }

    private void showMaterialDatePicker() {
        Date selectedDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            selectedDate = sdf.parse(management_LBL_dueDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date");

        if (selectedDate != null) {
            Calendar localCal = Calendar.getInstance();
            localCal.setTime(selectedDate);
            localCal.set(Calendar.HOUR_OF_DAY, 0);
            localCal.set(Calendar.MINUTE, 0);
            localCal.set(Calendar.SECOND, 0);
            localCal.set(Calendar.MILLISECOND, 0);

            // Convert local midnight to UTC midnight
            Log.d("DatePickerHelper", "Timezone offset: " + localCal.getTimeZone().getOffset(localCal.getTimeInMillis()) / 3600000 );
            long utcMillis = localCal.getTimeInMillis() + localCal.getTimeZone().getOffset(localCal.getTimeInMillis());
            builder.setSelection(utcMillis);
        } else {
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        }

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());
        builder.setCalendarConstraints(constraintsBuilder.build());

        final MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year);
            Date dueDate;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                dueDate = sdf.parse(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            if (dueDate != null && survey != null && !dueDate.equals(survey.getDueDate())) {
                updates.put("dueDate", dueDate);
                management_LBL_dueDate.setText(formattedDate);
                if (mMenu != null) {
                    MenuItem saveMenuItem = mMenu.findItem(R.id.action_save);
                    if (saveMenuItem != null) {
                        saveMenuItem.setVisible(true); // or true
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_save, menu);
        mMenu = menu;

        MenuItem saveMenuItem = mMenu.findItem(R.id.action_save);
        if (saveMenuItem != null) {
            saveMenuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (!updates.isEmpty()) {
                updates.put("modified", new Timestamp(new Date()));
                FirestoreManager.getInstance().updateSurvey(survey.getID(), updates, new UpdateSurveyDetailsCallback() {
                    @Override
                    public void onSuccess(Survey updatedSurvey) {
                        survey = updatedSurvey;
                        toolbar.setTitle(survey.getSurveyTitle());
                        Toast.makeText(getApplicationContext(), getString(R.string.survey_updated_successfully), LENGTH_SHORT).show();
                        updates.clear();

                        MenuItem saveMenuItem = mMenu.findItem(R.id.action_save);
                        if (saveMenuItem != null) {
                            saveMenuItem.setVisible(false);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.survey_update_failed), LENGTH_SHORT).show();
                    }
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBack() {
        if (updates.isEmpty())
            finish();
        else showCancelConfirmationDialog();
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.discard_changes_title)
                .setMessage(R.string.discard_changes_msg)
                .setPositiveButton(R.string.continue_btn, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void showEditWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.editing_unavailable)
                .setMessage(R.string.survey_already_has_response)
                /*.setPositiveButton(R.string.edit, (dialog, which) -> {
                    dialog.dismiss();
                    navigateToEditScreen();
                })*/
                .setNegativeButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void showDescriptionDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // or getActivity() for a Fragment
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        View customTitleView = inflater.inflate(R.layout.dialog_title, null);
        TextView titleTextView = customTitleView.findViewById(R.id.dialog_custom_title);
        titleTextView.setText(getString(R.string.update_description));

        // Find the EditText in the custom layout
        TextInputEditText inputEditText = dialogView.findViewById(R.id.dialog_edittext);
        inputEditText.setText(management_LBL_description.getText());

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(customTitleView)
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String enteredText = inputEditText.getText().toString().trim();
                    if (!enteredText.equals(survey.getDescription())) {
                        management_LBL_description.setText(enteredText);
                        updates.put("description", enteredText);
                        if (mMenu != null) {
                            MenuItem saveMenuItem = mMenu.findItem(R.id.action_save);
                            if (saveMenuItem != null) {
                                saveMenuItem.setVisible(true); // or true
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTitleDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(this); // or getActivity() for a Fragment
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        View customTitleView = inflater.inflate(R.layout.dialog_title, null);
        TextView titleTextView = customTitleView.findViewById(R.id.dialog_custom_title);
        titleTextView.setText(getString(R.string.update_title));

        // Find the EditText in the custom layout
        TextInputEditText inputEditText = dialogView.findViewById(R.id.dialog_edittext);
        inputEditText.setText(management_LBL_title.getText());

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(customTitleView)
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String enteredText = inputEditText.getText().toString().trim();
                    if (!enteredText.equals(survey.getDescription())) {
                        management_LBL_title.setText(enteredText);
                        updates.put("surveyTitle", enteredText);
                        if (mMenu != null) {
                            MenuItem saveMenuItem = mMenu.findItem(R.id.action_save);
                            if (saveMenuItem != null) {
                                saveMenuItem.setVisible(true); // or true
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCompletedResponsesCounter();
        getTotalResponsesCounter();
        getQuestionsCount();
    }
}