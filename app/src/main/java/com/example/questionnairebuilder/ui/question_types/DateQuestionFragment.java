package com.example.questionnairebuilder.ui.question_types;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.questionnairebuilder.EditQuestionActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.databinding.FragmentDateQuestionBinding;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.DateSelectionModeEnum;
import com.example.questionnairebuilder.models.Question;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DateQuestionFragment extends Fragment implements UnsavedChangesHandler {

    private FragmentDateQuestionBinding binding;
    private TextInputLayout dateQuestion_TIL_question;
    private TextInputEditText dateQuestion_TXT_question;
    private MaterialSwitch dateQuestion_SW_mandatory;
    private MaterialButton dateQuestion_BTN_save;
    private MaterialButton dateQuestion_BTN_cancel;
    private AutoCompleteTextView dateQuestion_DD_DateSelectionMode;
    private DateSelectionModeEnum selectedMode;


    public DateQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (hasUnsavedChanges()) {
                            // Call the dialog from the activity
                            ((EditQuestionActivity) requireActivity()).showCancelConfirmationDialog();
                        } else {
                            requireActivity().finish();
                        }
                    }
                });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDateQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();

        return root;
    }


    private void createBinding() {
        dateQuestion_BTN_save = binding.dateQuestionBTNSave;
        dateQuestion_BTN_cancel = binding.dateQuestionBTNCancel;
        dateQuestion_TIL_question = binding.dateQuestionTILQuestion;
        dateQuestion_TXT_question = binding.dateQuestionTXTQuestion;
        dateQuestion_SW_mandatory = binding.dateQuestionSWMandatory;

        // Date Selection Mode dropdown
        dateQuestion_DD_DateSelectionMode = binding.dateQuestionDDDateSelectionMode;
        initDropDownValues();

        dateQuestion_BTN_cancel.setOnClickListener(v -> cancel());
        dateQuestion_BTN_save.setOnClickListener(v -> save());

    }


    private void initDropDownValues() {
        Map<DateSelectionModeEnum, String> itemsDateSelectionMode = new LinkedHashMap<>();
        itemsDateSelectionMode.put(DateSelectionModeEnum.SINGLE_DATE, getString(R.string.single_date));
        itemsDateSelectionMode.put(DateSelectionModeEnum.DATE_RANGE, getString(R.string.date_range));

        Map<String, DateSelectionModeEnum> reverseItems = new LinkedHashMap<>();
        ArrayList<String> values = new ArrayList<>();
        for (Map.Entry<DateSelectionModeEnum, String> entry : itemsDateSelectionMode.entrySet()) {
            reverseItems.put(entry.getValue(), entry.getKey());
            values.add(entry.getValue());
        }

        if(selectedMode == null)
            selectedMode = DateSelectionModeEnum.SINGLE_DATE;
        dateQuestion_DD_DateSelectionMode.setText(itemsDateSelectionMode.get(selectedMode));

        ArrayAdapter<String> adapterItems_DateSelectionMode = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, values);
        dateQuestion_DD_DateSelectionMode.setAdapter(adapterItems_DateSelectionMode);
        dateQuestion_DD_DateSelectionMode.setOnItemClickListener((adapterView, view, position, id) -> selectedMode = reverseItems.get(adapterItems_DateSelectionMode.getItem(position)));
    }


    private void loadQuestionDetails(Question q){
        //TODO
    }


    private void save() {
        String questionTitle = null;
        if (!isValid())
            dateQuestion_TIL_question.setError(getString(R.string.error_required));
        else {
            dateQuestion_TIL_question.setError(null);
            if(dateQuestion_TXT_question.getText() != null)
                questionTitle = dateQuestion_TXT_question.getText().toString().trim();
            boolean mandatory = dateQuestion_SW_mandatory.isChecked();
            Question q = new DateQuestion(questionTitle).setDateMode(selectedMode).setMandatory(mandatory);
            q.save();
        }
    }


    private boolean isValid(){
        return dateQuestion_TXT_question.getText() != null &&
                !dateQuestion_TXT_question.getText().toString().trim().isEmpty();
    }


    private void cancel(){
        if(hasUnsavedChanges())
            ((EditQuestionActivity) requireActivity()).showCancelConfirmationDialog();
        else
            requireActivity().finish();
    }


    @Override
    public boolean hasUnsavedChanges() {
        return dateQuestion_TXT_question.getText() != null &&
                !dateQuestion_TXT_question.getText().toString().trim().isEmpty();
    }
}