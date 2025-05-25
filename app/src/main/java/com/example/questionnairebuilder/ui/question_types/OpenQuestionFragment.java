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

import com.example.questionnairebuilder.EditQuestionActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.databinding.FragmentOpenQuestionBinding;
import com.example.questionnairebuilder.interfaces.SaveHandler;
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
public class OpenQuestionFragment extends Fragment implements UnsavedChangesHandler, SaveHandler {

    private FragmentOpenQuestionBinding binding;
    private TextInputLayout openQuestion_TIL_question;
    private TextInputEditText openQuestion_TXT_question;
    private MaterialSwitch openQuestion_SW_mandatory;
    private MaterialButton openQuestion_BTN_save;
    private MaterialButton openQuestion_BTN_cancel;
    private MaterialButton openQuestion_BTN_delete;
    private String surveyID;
    private OpenEndedQuestion question;
    private int currentQuestionOrder;

    public OpenQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionArgs bundle of question's details.
     * @return A new instance of fragment OpenQuestionFragment.
     */
    public static OpenQuestionFragment newInstance(Bundle questionArgs) {
        OpenQuestionFragment fragment = new OpenQuestionFragment();
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
                question = (OpenEndedQuestion) new OpenEndedQuestion(args.getString("questionTitle"))
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

        binding = FragmentOpenQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();
        initView();
        loadQuestionDetails(question);

        return root;
    }

    private void createBinding() {
        openQuestion_BTN_save = binding.openQuestionBTNSave;
        openQuestion_BTN_cancel = binding.openQuestionBTNCancel;
        openQuestion_BTN_delete = binding.openQuestionBTNDelete;
        openQuestion_TIL_question = binding.openQuestionTILQuestion;
        openQuestion_TXT_question = binding.openQuestionTXTQuestion;
        openQuestion_SW_mandatory = binding.openQuestionSWMandatory;
    }

    private void initView(){
        openQuestion_BTN_cancel.setOnClickListener(v -> cancel());
        openQuestion_BTN_save.setOnClickListener(v -> save());
        if(question != null) {
            openQuestion_BTN_delete.setVisibility(VISIBLE);
            openQuestion_BTN_delete.setOnClickListener(v -> delete());
        }
        else  openQuestion_BTN_delete.setVisibility(GONE);
    }

    private void loadQuestionDetails(Question q){
        if(q == null) {
            return;
        }

        openQuestion_TXT_question.setText(q.getQuestionTitle());
        openQuestion_SW_mandatory.setChecked(q.isMandatory());
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
                if(question == null) {
                    question = (OpenEndedQuestion) new OpenEndedQuestion(questionTitle)
                            .setQuestionID(UUID.randomUUID().toString())
                            .setSurveyID(surveyID)
                            .setMandatory(mandatory)
                            .setOrder(currentQuestionOrder);
                }
                else{
                    question.setQuestionTitle(questionTitle)
                            .setMandatory(mandatory);
                }
                question.save();
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

    private void delete(){
        ((EditQuestionActivity) requireActivity()).showDeleteConfirmationDialog(question);
    }

    @Override
    public boolean hasUnsavedChanges() {
        String currentText = openQuestion_TXT_question.getText() != null
                ? openQuestion_TXT_question.getText().toString().trim()
                : "";
        boolean currentMandatory = openQuestion_SW_mandatory.isChecked();

        String originalText = question != null ? question.getQuestionTitle() : "";
        boolean originalMandatory = question != null && question.isMandatory();

        boolean textChanged = !currentText.equals(originalText);
        boolean mandatoryChanged = currentMandatory != originalMandatory;

        return textChanged || mandatoryChanged;
    }

    @Override
    public void onSaveClicked() {
        save();
    }
}