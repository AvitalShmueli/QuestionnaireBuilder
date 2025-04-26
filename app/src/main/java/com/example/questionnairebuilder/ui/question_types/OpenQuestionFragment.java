package com.example.questionnairebuilder.ui.question_types;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.questionnairebuilder.EditQuestionActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.databinding.FragmentOpenQuestionBinding;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpenQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenQuestionFragment extends Fragment implements UnsavedChangesHandler {

    private FragmentOpenQuestionBinding binding;
    private TextInputLayout openQuestion_TIL_question;
    private TextInputEditText openQuestion_TXT_question;
    private MaterialSwitch openQuestion_SW_mandatory;
    private MaterialButton openQuestion_BTN_save;
    private MaterialButton openQuestion_BTN_cancel;
    private String surveyID;

    private static final String ARG_SURVEY_ID = "ARG_SURVEY_ID";

    public OpenQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param surveyID surveyID.
     * @return A new instance of fragment OpenQuestionFragment.
     */
    public static OpenQuestionFragment newInstance(String surveyID) {
        OpenQuestionFragment fragment = new OpenQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SURVEY_ID, surveyID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            surveyID = getArguments().getString(ARG_SURVEY_ID);
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

        binding = FragmentOpenQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();

        return root;
    }

    private void createBinding() {
        openQuestion_BTN_save = binding.openQuestionBTNSave;
        openQuestion_BTN_cancel = binding.openQuestionBTNCancel;
        openQuestion_TIL_question = binding.openQuestionTILQuestion;
        openQuestion_TXT_question = binding.openQuestionTXTQuestion;
        openQuestion_SW_mandatory = binding.openQuestionSWMandatory;

        openQuestion_BTN_cancel.setOnClickListener(v -> cancel());
        openQuestion_BTN_save.setOnClickListener(v -> save());
    }

    private void loadQuestionDetails(Question q){
        //TODO
    }

    private void save(){
        {
            String questionTitle = null;
            if (!isValid())
                openQuestion_TIL_question.setError(getString(R.string.error_required));
            else {
                openQuestion_TIL_question.setError(null);
                if(openQuestion_TXT_question.getText() != null)
                    questionTitle = openQuestion_TXT_question.getText().toString().trim();
                boolean mandatory = openQuestion_SW_mandatory.isChecked();
                Question q = new OpenEndedQuestion(questionTitle)
                        .setQuestionID(UUID.randomUUID().toString())
                        .setSurveyID(surveyID)
                        .setMandatory(mandatory);
                q.save();
                requireActivity().finish();
            }
        }
    }

    private boolean isValid(){
        return openQuestion_TXT_question.getText() != null &&
                !openQuestion_TXT_question.getText().toString().trim().isEmpty();
    }

    private void cancel(){
        if(hasUnsavedChanges())
            ((EditQuestionActivity) requireActivity()).showCancelConfirmationDialog();
        else
            requireActivity().finish();
    }

    @Override
    public boolean hasUnsavedChanges() {
        return openQuestion_TXT_question.getText() != null &&
                !openQuestion_TXT_question.getText().toString().trim().isEmpty();
    }
}