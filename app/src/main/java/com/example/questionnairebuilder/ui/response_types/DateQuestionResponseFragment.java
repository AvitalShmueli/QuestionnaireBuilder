package com.example.questionnairebuilder.ui.response_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.questionnairebuilder.QuestionResponseActivity;
import com.example.questionnairebuilder.databinding.FragmentDateQuestionResponseBinding;
import com.example.questionnairebuilder.interfaces.OnResponseCallback;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.DateSelectionModeEnum;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.Response;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateQuestionResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateQuestionResponseFragment extends Fragment {
    private FragmentDateQuestionResponseBinding binding;
    private MaterialTextView responseDateQuestion_LBL_question;
    private MaterialTextView responseDateQuestion_LBL_mandatory;
    private TextInputLayout responseDateQuestion_TIL_date;
    private TextInputEditText responseDateQuestion_TXT_date;
    private TextInputLayout responseDateQuestion_TIL_date2;
    private TextInputEditText responseDateQuestion_TXT_date2;
    private MaterialButton responseDateQuestion_BTN_save;
    private MaterialButton responseDateQuestion_BTN_skip;
    private Question question;
    private Response response;

    public DateQuestionResponseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *@param questionArgs bundle of question's details.
     * @return A new instance of fragment DateQuestionResponseFragment.
     */
    public static DateQuestionResponseFragment newInstance(Bundle questionArgs) {
        DateQuestionResponseFragment fragment = new DateQuestionResponseFragment();
        fragment.setArguments(questionArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            question = new DateQuestion(args.getString("questionTitle"))
                    .setDateMode(DateSelectionModeEnum.valueOf(args.getString("dateSelectionMode")))
                    .setMandatory(args.getBoolean("mandatory"))
                    .setQuestionID(args.getString("questionID"))
                    .setSurveyID(args.getString("surveyID"))
                    .setOrder(args.getInt("order"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDateQuestionResponseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();
        loadResponse(question);

        return root;
    }

    private void createBinding() {
        responseDateQuestion_LBL_question = binding.responseDateQuestionLBLQuestion;
        responseDateQuestion_LBL_mandatory = binding.responseDateQuestionLBLMandatory;
        responseDateQuestion_TIL_date = binding.responseDateQuestionTILDate;
        responseDateQuestion_TXT_date = binding.responseDateQuestionTXTDate;
        responseDateQuestion_TIL_date2 = binding.responseDateQuestionTILDate2;
        responseDateQuestion_TXT_date2 = binding.responseDateQuestionTXTDate2;
        responseDateQuestion_BTN_save = binding.responseDateQuestionBTNSave;
        responseDateQuestion_BTN_skip = binding.responseDateQuestionBTNSkip;

        if(question != null){
            responseDateQuestion_LBL_question.setText(question.getQuestionTitle());
            if(question.isMandatory()) {
                responseDateQuestion_LBL_mandatory.setVisibility(VISIBLE);
                responseDateQuestion_BTN_skip.setVisibility(GONE);
            }
            else {
                responseDateQuestion_LBL_mandatory.setVisibility(GONE);
                responseDateQuestion_BTN_skip.setVisibility(VISIBLE);
            }

            setupDateFieldBehavior();

            // listeners
            responseDateQuestion_BTN_save.setOnClickListener(v -> save());
            responseDateQuestion_BTN_skip.setOnClickListener(v -> skipQuestion());
        }
    }

    private void loadResponse(Question question) {
        String userID = AuthenticationManager.getInstance().getCurrentUser().getUid();
        FirestoreManager.getInstance().getResponse(question.getSurveyID(), question.getQuestionID(), userID, new OnResponseCallback() {
            @Override
            public void onResponseLoad(Response theResponse) {
                if(theResponse != null) {
                    response = theResponse;
                    if (!response.getResponseValues().isEmpty()) {
                        if(((DateQuestion)question).getDateMode() == DateSelectionModeEnum.DATE_RANGE && response.getResponseValues().size() == 2){
                            responseDateQuestion_TXT_date.setText(response.getResponseValues().get(0));
                            responseDateQuestion_TXT_date2.setText(response.getResponseValues().get(1));
                        }
                        else{
                            responseDateQuestion_TXT_date.setText(response.getResponseValues().get(0));
                        }
                    }
                }
            }

            @Override
            public void onResponseLoadFailure() {
                response = null;
            }
        });
    }

    private void setupDateFieldBehavior() {
        setupDateField(responseDateQuestion_TIL_date, responseDateQuestion_TXT_date);

        if(((DateQuestion)question).getDateMode() == DateSelectionModeEnum.DATE_RANGE){
            responseDateQuestion_TIL_date2.setVisibility(VISIBLE);
            responseDateQuestion_TIL_date.setHint("Start Date");
            responseDateQuestion_TIL_date2.setHint("End Date");
            setupDateField(responseDateQuestion_TIL_date2, responseDateQuestion_TXT_date2);
        }
        else{
            responseDateQuestion_TIL_date2.setVisibility(GONE);
        }
    }

    private void setupDateField(TextInputLayout inputLayout, TextInputEditText editText) {
        inputLayout.setEndIconOnClickListener(v -> showMaterialDatePicker(editText));

        editText.addTextChangedListener(new TextWatcher() {
            boolean isEditing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String input = s.toString().replaceAll("[^\\d]", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < input.length() && i < 8; i++) {
                    if (i == 2 || i == 4) formatted.append("/");
                    formatted.append(input.charAt(i));
                }

                editText.setText(formatted.toString());
                editText.setSelection(formatted.length());

                if (formatted.length() == 10 && !isValidDate(formatted.toString()))
                    inputLayout.setError("Invalid date");
                else
                    inputLayout.setError(null);

                isEditing = false;
            }
        });

        editText.setOnFocusChangeListener((v, hasFocus) ->
                editText.setHint(hasFocus ? "DD/MM/YYYY" : "")
        );
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

    private void showMaterialDatePicker(TextInputEditText editText) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year);
            editText.setText(formattedDate);

            editText.requestFocus();
            editText.setSelection(formattedDate.length());
        });

        datePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
    }

    private void skipQuestion() {
        ((QuestionResponseActivity) requireActivity()).skipQuestion();
    }

    private void save() {
        if (isValidResponse()) {
            ArrayList<String> selectedDates = new ArrayList<>();
            selectedDates.add(responseDateQuestion_TXT_date.getText().toString());
            if(((DateQuestion)question).getDateMode() == DateSelectionModeEnum.DATE_RANGE)
                selectedDates.add(responseDateQuestion_TXT_date2.getText().toString());

            if(response == null) {
                response = new Response()
                        .setResponseID(UUID.randomUUID().toString())
                        .setSurveyID(question.getSurveyID())
                        .setQuestionID(question.getQuestionID())
                        .setResponseValues(selectedDates);
            }
            else {
                response.setResponseValues(selectedDates);
            }
            ((QuestionResponseActivity)requireActivity()).saveResponse(response);
        }
    }

    private boolean isValidResponse() {
        boolean valid = validateDateField(responseDateQuestion_TXT_date, responseDateQuestion_TIL_date);

        // If it's a date range, validate the second date field
        if (((DateQuestion) question).getDateMode() == DateSelectionModeEnum.DATE_RANGE) {
            if (!validateDateField(responseDateQuestion_TXT_date2, responseDateQuestion_TIL_date2)) {
                valid = false;
            }
        }

        return valid;
    }

    private boolean validateDateField(TextInputEditText editText, TextInputLayout inputLayout) {
        String date = "";
        if(editText.getText() != null)
            date = editText.getText().toString().trim();
        Context context = requireContext();

        if (question.isMandatory() && date.isEmpty()) {
            inputLayout.setError("Date is required");
            editText.setHintTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_error));
            return false;
        } else if (!date.isEmpty() && !isValidDate(date)) {
            inputLayout.setError("Invalid date format");
            editText.setHintTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_error));
            return false;
        } else {
            inputLayout.setError(null);
            return true;
        }
    }
}