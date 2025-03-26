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
import com.example.questionnairebuilder.models.SingleChoiceQuestion;
import com.example.questionnairebuilder.models.SingleChoiceType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ChoiceQuestionFragment extends Fragment implements OnRowCountChangeListener{

    private FragmentChoiceQuestionBinding binding;
    private TextInputEditText choiceQuestion_TXT_question;
    private RecyclerView choiceQuestion_RV_choices;
    private MaterialSwitch choiceQuestion_SW_mandatory;
    private MaterialSwitch choiceQuestion_SW_other;
    private LinearLayout choiceQuestion_LL_maxAllowed;
    private AutoCompleteTextView choiceQuestion_DD_maxAllowed;
    private MaterialTextView choiceQuestion_LBL_singleChoice;
    private ArrayList<Integer> itemsMaxSelectionsAllowed;
    private Integer selectedMaxSelectionsAllowed = 1;
    private ChoicesAdapter choicesAdapter;
    private MaterialButton choiceQuestion_BTN_save;
    private MaterialButton choiceQuestion_BTN_cancel;
    private int choiceCount = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "ARG_TYPE";

    private String type;


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
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
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
        choiceQuestion_TXT_question = binding.choiceQuestionTXTQuestion;
        choiceQuestion_SW_mandatory = binding.choiceQuestionSWMandatory;
        choiceQuestion_SW_other = binding.choiceQuestionSWOther;

        // max selection allowed dropdown
        choiceQuestion_DD_maxAllowed = binding.choiceQuestionDDMaxAllowed;
        choiceQuestion_LBL_singleChoice = binding.choiceQuestionLBLSingleChoice;
        choiceQuestion_LL_maxAllowed = binding.choiceQuestionLLMaxAllowed;
        choiceQuestion_LL_maxAllowed.setVisibility(type.equals(getString(R.string.multiple_choice)) ? VISIBLE : GONE);
        initDropDownValues();

        // choices repeating table
        choiceQuestion_RV_choices = binding.choiceQuestionRVChoices;
        choiceQuestion_RV_choices.setLayoutManager(new LinearLayoutManager(requireActivity()));
        choicesAdapter = new ChoicesAdapter(this);
        choiceQuestion_RV_choices.setAdapter(choicesAdapter);

        // save & cancel buttons
        choiceQuestion_BTN_save = binding.choiceQuestionBTNSave;
        choiceQuestion_BTN_cancel = binding.choiceQuestionBTNCancel;
        choiceQuestion_BTN_cancel.setOnClickListener(v -> requireActivity().finish());
        choiceQuestion_BTN_save.setOnClickListener(v -> {
            Question q;
            String questionTitle = choiceQuestion_TXT_question.getText().toString().trim();
            ArrayList<String> theChoices = choicesAdapter.getDataList();
            boolean other = choiceQuestion_SW_other.isChecked();
            switch (type){
                case "Single choice":
                    q = new SingleChoiceQuestion(questionTitle,theChoices,other, SingleChoiceType.RADIO);
                    break;
                case "Dropdown":
                    q = new SingleChoiceQuestion(questionTitle,theChoices,other, SingleChoiceType.DROPDOWN);
                    break;
                default:
                    q = new MultipleChoiceQuestion(questionTitle,theChoices,other)
                            .setAllowedSelectionNum(selectedMaxSelectionsAllowed);
                    Log.d("pttt","max selections: "+selectedMaxSelectionsAllowed);
                    break;
            }
            Log.d("pttt","the choices: "+theChoices);
            q.save();
        });
    }

    private void initDropDownValues() {
        itemsMaxSelectionsAllowed = new ArrayList<>();
        itemsMaxSelectionsAllowed.add(1);

        choiceQuestion_DD_maxAllowed.setText(""+selectedMaxSelectionsAllowed);
        ArrayAdapter<Integer> adapterItems_MaxSelectionsAllowed = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsMaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setAdapter(adapterItems_MaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedMaxSelectionsAllowed = adapterItems_MaxSelectionsAllowed.getItem(position);
        });
    }


    @Override
    public void onRowCountChanged(int count) {
        choiceCount = count;
        if (itemsMaxSelectionsAllowed != null) {
            itemsMaxSelectionsAllowed.clear();
            if (count == 0 || type.equals("Single choice")) {
                itemsMaxSelectionsAllowed.add(1);
            } else {
                for (int i = 1; i <= choiceCount; i++) {
                    itemsMaxSelectionsAllowed.add(i);
                }
            }
        }
    }

}
