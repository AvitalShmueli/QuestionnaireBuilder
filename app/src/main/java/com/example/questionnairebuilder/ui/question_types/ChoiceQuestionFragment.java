package com.example.questionnairebuilder.ui.question_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.EditQuestionActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.adapters.ChoicesAdapter;
import com.example.questionnairebuilder.databinding.FragmentChoiceQuestionBinding;
import com.example.questionnairebuilder.listeners.OnRowCountChangeListener;
import com.example.questionnairebuilder.models.ChoiceQuestion;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.UUID;

public class ChoiceQuestionFragment extends Fragment implements OnRowCountChangeListener, UnsavedChangesHandler {

    private FragmentChoiceQuestionBinding binding;
    private TextInputLayout choiceQuestion_TIL_question;
    private TextInputEditText choiceQuestion_TXT_question;
    private RecyclerView choiceQuestion_RV_choices;
    private MaterialTextView choiceQuestion_LBL_ErrorRV;
    private MaterialSwitch choiceQuestion_SW_mandatory;
    private MaterialSwitch choiceQuestion_SW_other;
    private LinearLayout choiceQuestion_LL_maxAllowed;
    private AutoCompleteTextView choiceQuestion_DD_maxAllowed;
    private ArrayList<Integer> itemsMaxSelectionsAllowed;
    private Integer selectedMaxSelectionsAllowed = 1;
    private ChoicesAdapter choicesAdapter;
    private MaterialButton choiceQuestion_BTN_save;
    private MaterialButton choiceQuestion_BTN_cancel;
    private int choiceCount = 0;
    private String strType;
    private QuestionTypeEnum questionTypeEnum;
    private String surveyID;
    private ChoiceQuestion question;

    private static final String ARG_SURVEY_ID = "ARG_SURVEY_ID";
    private static final String ARG_TYPE = "ARG_TYPE";

