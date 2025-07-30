package com.example.questionnairebuilder.utilities;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.questionnairebuilder.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerHelper {

    private final Context context;
    private final FragmentManager fragmentManager;
    private final TextInputLayout inputLayout;
    private final TextInputEditText inputEditText;

    private boolean allowPastDates = true;

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public DatePickerHelper(Context context, FragmentManager fragmentManager,
                            TextInputLayout inputLayout,
                            TextInputEditText inputEditText) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.inputLayout = inputLayout;
        this.inputEditText = inputEditText;
        setupBehavior();
    }

    private void setupBehavior() {
        inputEditText.addTextChangedListener(new TextWatcher() {
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

                String formattedStr = formatted.toString();

                if (!formattedStr.equals(s.toString())) {
                    inputEditText.setText(formattedStr);
                    inputEditText.setSelection(formattedStr.length()); // Move cursor to end
                }

                if (formattedStr.length() == 10 && !isValidDate(formattedStr))
                    inputLayout.setError(context.getString(R.string.invalid_date));
                else
                    inputLayout.setError(null);

                isEditing = false;
            }

        });

        inputLayout.setEndIconOnClickListener(v -> showDatePickerDialog());

        inputLayout.setErrorIconOnClickListener(v -> showDatePickerDialog());

        inputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            inputEditText.post(() -> {
                inputEditText.setHint(hasFocus ? "DD/MM/YYYY" : "");
            });
        });
    }

    public void setAllowPastDates(boolean allow) {
        this.allowPastDates = allow;
    }

    public Date getSelectedDate() {
        String dateStr = inputEditText.getText() != null ? inputEditText.getText().toString() : "";
        if (TextUtils.isEmpty(dateStr)) {
            return null;
        }

        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private void showDatePickerDialog() {
        Date selectedDate = getSelectedDate();
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
            Log.d("DatePickerHelper","Timezone offset: "+localCal.getTimeZone().getOffset(localCal.getTimeInMillis())/3600000+"");
            long utcMillis = localCal.getTimeInMillis() + localCal.getTimeZone().getOffset(localCal.getTimeInMillis());
            builder.setSelection(utcMillis);
        } else {
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        }

        if (!allowPastDates) {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            constraintsBuilder.setValidator(DateValidatorPointForward.now());
            builder.setCalendarConstraints(constraintsBuilder.build());
        }


        final MaterialDatePicker<Long> picker = builder.build();

        picker.show(fragmentManager, "DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            String formattedDate = String.format(Locale.getDefault(),
                    "%02d/%02d/%04d",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.YEAR));

            inputEditText.setText(formattedDate);
            inputEditText.requestFocus();
            inputEditText.setSelection(formattedDate.length());
        });
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false); // Ensures 32/13/2025 is rejected

        try {
            Date inputDate = sdf.parse(date); // Will throw ParseException if invalid
            Date today = new Date();

            today = sdf.parse(sdf.format(today));
            if (!allowPastDates && inputDate != null)
                return !inputDate.before(today);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isSelectedDateValid(){
        return isValidDate(inputEditText.getText().toString());
    }

}
