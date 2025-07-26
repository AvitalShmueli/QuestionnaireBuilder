package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.User;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.DatePickerHelper;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.UUID;

public class NewSurveyActivity extends AppCompatActivity {
    private TextInputLayout newSurvey_TIL_date;
    private TextInputEditText newSurvey_TIET_date;
    private MaterialToolbar myToolBar;
    private MaterialButton newSurvey_BTN_continue;
    private TextInputEditText newSurvey_TXT_title;
    private TextInputEditText newSurvey_TXT_description;
    private ChipGroup tagChipGroup;
    private final Set<String> selectedTags = new HashSet<>();
    private User author;
    private DatePickerHelper datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        findViews();
        initViews();

        setupDateFieldBehavior();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showCancelConfirmationDialog();
            }
        });
    }

    private void initViews() {
        FirestoreManager firebaseManager = FirestoreManager.getInstance();
        AuthenticationManager authenticationManager = AuthenticationManager.getInstance();

        if (authenticationManager.getCurrentUser() != null) {
            String currentUserId = authenticationManager.getCurrentUser().getUid();
            firebaseManager.getUserData(currentUserId, user -> author = user);
        }

        myToolBar.setNavigationOnClickListener(v -> showCancelConfirmationDialog());

        newSurvey_BTN_continue.setOnClickListener(v -> {
            if (validateForm()) {
                Date now = new Date(); // current timestamp
                Survey survey = new Survey()
                        .setID(UUID.randomUUID().toString())
                        .setSurveyTitle(newSurvey_TXT_title.getText().toString().trim())
                        .setDescription(newSurvey_TXT_description.getText().toString().trim())
                        .setDueDate(datePicker.getSelectedDate())
                        .setStatus(Survey.SurveyStatus.Draft)
                        .setAuthor(author)
                        .setCreated(now)
                        .setModified(now)
                        .setSurveyViewers(new ArrayList<>())
                        .setTags(convertToTagEnums(new ArrayList<>(selectedTags)));

                survey.save();

                Intent intent = new Intent(NewSurveyActivity.this, QuestionsActivity.class);
                intent.putExtra("surveyID",survey.getID());
                intent.putExtra("survey_title",survey.getSurveyTitle());
                intent.putExtra(QuestionsActivity.KEY_EDIT_MODE, true);
                startActivity(intent);
                finish();
            }
        });

        setupTagChips();
    }

    private List<Survey.SurveyTag> convertToTagEnums(List<String> tags) {
        List<Survey.SurveyTag> tagEnums = new ArrayList<>();
        for (String tag : tags) {
            try {
                tagEnums.add(Survey.SurveyTag.valueOf(tag.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        return tagEnums;
    }

    private void setupTagChips() {
        for (Survey.SurveyTag tagEnum : Survey.SurveyTag.values()) {
            String tag = tagEnum.name().charAt(0) + tagEnum.name().substring(1).toLowerCase();
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setTextColor(ContextCompat.getColor(this, R.color.dark_blue));
            chip.setChipBackgroundColorResource(R.color.light_blue);
            chip.setChipStrokeWidth(2f);
            chip.setChipStrokeColorResource(R.color.blackish);
            chip.setChipCornerRadius(12f);
            chip.setChipIconResource(tagEnum.getIconResId());
            chip.setChipIconTintResource(R.color.dark_blue);
            chip.setChipIconSize(50f);
            chip.setIconStartPadding(8f);
            chip.setChipIconVisible(true);
            chip.setOnClickListener(v -> {
                if (selectedTags.contains(tag)) {
                    // Deselecting tag
                    selectedTags.remove(tag);
                    chip.setChipBackgroundColorResource(R.color.light_blue);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.dark_blue));
                } else {
                    // Trying to select a new tag
                    if (selectedTags.size() >= 3) {
                        Toast.makeText(this, "You can select up to 3 tags", Toast.LENGTH_SHORT).show();
                        chip.setChecked(false); // prevent visually staying checked
                    } else {
                        selectedTags.add(tag);
                        chip.setChipBackgroundColorResource(R.color.blue);
                        chip.setTextColor(ContextCompat.getColor(this, R.color.white));
                    }
                }
            });
            tagChipGroup.addView(chip);
        }
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.cancel_survey_title)
                .setMessage(R.string.cancel_survey_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private boolean validateForm() {
        boolean valid = true;

        String title = newSurvey_TXT_title.getText().toString().trim();
        String desc = newSurvey_TXT_description.getText().toString().trim();
        String date = newSurvey_TIET_date.getText().toString().trim();

        TextInputLayout titleLayout = findViewById(R.id.newSurvey_TIL_title);
        TextInputLayout descLayout = findViewById(R.id.newSurvey_TIL_description);

        if (title.isEmpty()) {
            titleLayout.setError("Title is required");
            newSurvey_TXT_title.requestFocus();
            valid = false;
        } else
            titleLayout.setError(null);

        if (desc.isEmpty()) {
            descLayout.setError("Description is required");
            valid = false;
        } else
            descLayout.setError(null);

        if (date.isEmpty()) {
            newSurvey_TIL_date.setError(getString(R.string.date_required_alert));
            newSurvey_TIET_date.setHintTextColor(ContextCompat.getColor(this, R.color.error_red));
            valid = false;
        } else if (!datePicker.isSelectedDateValid()) {
            newSurvey_TIL_date.setError(getString(R.string.invalid_date_format));
            newSurvey_TIET_date.setHintTextColor(ContextCompat.getColor(this, R.color.error_red));
            valid = false;
        } else
            newSurvey_TIL_date.setError(null);

        if (selectedTags.isEmpty()) {
            Toast.makeText(this, "Please select at least one tag", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void setupDateFieldBehavior() {
        datePicker = new DatePickerHelper(this,getSupportFragmentManager(),newSurvey_TIL_date,newSurvey_TIET_date);
        datePicker.setAllowPastDates(false);
    }

    private void findViews() {
        newSurvey_TIET_date = findViewById(R.id.newSurvey_TIET_date);
        newSurvey_TIL_date = findViewById(R.id.newSurvey_TIL_date);
        myToolBar = findViewById(R.id.topAppBar);
        newSurvey_BTN_continue = findViewById(R.id.newSurvey_BTN_continue);
        newSurvey_TXT_title = findViewById(R.id.newSurvey_TXT_title);
        newSurvey_TXT_description = findViewById(R.id.newSurvey_TXT_description);
        tagChipGroup = findViewById(R.id.tagChipGroup);
    }

    private void animateButtonClick(MaterialButton button) {
        button.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(100)
                .withEndAction(() -> button.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
        if (ev.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            android.view.View v = getCurrentFocus();
            if (v instanceof android.widget.EditText) {
                android.graphics.Rect outRect = new android.graphics.Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    android.view.inputmethod.InputMethodManager imm =
                            (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}