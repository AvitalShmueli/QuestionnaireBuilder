package com.example.questionnairebuilder.ui.question_types;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.adapters.TableAdapter;
import com.example.questionnairebuilder.databinding.FragmentChoiceQuestionBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChoiceQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChoiceQuestionFragment extends Fragment {

    private FragmentChoiceQuestionBinding binding;
    private RecyclerView choiceQuestion_RV_choices;
    private AutoCompleteTextView choiceQuestion_DD_maxAllowed;
    private ArrayList<Integer> itemsMaxSelectionsAllowed;
    private Integer selectedMaxSelectionsAllowed = null;
    private TableAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChoiceQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChoiceQuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChoiceQuestionFragment newInstance(String param1, String param2) {
        ChoiceQuestionFragment fragment = new ChoiceQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_choice_question, container, false);
        binding = FragmentChoiceQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();
        initDropDownValues();


        return root;
    }


    private void createBinding() {
        choiceQuestion_DD_maxAllowed = binding.choiceQuestionDDMaxAllowed;

        choiceQuestion_RV_choices = binding.choiceQuestionRVChoices;
        choiceQuestion_RV_choices.setLayoutManager(new LinearLayoutManager(requireActivity()));

        adapter = new TableAdapter();
        choiceQuestion_RV_choices.setAdapter(adapter);
    }

    private void initDropDownValues() {
        itemsMaxSelectionsAllowed = new ArrayList<>();
        itemsMaxSelectionsAllowed.add(1);

        ArrayAdapter<Integer> adapterItems_MaxSelectionsAllowed = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, itemsMaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setAdapter(adapterItems_MaxSelectionsAllowed);
        choiceQuestion_DD_maxAllowed.setOnItemClickListener((adapterView, view, position, id) -> {
            //selectedMaxSelectionsAllowed = (int) adapterView.getItemAtPosition(position);
            selectedMaxSelectionsAllowed = position;
        });
        /*
        itemsMaxSelectionsAllowed = new ArrayList<>();
        for (Product.ProductType p : Product.ProductType.values()) {
            itemsMaxSelectionsAllowed.add(p.name());
        }
        Log.d(TAG, "dropdown items (product type) = " + itemsMaxSelectionsAllowed.toString());

        itemsProductCondition = new ArrayList<>();
        for (Product.ProductCondition c : Product.ProductCondition.values()) {
            itemsProductCondition.add(c.name());
        }
        Log.d(TAG, "dropdown items (product condition) = " + itemsProductCondition.toString());
        */
    }

}
