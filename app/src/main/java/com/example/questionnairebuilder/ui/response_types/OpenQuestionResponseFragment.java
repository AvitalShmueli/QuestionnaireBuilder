package com.example.questionnairebuilder.ui.response_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.databinding.FragmentOpenQuestionResponseBinding;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpenQuestionResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenQuestionResponseFragment extends Fragment {
    private FragmentOpenQuestionResponseBinding binding;
    private MaterialTextView responseOpenQuestion_LBL_question;
    private MaterialTextView responseOpenQuestion_LBL_mandatory;
    private MaterialButton responseOpenQuestion_BTN_save;
    private MaterialButton responseOpenQuestion_BTN_skip;
    private TextInputLayout responseOpenQuestion_TIL_answer;
    private TextInputEditText responseOpenQuestion_TXT_answer;
    private Question question;


    public OpenQuestionResponseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionArgs bundle of question's details.
     * @return A new instance of fragment OpenQuestionResponseFragment.
     */
    public static OpenQuestionResponseFragment newInstance(Bundle questionArgs) {
        OpenQuestionResponseFragment fragment = new OpenQuestionResponseFragment();
        fragment.setArguments(questionArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            question = new OpenEndedQuestion(args.getString("questionTitle"))
                    .setMandatory(args.getBoolean("mandatory"))
                    .setQuestionID(args.getString("questionID"))
                    .setSurveyID(args.getString("surveyID"))
                    .setOrder(args.getInt("order"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOpenQuestionResponseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();

        return root;
    }

    private void createBinding() {
        responseOpenQuestion_LBL_question = binding.responseOpenQuestionLBLQuestion;
        responseOpenQuestion_LBL_mandatory = binding.responseOpenQuestionLBLMandatory;
        responseOpenQuestion_BTN_save = binding.responseOpenQuestionBTNSave;
        responseOpenQuestion_BTN_skip = binding.responseOpenQuestionBTNSkip;
        responseOpenQuestion_TIL_answer = binding.responseOpenQuestionTILAnswer;
        responseOpenQuestion_TXT_answer = binding.responseOpenQuestionTXTAnswer;

        if(question != null){
            responseOpenQuestion_LBL_question.setText(question.getQuestionTitle());
            if(question.isMandatory()) {
                responseOpenQuestion_LBL_mandatory.setVisibility(VISIBLE);
                responseOpenQuestion_BTN_skip.setVisibility(GONE);
            }
            else {
                responseOpenQuestion_LBL_mandatory.setVisibility(GONE);
                responseOpenQuestion_BTN_skip.setVisibility(VISIBLE);
            }

            /*
            if(((OpenEndedQuestion)question).isMultipleLineAnswer()) {
                responseOpenQuestion_TXT_answer.setMinLines(5);
                responseOpenQuestion_TXT_answer.setMaxLines(5);
            }
            else {
                responseOpenQuestion_TXT_answer.setMinLines(1);
                responseOpenQuestion_TXT_answer.setMaxLines(1);
            }
            */

            // listeners
            responseOpenQuestion_BTN_save.setOnClickListener(v -> save());
            responseOpenQuestion_BTN_skip.setOnClickListener(v -> skipQuestion());
        }
    }

    private void skipQuestion() {
        // TODO
    }

    private void save() {
        if (!isValidResponse()) {
            responseOpenQuestion_TIL_answer.setError(getString(R.string.error_required));
        }
        else{
            responseOpenQuestion_TIL_answer.setError(null);
            // TODO
        }
    }

    private boolean isValidResponse() {
        return !question.isMandatory() ||
                (responseOpenQuestion_TXT_answer.getText() != null && !responseOpenQuestion_TXT_answer.getText().toString().isEmpty());
    }
}