    public ChoiceQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param surveyID surveyID.
     * @param type choice type.
     * @return A new instance of fragment ChoiceQuestionFragment.
     */
    public static ChoiceQuestionFragment newInstance(String surveyID, String type) {
        ChoiceQuestionFragment fragment = new ChoiceQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SURVEY_ID, surveyID);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionArgs bundle of question's details.
     * @return A new instance of fragment ChoiceQuestionFragment.
     */
    public static ChoiceQuestionFragment newInstance(Bundle questionArgs) {
        ChoiceQuestionFragment fragment = new ChoiceQuestionFragment();
        fragment.setArguments(questionArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        QuestionTypeManager.init(requireContext());
        if (args != null) {
            if (args.getString("questionID") == null) { // new question
                surveyID = getArguments().getString(ARG_SURVEY_ID);
                strType = getArguments().getString(ARG_TYPE);
                if(strType != null)
                    questionTypeEnum = QuestionTypeManager.getKeyByValue(strType);
            }
            else {
                surveyID = args.getString("surveyID");
                questionTypeEnum = QuestionTypeEnum.valueOf(args.getString("questionType"));
                strType = QuestionTypeManager.getValueByKey(questionTypeEnum);
                if (questionTypeEnum.isSingleSelection()) {
                    question = new SingleChoiceQuestion(args.getString("questionTitle"), questionTypeEnum)
                            .setChoices(args.getStringArrayList("choices"));
                } else {
                    selectedMaxSelectionsAllowed = args.getInt("allowedSelectionNum");
                    question = new MultipleChoiceQuestion(args.getString("questionTitle"))
                            .setAllowedSelectionNum(selectedMaxSelectionsAllowed)
                            .setChoices(args.getStringArrayList("choices"));
                }
                question.setOther(args.getBoolean("other"))
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
        binding = FragmentChoiceQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();
        initView();
        loadQuestionDetails(question);
        return root;
    }

    private void createBinding() {
        choiceQuestion_TIL_question = binding.choiceQuestionTILQuestion;
        choiceQuestion_TXT_question = binding.choiceQuestionTXTQuestion;
        choiceQuestion_SW_mandatory = binding.choiceQuestionSWMandatory;
        choiceQuestion_SW_other = binding.choiceQuestionSWOther;
        choiceQuestion_DD_maxAllowed = binding.choiceQuestionDDMaxAllowed;
        choiceQuestion_LL_maxAllowed = binding.choiceQuestionLLMaxAllowed;
        choiceQuestion_RV_choices = binding.choiceQuestionRVChoices;
        choiceQuestion_LBL_ErrorRV = binding.choiceQuestionLBLErrorRV;
        choiceQuestion_BTN_save = binding.choiceQuestionBTNSave;
        choiceQuestion_BTN_cancel = binding.choiceQuestionBTNCancel;
    }

    private void initView(){
        choiceQuestion_SW_mandatory.setOnClickListener(v -> choiceQuestion_RV_choices.clearFocus());
        choiceQuestion_SW_other.setOnClickListener(v -> choiceQuestion_RV_choices.clearFocus());

        // save & cancel buttons
        choiceQuestion_BTN_cancel.setOnClickListener(v -> {
            choiceQuestion_RV_choices.clearFocus();
            cancel();
        });
        choiceQuestion_BTN_save.setOnClickListener(v -> {
            choiceQuestion_RV_choices.clearFocus();
            save();
        });
    }

    private void initDropDownValues() {
        choiceQuestion_LL_maxAllowed.setVisibility(strType.equals(getString(R.string.multiple_choice)) ? VISIBLE : GONE);
        itemsMaxSelectionsAllowed = new ArrayList<>();
        choiceCount = choicesAdapter != null ? choicesAdapter.getDataList().size() : 1;
        for (int i = 1; i <= choiceCount; i++) {
            itemsMaxSelectionsAllowed.add(i);
        }
        String maxSelections = selectedMaxSelectionsAllowed.toString();
        choiceQuestion_DD_maxAllowed.setText(maxSelections);
        ArrayAdapter<Integer> adapterItems_MaxSelectionsAllowed = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsMaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setAdapter(adapterItems_MaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setOnItemClickListener((adapterView, view, position, id) -> selectedMaxSelectionsAllowed = adapterItems_MaxSelectionsAllowed.getItem(position));

    }

    private void initChoices(ArrayList<String> choices) {
        // choices repeating table
        choiceQuestion_RV_choices.setLayoutManager(new LinearLayoutManager(requireActivity()));
        boolean isYesNo = questionTypeEnum == QuestionTypeEnum.YES_NO;
        if(choices == null){
            choices = new ArrayList<>();
            if(isYesNo){
                choices.add(requireContext().getString(R.string.yes));
                choices.add(requireContext().getString(R.string.no));
            }
        }

        choicesAdapter = new ChoicesAdapter(this,choices,isYesNo);
        choiceQuestion_RV_choices.setAdapter(choicesAdapter);
    }

    @Override
    public void onRowCountChanged(int count) {
        choiceCount = count;
        if (itemsMaxSelectionsAllowed != null) {
            itemsMaxSelectionsAllowed.clear();
            if (count == 0 || questionTypeEnum == QuestionTypeEnum.SINGLE_CHOICE) {
                itemsMaxSelectionsAllowed.add(1);
            } else {
                for (int i = 1; i <= choiceCount; i++) {
                    itemsMaxSelectionsAllowed.add(i);
                }
            }
        }
    }

    private void loadQuestionDetails(ChoiceQuestion q){
        if(q == null) {
            initChoices(null);
            return;
        }
        questionTypeEnum = q.getType();
        choiceQuestion_TXT_question.setText(q.getQuestionTitle());
        initChoices(q.getChoices());
        choiceQuestion_SW_mandatory.setChecked(q.isMandatory());
        choiceQuestion_SW_other.setChecked(q.isOther());

        if(!questionTypeEnum.isSingleSelection()) {
            initDropDownValues(); // max selection allowed dropdown
        }
    }

    private void save(){
        if (isValid()) {
            choiceQuestion_TIL_question.setError(null);
            choiceQuestion_LBL_ErrorRV.setVisibility(GONE);

            String questionTitle = choiceQuestion_TXT_question.getText().toString().trim();
            boolean mandatory = choiceQuestion_SW_mandatory.isChecked();
            ArrayList<String> theChoices = choicesAdapter.getDataList();
            boolean other = choiceQuestion_SW_other.isChecked();

            if(question == null){
                switch (questionTypeEnum) {
                    case SINGLE_CHOICE:
                        question = new SingleChoiceQuestion(questionTitle, QuestionTypeEnum.SINGLE_CHOICE, theChoices, other);
                        break;
                    case DROPDOWN:
                        question = new SingleChoiceQuestion(questionTitle, QuestionTypeEnum.DROPDOWN, theChoices, other);
                        break;
                    case YES_NO:
                        question = new SingleChoiceQuestion(questionTitle, QuestionTypeEnum.YES_NO, theChoices, other);
                        break;
                    default:
                        question = new MultipleChoiceQuestion(questionTitle, theChoices, other)
                                .setAllowedSelectionNum(selectedMaxSelectionsAllowed);
                        break;
                }
                question.setQuestionID(UUID.randomUUID().toString())
                        .setSurveyID(surveyID)
                        .setMandatory(mandatory);
            }
            else{
                if(!questionTypeEnum.isSingleSelection())
                    ((MultipleChoiceQuestion)question).setAllowedSelectionNum(selectedMaxSelectionsAllowed);
                question.setChoices(theChoices)
                        .setOther(other)
                        .setQuestionTitle(questionTitle)
                        .setMandatory(mandatory);
            }
            question.save();
            requireActivity().finish();
        }
        else{
            choiceQuestion_TIL_question.setError(choiceQuestion_TXT_question.getText().toString().trim().isEmpty()? getString(R.string.error_required) : null);
            choiceQuestion_LBL_ErrorRV.setVisibility(choicesAdapter.getDataList().isEmpty() ? VISIBLE : GONE);
        }
    }

    private boolean isValid(){
        return choiceQuestion_TXT_question.getText() != null &&
                !choiceQuestion_TXT_question.getText().toString().trim().isEmpty() &&
                !choicesAdapter.getDataList().isEmpty();
    }

    private void cancel(){
        if(hasUnsavedChanges())
            ((EditQuestionActivity) requireActivity()).showCancelConfirmationDialog();
        else
            requireActivity().finish();
    }

    public boolean hasUnsavedChanges() {
        if(choiceQuestion_TXT_question.getText() != null &&
                !choiceQuestion_TXT_question.getText().toString().trim().isEmpty())
            return true;
        else return !choicesAdapter.getDataList().isEmpty() && questionTypeEnum != QuestionTypeEnum.YES_NO;
    }
}
