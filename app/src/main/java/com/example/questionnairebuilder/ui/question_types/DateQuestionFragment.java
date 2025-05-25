package com.example.questionnairebuilder.ui.question_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import com.example.questionnairebuilder.interfaces.SaveHandler;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.DateSelectionModeEnum;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class DateQuestionFragment extends Fragment implements UnsavedChangesHandler, SaveHandler {

    private FragmentDateQuestionBinding binding;
    private TextInputLayout dateQuestion_TIL_question;
    private TextInputEditText dateQuestion_TXT_question;
    private MaterialSwitch dateQuestion_SW_mandatory;
    private MaterialButton dateQuestion_BTN_save;
    private MaterialButton dateQuestion_BTN_cancel;
    private MaterialButton dateQuestion_BTN_delete;
    private AutoCompleteTextView dateQuestion_DD_DateSelectionMode;
    private DateSelectionModeEnum selectedMode;
    private String surveyID;
    private DateQuestion question;
    private int currentQuestionOrder;

    public DateQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionArgs bundle of question's details.
     * @return A new instance of fragment DateQuestionFragment.
     */
    public static DateQuestionFragment newInstance(Bundle questionArgs) {
        DateQuestionFragment fragment = new DateQuestionFragment();
        fragment.setArguments(questionArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            surveyID = args.getString("surveyID");
            currentQuestionOrder = args.getInt("order");
            if (args.getString("questionID") != null) { // edit question
                question = (DateQuestion) new DateQuestion(args.getString("questionTitle"))
                        .setDateMode(DateSelectionModeEnum.valueOf(args.getString("dateSelectionMode")))
                        .setMandatory(args.getBoolean("mandatory"))
                        .setQuestionID(args.getString("questionID"))
                        .setSurveyID(surveyID)
                        .setOrder(args.getInt("order"));
            }
        }

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
        initView();
        loadQuestionDetails(question);

        return root;
    }

    private void createBinding() {
        dateQuestion_BTN_save = binding.dateQuestionBTNSave;
        dateQuestion_BTN_cancel = binding.dateQuestionBTNCancel;
        dateQuestion_BTN_delete = binding.dateQuestionBTNDelete;
        dateQuestion_TIL_question = binding.dateQuestionTILQuestion;
        dateQuestion_TXT_question = binding.dateQuestionTXTQuestion;
        dateQuestion_SW_mandatory = binding.dateQuestionSWMandatory;
        dateQuestion_DD_DateSelectionMode = binding.dateQuestionDDDateSelectionMode;
    }

    private void initView(){
        initDropDownValues();
        dateQuestion_BTN_cancel.setOnClickListener(v -> cancel());
        dateQuestion_BTN_save.setOnClickListener(v -> save());
        if(question != null) {
            dateQuestion_BTN_delete.setVisibility(VISIBLE);
            dateQuestion_BTN_delete.setOnClickListener(v -> delete());
        }
        else dateQuestion_BTN_delete.setVisibility(GONE);
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

        ArrayAdapter<String> adapterItems_DateSelectionMode = new ArrayAdapter<>(requireActivity(), R.layout.item_dropdown, values);
        dateQuestion_DD_DateSelectionMode.setAdapter(adapterItems_DateSelectionMode);
        dateQuestion_DD_DateSelectionMode.setOnItemClickListener((adapterView, view, position, id) -> selectedMode = reverseItems.get(adapterItems_DateSelectionMode.getItem(position)));
    }

    private void loadQuestionDetails(DateQuestion q){
        if(q == null) {
            return;
        }

        dateQuestion_TXT_question.setText(q.getQuestionTitle());
        dateQuestion_SW_mandatory.setChecked(q.isMandatory());
        selectedMode = q.getDateMode();

        initDropDownValues(); //Date Selection Mode dropdown
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
            if(question == null) {
                question = (DateQuestion) new DateQuestion(questionTitle)
                        .setDateMode(selectedMode)
                        .setQuestionID(UUID.randomUUID().toString())
                        .setSurveyID(surveyID)
                        .setMandatory(mandatory)
                        .setOrder(currentQuestionOrder);
            }
            else{
                question.setDateMode(selectedMode)
                        .setQuestionTitle(questionTitle)
                        .setMandatory(mandatory);
            }
            question.save();
            requireActivity().finish();
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

    private void delete(){
        ((EditQuestionActivity) requireActivity()).showDeleteConfirmationDialog(question);
    }

    @Override
    public boolean hasUnsavedChanges() {
        String currentText = dateQuestion_TXT_question.getText() != null
                ? dateQuestion_TXT_question.getText().toString().trim()
                : "";
        boolean currentMandatory = dateQuestion_SW_mandatory.isChecked();
        DateSelectionModeEnum currentDateMode = selectedMode;

        String originalText = question != null ? question.getQuestionTitle() : "";
        boolean originalMandatory = question != null && question.isMandatory();
        DateSelectionModeEnum originalDateMode = question != null ? question.getDateMode() : null;

        boolean textChanged = !currentText.equals(originalText);
        boolean mandatoryChanged = currentMandatory != originalMandatory;
        boolean dateModeChanged = currentDateMode != originalDateMode;

        return textChanged || mandatoryChanged || dateModeChanged;
    }

    @Override
    public void onSaveClicked() {
        save();
    }
}