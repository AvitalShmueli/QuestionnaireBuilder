package com.example.questionnairebuilder;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.example.questionnairebuilder.ui.new_question.NewQuestionFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewSurveyActivity extends AppCompatActivity {
    private TextInputLayout newSurvey_TIL_date;
    private TextInputEditText newSurvey_TIET_date;
    private MaterialButton newSurvey_BTN_themeRed;
    private MaterialButton newSurvey_BTN_themeGreen;
    private MaterialButton newSurvey_BTN_themeBlue;
    private MaterialButton newSurvey_BTN_themePurple;
    private MaterialToolbar myToolBar;
    private MaterialButton newSurvey_BTN_continue;
    private TextInputEditText newSurvey_TXT_title;
    private TextInputEditText newSurvey_TXT_description;
    private String selectedTheme = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        findViews();
        initViews();
        setupDateFieldBehavior();
        handleThemeColorSelection();
    }

    private void initViews() {
        myToolBar.setNavigationOnClickListener(v -> finish());

        newSurvey_BTN_continue.setOnClickListener(v -> {
            if (validateForm()) {
                Intent intent = new Intent(NewSurveyActivity.this, QuestionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
        }
        else
            titleLayout.setError(null);

        if (desc.isEmpty()) {
            descLayout.setError("Description is required");
            valid = false;
        }
        else
            descLayout.setError(null);

        if (date.isEmpty()) {
            newSurvey_TIL_date.setError("Due date is required");
            valid = false;
        }
        else if (!isValidDate(date)) {
            newSurvey_TIL_date.setError("Invalid date format");
            valid = false;
        }
        else
            newSurvey_TIL_date.setError(null);

        if (selectedTheme == null) {
            Toast.makeText(this, "Please select a theme", Toast.LENGTH_SHORT).show();
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
                    newSurvey_TIL_date.setError("Invalid date");
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
        newSurvey_BTN_themeRed = findViewById(R.id.newSurvey_BTN_themeRed);
        newSurvey_BTN_themeGreen = findViewById(R.id.newSurvey_BTN_themeGreen);
        newSurvey_BTN_themeBlue = findViewById(R.id.newSurvey_BTN_themeBlue);
        newSurvey_BTN_themePurple = findViewById(R.id.newSurvey_BTN_themePurple);
        myToolBar = findViewById(R.id.topAppBar);
        newSurvey_BTN_continue = findViewById(R.id.newSurvey_BTN_continue);
        newSurvey_TXT_title = findViewById(R.id.newSurvey_TXT_title);
        newSurvey_TXT_description = findViewById(R.id.newSurvey_TXT_description);
    }

    private void handleThemeColorSelection() {
        MaterialButton[] themeButtons = {newSurvey_BTN_themeRed, newSurvey_BTN_themeGreen, newSurvey_BTN_themeBlue, newSurvey_BTN_themePurple};

        int[] fillColors = { // Define their corresponding fill colors
                ColorUtils.setAlphaComponent(ContextCompat.getColor(this, R.color.theme_circle_red), 200),
                ColorUtils.setAlphaComponent(ContextCompat.getColor(this, R.color.theme_circle_green), 200),
                ColorUtils.setAlphaComponent(ContextCompat.getColor(this, R.color.theme_circle_blue), 200),
                ColorUtils.setAlphaComponent(ContextCompat.getColor(this, R.color.theme_circle_purple), 200)
        };

        final String[] themeIds = {"red", "green", "blue", "purple"};

        for (int i = 0; i < themeButtons.length; i++) { // Add click listeners
            final int index = i;
            themeButtons[i].setOnClickListener(v -> {
                animateButtonClick(themeButtons[index]);
                for (int j = 0; j < themeButtons.length; j++) // Reset background colors
                    themeButtons[j].setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                themeButtons[index].setBackgroundTintList(ColorStateList.valueOf(fillColors[index])); // Fill selected one
                selectedTheme = themeIds[index]; // Save selected theme
            });
        }
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
                .setTitleText("Select Due Date")
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
}