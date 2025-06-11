package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.User;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
                String dateStr = newSurvey_TIET_date.getText().toString().trim();
                Date dueDate;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    dueDate = sdf.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    newSurvey_TIL_date.setError(getString(R.string.invalid_date_format));
                    return;
                }

                Date now = new Date(); // current timestamp

                Survey survey = new Survey()
                        .setID(UUID.randomUUID().toString())
                        .setSurveyTitle(newSurvey_TXT_title.getText().toString().trim())
                        .setDescription(newSurvey_TXT_description.getText().toString().trim())
                        .setDueDate(dueDate)
                        .setStatus(Survey.SurveyStatus.Draft)
                        .setAuthor(author)
                        .setCreated(now)
                        .setModified(now)
                        .setSurveyViewers(new ArrayList<>())
                        .setTags(convertToTagEnums(new ArrayList<>(selectedTags)));

                survey.save();

                Intent intent = new Intent(NewSurveyActivity.this, QuestionsActivity.class);
                intent.putExtra("surveyID",survey.getID());
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
                    selectedTags.remove(tag);
                    chip.setChipBackgroundColorResource(R.color.light_blue);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.dark_blue));
                } else {
                    selectedTags.add(tag);
                    chip.setChipBackgroundColorResource(R.color.blue);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white));
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
        } else if (!isValidDate(date)) {
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
        newSurvey_TIL_date.setEndIconOnClickListener(v -> showMaterialDatePicker());
        newSurvey_TIET_date.addTextChangedListener(new TextWatcher() {
            boolean isEditing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing)
                    return;
                isEditing = true;

                String input = s.toString().replaceAll("[^\\d]", ""); // Only digits
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < input.length() && i < 8; i++) {
                    if (i == 2 || i == 4)
                        formatted.append("/");
                    formatted.append(input.charAt(i));
                }

                newSurvey_TIET_date.setText(formatted.toString());
                newSurvey_TIET_date.setSelection(formatted.length()); // Move cursor to end

                if (formatted.length() == 10 && !isValidDate(formatted.toString()))
                    newSurvey_TIL_date.setError(getString(R.string.invalid_date));
                else
                    newSurvey_TIL_date.setError(null);

                isEditing = false;
            }
        });

        newSurvey_TIET_date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                newSurvey_TIET_date.setHint("DD/MM/YYYY");
            else
                newSurvey_TIET_date.setHint(""); // Remove when not focused
        });
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

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false); // Ensures 32/13/2025 is rejected

        try {
            sdf.parse(date); // Will throw ParseException if invalid
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void showMaterialDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText(R.string.select_due_date)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year);
            newSurvey_TIET_date.setText(formattedDate);

            newSurvey_TIET_date.requestFocus();
            newSurvey_TIET_date.setSelection(formattedDate.length());
        });
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