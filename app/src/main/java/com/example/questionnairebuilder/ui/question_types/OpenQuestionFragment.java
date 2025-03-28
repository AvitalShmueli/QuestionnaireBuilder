package com.example.questionnairebuilder.ui.question_types;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.questionnairebuilder.databinding.FragmentOpenQuestionBinding;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpenQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenQuestionFragment extends Fragment {

    private FragmentOpenQuestionBinding binding;
    private TextInputEditText openQuestion_TXT_question;
    private MaterialSwitch openQuestion_SW_mandatory;
    private MaterialButton openQuestion_BTN_save;
    private MaterialButton openQuestion_BTN_cancel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OpenQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OpenQuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OpenQuestionFragment newInstance(String param1, String param2) {
        OpenQuestionFragment fragment = new OpenQuestionFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentOpenQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();

        return root;
    }

    private void createBinding() {
        openQuestion_BTN_save = binding.openQuestionBTNSave;
        openQuestion_BTN_cancel = binding.openQuestionBTNCancel;
        openQuestion_TXT_question = binding.openQuestionTXTQuestion;
        openQuestion_SW_mandatory = binding.openQuestionSWMandatory;

        openQuestion_BTN_cancel.setOnClickListener(v -> requireActivity().finish());
        openQuestion_BTN_save.setOnClickListener(v -> save());
    }

    private void save(){
        {
            String questionTitle = openQuestion_TXT_question.getText().toString().trim();
            boolean mandatory = openQuestion_SW_mandatory.isChecked();
            Question q = new OpenEndedQuestion(questionTitle).setMandatory(mandatory);
            q.save();
        }
    }
}