package com.example.questionnairebuilder.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questionnairebuilder.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class NewSurveyActivity extends AppCompatActivity {
    private TextInputLayout newSurvey_TIL_date;
    private TextInputEditText newSurvey_TIET_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        newSurvey_TIET_date = findViewById(R.id.newSurvey_TIET_date);
        newSurvey_TIL_date = findViewById(R.id.newSurvey_TIL_date);
        newSurvey_TIL_date.setEndIconOnClickListener(v -> showMaterialDatePicker());
        newSurvey_TIET_date.addTextChangedListener(new TextWatcher() {
            private boolean isEditing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String input = s.toString().replaceAll("[^\\d]", ""); // Only digits
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < input.length() && i < 8; i++) {
                    if (i == 2 || i == 4) {
                        formatted.append("/");
                    }
                    formatted.append(input.charAt(i));
                }

                newSurvey_TIET_date.setText(formatted.toString());
                newSurvey_TIET_date.setSelection(formatted.length()); // Move cursor to end

                // Optional validation
                if (formatted.length() == 10 && !isValidDate(formatted.toString())) {
                    newSurvey_TIL_date.setError("Invalid date");
                } else {
                    newSurvey_TIL_date.setError(null);
                }

                isEditing = false;
            }
        });

        newSurvey_TIET_date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                newSurvey_TIET_date.setHint("DD/MM/YYYY");
            } else {
                newSurvey_TIET_date.setHint(""); // remove when not focused
            }
        });
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
}