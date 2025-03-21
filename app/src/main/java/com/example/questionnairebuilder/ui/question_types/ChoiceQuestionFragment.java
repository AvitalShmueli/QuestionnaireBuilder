package com.example.questionnairebuilder.ui.question_types;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.adapters.ChoicesAdapter;
import com.example.questionnairebuilder.databinding.FragmentChoiceQuestionBinding;
import com.example.questionnairebuilder.listeners.OnRowCountChangeListener;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ChoiceQuestionFragment extends Fragment implements OnRowCountChangeListener{

    private FragmentChoiceQuestionBinding binding;
    private RecyclerView choiceQuestion_RV_choices;
    private AutoCompleteTextView choiceQuestion_DD_maxAllowed;
    private ArrayList<Integer> itemsMaxSelectionsAllowed;
    private Integer selectedMaxSelectionsAllowed = null;
    private ChoicesAdapter choicesAdapter;
    private MaterialButton choiceQuestion_BTN_save;
    private MaterialButton choiceQuestion_BTN_cancel;
    private int choiceCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChoiceQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();

        return root;
    }


    private void createBinding() {
        //max selection allowed dropdown
        choiceQuestion_DD_maxAllowed = binding.choiceQuestionDDMaxAllowed;

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
    }

    private void initDropDownValues() {
        itemsMaxSelectionsAllowed = new ArrayList<>();
        itemsMaxSelectionsAllowed.add(1);

        ArrayAdapter<Integer> adapterItems_MaxSelectionsAllowed = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsMaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setAdapter(adapterItems_MaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedMaxSelectionsAllowed = position;
        });
    }


    @Override
    public void onRowCountChanged(int count) {
        choiceCount = count;
        if(itemsMaxSelectionsAllowed != null) {
            itemsMaxSelectionsAllowed.clear();
            for (int i = 1; i <= choiceCount; i++) {
                itemsMaxSelectionsAllowed.add(i);
            }
        }
        Log.d("pttt","choiceCount: " + choiceCount);
    }
}
