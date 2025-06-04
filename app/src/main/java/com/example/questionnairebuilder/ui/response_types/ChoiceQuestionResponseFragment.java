package com.example.questionnairebuilder.ui.response_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.questionnairebuilder.QuestionResponseActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.databinding.FragmentChoiceQuestionResponseBinding;
import com.example.questionnairebuilder.interfaces.OneResponseCallback;
import com.example.questionnairebuilder.models.ChoiceQuestion;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.Response;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChoiceQuestionResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChoiceQuestionResponseFragment extends Fragment {
    private FragmentChoiceQuestionResponseBinding binding;
    private MaterialTextView responseChoiceQuestion_LBL_question;
    private MaterialTextView responseChoiceQuestion_LBL_mandatory;
    private MaterialButton responseChoiceQuestion_BTN_save;
    private MaterialButton responseChoiceQuestion_BTN_skip;
    private RadioGroup responseChoiceQuestion_RadioGroup;
    private LinearLayout responseChoiceQuestion_LL_checkBoxContainer;
    private MaterialTextView responseChoiceQuestion_LBL_error;
    private TextInputLayout choiceQuestion_DD_layout_dropdown;
    private AutoCompleteTextView responseChoiceQuestion_DD_dropdown;
    private Question question;
    private Response response;
    private int currentSelectionCount = 0;
    private final List<String> selectedChoices = new ArrayList<>();
    private boolean isSurveyCompleted;

    public ChoiceQuestionResponseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionArgs bundle of question's details.
     * @return A new instance of fragment ChoiceQuestionResponseFragment.
     */
    public static ChoiceQuestionResponseFragment newInstance(Bundle questionArgs) {
        ChoiceQuestionResponseFragment fragment = new ChoiceQuestionResponseFragment();
        fragment.setArguments(questionArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            QuestionTypeEnum type = QuestionTypeEnum.valueOf(args.getString("questionType"));
            if(type.isSingleSelection()) {
                question = new SingleChoiceQuestion(args.getString("questionTitle"),type)
                        .setChoices(args.getStringArrayList("choices"));
            }
            else {
                question = new MultipleChoiceQuestion(args.getString("questionTitle"))
                        .setAllowedSelectionNum(args.getInt("allowedSelectionNum"))
                        .setChoices(args.getStringArrayList("choices"));
            }
            question.setMandatory(args.getBoolean("mandatory"))
                    .setQuestionID(args.getString("questionID"))
                    .setSurveyID(args.getString("surveyID"))
                    .setOrder(args.getInt("order"));
        }
        isSurveyCompleted = ((QuestionResponseActivity) requireActivity()).isSurveyResponseCompleted();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChoiceQuestionResponseBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        createBinding();

        return root;
    }

    private void createBinding() {
        responseChoiceQuestion_LBL_question = binding.responseChoiceQuestionLBLQuestion;
        responseChoiceQuestion_LBL_mandatory = binding.responseChoiceQuestionLBLMandatory;
        responseChoiceQuestion_BTN_save = binding.responseChoiceQuestionBTNSave;
        responseChoiceQuestion_BTN_skip = binding.responseChoiceQuestionBTNSkip;
        responseChoiceQuestion_RadioGroup = binding.responseChoiceQuestionRadioGroup;
        responseChoiceQuestion_LL_checkBoxContainer = binding.responseChoiceQuestionLLCheckBoxContainer;
        responseChoiceQuestion_LBL_error = binding.responseChoiceQuestionLBLError;
        choiceQuestion_DD_layout_dropdown = binding.choiceQuestionDDLayoutDropdown;
        responseChoiceQuestion_DD_dropdown = binding.responseChoiceQuestionDDDropdown;

        if (question != null) {
            responseChoiceQuestion_LBL_question.setText(question.getQuestionTitle());

            if (isSurveyCompleted) {
                responseChoiceQuestion_BTN_skip.setVisibility(GONE);
                responseChoiceQuestion_BTN_save.setVisibility(GONE);
            }
            else {
                if (question.isMandatory()) {
                    responseChoiceQuestion_LBL_mandatory.setVisibility(VISIBLE);
                    responseChoiceQuestion_BTN_skip.setVisibility(GONE);
                } else {
                    responseChoiceQuestion_LBL_mandatory.setVisibility(GONE);
                    responseChoiceQuestion_BTN_skip.setVisibility(VISIBLE);
                }

                // listeners
                responseChoiceQuestion_BTN_save.setOnClickListener(v -> save());
                responseChoiceQuestion_BTN_skip.setOnClickListener(v -> skipQuestion());
            }

            selectedChoices.clear();
            loadResponse(question);
        }
    }

    private void initChoices(){
        ArrayList<String> options = ((ChoiceQuestion)question).getChoices();
        responseChoiceQuestion_RadioGroup.removeAllViews();
        responseChoiceQuestion_LL_checkBoxContainer.removeAllViews();
        if(question.getType() == QuestionTypeEnum.DROPDOWN){
            initDropdownValues(options);
        }
        else if(question.getType().isSingleSelection()) {
            initRadioButtons(options);
        }
        else {
            initCheckboxes(options, ((MultipleChoiceQuestion)question).getAllowedSelectionNum());
        }
    }

    private void initRadioButtons(List<String> options){
        for (String option : options) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(option);
            radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue)));
            radioButton.setId(View.generateViewId()); // Assign a unique ID
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, dpToPx(8));
            radioButton.setLayoutParams(params);

            if (selectedChoices.contains(option)) {
                radioButton.setChecked(true);
            }
            radioButton.setEnabled(!isSurveyCompleted);
            responseChoiceQuestion_RadioGroup.addView(radioButton);
        }
        responseChoiceQuestion_RadioGroup.setVisibility(VISIBLE);
        responseChoiceQuestion_LL_checkBoxContainer.setVisibility(GONE);
        choiceQuestion_DD_layout_dropdown.setVisibility(GONE);
        responseChoiceQuestion_RadioGroup.setEnabled(!isSurveyCompleted);

        if (!isSurveyCompleted) {
            responseChoiceQuestion_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton selectedButton = group.findViewById(checkedId);
                    if (selectedButton != null) {
                        String selectedText = selectedButton.getText().toString();
                        selectedChoices.clear();
                        selectedChoices.add(selectedText);
                        Log.d("RadioGroup", "Selected: " + selectedText);
                    }
                }
            });
        }
    }

    private void initCheckboxes(List<String> options, int maxSelections){
        currentSelectionCount = 0;
        for (String option : options) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(option);
            checkBox.setId(View.generateViewId());
            checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue)));

            // Optional spacing
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, dpToPx(8));
            checkBox.setLayoutParams(params);

            if (selectedChoices.contains(option)) {
                checkBox.setChecked(true);
            }
            if(isSurveyCompleted){
                checkBox.setEnabled(false);
            }
            else {
                checkBox.setEnabled(true);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        if (currentSelectionCount >= maxSelections) {
                            // Max reached: undo the check and show a warning
                            buttonView.setChecked(false);
                            Toast.makeText(getContext(), "You can select up to " + maxSelections + " options.", Toast.LENGTH_SHORT).show();
                        } else {
                            currentSelectionCount++;
                            selectedChoices.add(option);
                            Log.d("CheckBox", checkBox.getText() + " selected");
                        }
                    } else {
                        currentSelectionCount--;
                        selectedChoices.remove(option);
                        Log.d("CheckBox", checkBox.getText() + " unselected");
                    }
                });
            }
            responseChoiceQuestion_LL_checkBoxContainer.addView(checkBox);
        }
        responseChoiceQuestion_RadioGroup.setVisibility(GONE);
        responseChoiceQuestion_LL_checkBoxContainer.setVisibility(VISIBLE);
        choiceQuestion_DD_layout_dropdown.setVisibility(GONE);
    }

    private void initDropdownValues(List<String> options) {
        ArrayAdapter<String> adapterItems_dropdownOptions = new ArrayAdapter<>(requireActivity(), R.layout.item_dropdown, options);
        if(!selectedChoices.isEmpty())
            responseChoiceQuestion_DD_dropdown.setText(selectedChoices.get(0));

        responseChoiceQuestion_DD_dropdown.setAdapter(adapterItems_dropdownOptions);

        responseChoiceQuestion_DD_dropdown.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedItem = adapterItems_dropdownOptions.getItem(position);
            if (selectedItem != null) {
                selectedChoices.clear();
                selectedChoices.add(selectedItem);
                Log.d("RadioGroup", "Selected: " + selectedItem);
            }
        });

        responseChoiceQuestion_RadioGroup.setVisibility(GONE);
        responseChoiceQuestion_LL_checkBoxContainer.setVisibility(GONE);
        choiceQuestion_DD_layout_dropdown.setVisibility(VISIBLE);
        choiceQuestion_DD_layout_dropdown.setEnabled(!isSurveyCompleted);
        choiceQuestion_DD_layout_dropdown.setHint(isSurveyCompleted ? null : getString(R.string.select));
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void loadResponse(Question question) {
        String userID = AuthenticationManager.getInstance().getCurrentUser().getUid();
        FirestoreManager.getInstance().getResponse(question.getSurveyID(), question.getQuestionID(), userID, new OneResponseCallback() {
            @Override
            public void onResponseLoad(Response theResponse) {
                if(theResponse != null) {
                    response = theResponse;
                    selectedChoices.clear();
                    selectedChoices.addAll(response.getResponseValues());
                    currentSelectionCount = selectedChoices.size();
                }
                initChoices();
            }

            @Override
            public void onResponseLoadFailure() {
                response = null;
                initChoices();
            }
        });
    }

    private void skipQuestion() {
        ((QuestionResponseActivity) requireActivity()).skipQuestion();
    }

    private void save() {
        if (isValidResponse()) {
            responseChoiceQuestion_LBL_error.setVisibility(GONE);
            if(response == null) {
                response = new Response()
                        .setResponseID(UUID.randomUUID().toString())
                        .setSurveyID(question.getSurveyID())
                        .setQuestionID(question.getQuestionID())
                        .setMandatory(question.isMandatory())
                        .setResponseValues(selectedChoices);
            }
            else {
                response.getResponseValues().clear();
                response.setResponseValues(selectedChoices);
            }
            ((QuestionResponseActivity)requireActivity()).saveResponse(response);
        }
        else{
            responseChoiceQuestion_LBL_error.setVisibility(VISIBLE);
        }
    }

    private boolean isValidResponse() {
        return !question.isMandatory() || !selectedChoices.isEmpty();
    }
}