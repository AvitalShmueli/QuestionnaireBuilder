package com.example.questionnairebuilder.ui.question_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.adapters.ChoicesAdapter;
import com.example.questionnairebuilder.databinding.FragmentChoiceQuestionBinding;
import com.example.questionnairebuilder.listeners.OnRowCountChangeListener;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionType;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ChoiceQuestionFragment extends Fragment implements OnRowCountChangeListener{

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "ARG_TYPE";

    private String strType;
    private QuestionType questionType;


    public ChoiceQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type choice type.
     * @return A new instance of fragment ChoiceQuestionFragment.
     */
    public static ChoiceQuestionFragment newInstance(String type) {
        ChoiceQuestionFragment fragment = new ChoiceQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuestionTypeManager.init(requireContext());
        if (getArguments() != null) {
            strType = getArguments().getString(ARG_TYPE);
            if(strType != null)
                questionType = QuestionTypeManager.getKeyByValue(strType);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChoiceQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();

        return root;
    }


    private void createBinding() {
        // the question
        choiceQuestion_TIL_question = binding.choiceQuestionTILQuestion;
        choiceQuestion_TXT_question = binding.choiceQuestionTXTQuestion;
        choiceQuestion_SW_mandatory = binding.choiceQuestionSWMandatory;
        choiceQuestion_SW_mandatory.setOnClickListener(v -> choiceQuestion_RV_choices.clearFocus());
        choiceQuestion_SW_other = binding.choiceQuestionSWOther;
        choiceQuestion_SW_other.setOnClickListener(v -> choiceQuestion_RV_choices.clearFocus());

        // max selection allowed dropdown
        choiceQuestion_DD_maxAllowed = binding.choiceQuestionDDMaxAllowed;
        choiceQuestion_LL_maxAllowed = binding.choiceQuestionLLMaxAllowed;
        choiceQuestion_LL_maxAllowed.setVisibility(strType.equals(getString(R.string.multiple_choice)) ? VISIBLE : GONE);
        initDropDownValues();

        // choices repeating table
        choiceQuestion_RV_choices = binding.choiceQuestionRVChoices;
        choiceQuestion_LBL_ErrorRV = binding.choiceQuestionLBLErrorRV;
        choiceQuestion_RV_choices.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ArrayList<String> choices = new ArrayList<>();
        if(questionType == QuestionType.YES_NO){
            choices.add(requireContext().getString(R.string.yes));
            choices.add(requireContext().getString(R.string.no));
        }

        choicesAdapter = new ChoicesAdapter(this,choices);
        choiceQuestion_RV_choices.setAdapter(choicesAdapter);

        // save & cancel buttons
        choiceQuestion_BTN_save = binding.choiceQuestionBTNSave;
        choiceQuestion_BTN_cancel = binding.choiceQuestionBTNCancel;
        choiceQuestion_BTN_cancel.setOnClickListener(v -> requireActivity().finish());
        choiceQuestion_BTN_save.setOnClickListener(v -> {
            choiceQuestion_RV_choices.clearFocus();
            save();
        });
    }

    private void initDropDownValues() {
        itemsMaxSelectionsAllowed = new ArrayList<>();
        itemsMaxSelectionsAllowed.add(1);

        String maxSelections = selectedMaxSelectionsAllowed.toString();
        choiceQuestion_DD_maxAllowed.setText(maxSelections);
        ArrayAdapter<Integer> adapterItems_MaxSelectionsAllowed = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsMaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setAdapter(adapterItems_MaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setOnItemClickListener((adapterView, view, position, id) -> selectedMaxSelectionsAllowed = adapterItems_MaxSelectionsAllowed.getItem(position));
    }


    @Override
    public void onRowCountChanged(int count) {
        choiceCount = count;
        if (itemsMaxSelectionsAllowed != null) {
            itemsMaxSelectionsAllowed.clear();
            if (count == 0 || questionType == QuestionType.SINGLE_CHOICE) {
                itemsMaxSelectionsAllowed.add(1);
            } else {
                for (int i = 1; i <= choiceCount; i++) {
                    itemsMaxSelectionsAllowed.add(i);
                }
            }
        }
    }


    private void loadQuestionDetails(Question q){
        questionType = q.getType();
        if(questionType.isSingleSelection()){
            // TODO: complete
        }
    }


    private void save(){
        if (isValid()) {
            choiceQuestion_TIL_question.setError(null);
            choiceQuestion_LBL_ErrorRV.setVisibility(GONE);

            Question q;
            String questionTitle = choiceQuestion_TXT_question.getText().toString().trim();
            boolean mandatory = choiceQuestion_SW_mandatory.isChecked();
            ArrayList<String> theChoices = choicesAdapter.getDataList();
            boolean other = choiceQuestion_SW_other.isChecked();
            switch (questionType) {
                case SINGLE_CHOICE:
                    q = new SingleChoiceQuestion(questionTitle, QuestionType.SINGLE_CHOICE, theChoices, other);
                    break;
                case DROPDOWN:
                    q = new SingleChoiceQuestion(questionTitle, QuestionType.DROPDOWN, theChoices, other);
                    break;
                case YES_NO:
                    q = new SingleChoiceQuestion(questionTitle, QuestionType.YES_NO, theChoices, other);
                    break;
                default:
                    q = new MultipleChoiceQuestion(questionTitle, theChoices, other)
                            .setAllowedSelectionNum(selectedMaxSelectionsAllowed);
                    Log.d("pttt", "max selections: " + selectedMaxSelectionsAllowed);
                    break;
            }
            q.setMandatory(mandatory);
            Log.d("pttt", "the choices: " + theChoices);
            q.save();
        }
        else{
            choiceQuestion_TIL_question.setError(choiceQuestion_TXT_question.getText().toString().trim().isEmpty()? getString(R.string.error_required) : null);
            choiceQuestion_LBL_ErrorRV.setVisibility(choicesAdapter.getDataList().isEmpty() ? VISIBLE : GONE);
        }
    }

    private boolean isValid(){
        return !choiceQuestion_TXT_question.getText().toString().trim().isEmpty() && !choicesAdapter.getDataList().isEmpty();
    }

}
