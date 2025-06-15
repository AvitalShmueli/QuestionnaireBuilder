package com.example.questionnairebuilder.ui.response_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.questionnairebuilder.QuestionResponseActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.databinding.FragmentDateQuestionResponseBinding;
import com.example.questionnairebuilder.interfaces.OneResponseCallback;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.DateSelectionModeEnum;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.Response;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.DatePickerHelper;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateQuestionResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateQuestionResponseFragment extends Fragment implements UnsavedChangesHandler {
    private FragmentDateQuestionResponseBinding binding;
    private MaterialTextView responseDateQuestion_LBL_question;
    private MaterialTextView responseDateQuestion_LBL_mandatory;
    private TextInputLayout startDateLayout;
    private TextInputEditText startDateTXT;
    private TextInputLayout endDateLayout;
    private TextInputEditText endDateTXT;
    private LinearLayout responseDateQuestion_LL_buttons;
    private MaterialButton responseDateQuestion_BTN_save;
    private MaterialButton responseDateQuestion_BTN_skip;
    private LinearLayout response_LL_navigationButtons;
    private ExtendedFloatingActionButton response_BTN_previous;
    private ExtendedFloatingActionButton response_BTN_next;
    private Question question;
    private Response response;
    private boolean isSurveyCompleted;
    private DatePickerHelper startDatePicker;
    private DatePickerHelper endDatePicker;

    public DateQuestionResponseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *@param questionArgs bundle of question's details.
     * @return A new instance of fragment DateQuestionResponseFragment.
     */
    public static DateQuestionResponseFragment newInstance(Bundle questionArgs) {
        DateQuestionResponseFragment fragment = new DateQuestionResponseFragment();
        fragment.setArguments(questionArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            question = new DateQuestion(args.getString("questionTitle"))
                    .setDateMode(DateSelectionModeEnum.valueOf(args.getString("dateSelectionMode")))
                    .setMandatory(args.getBoolean("mandatory"))
                    .setQuestionID(args.getString("questionID"))
                    .setSurveyID(args.getString("surveyID"))
                    .setOrder(args.getInt("order"));
        }
        isSurveyCompleted = ((QuestionResponseActivity) requireActivity()).isSurveyResponseCompleted();

        requireActivity().getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (hasUnsavedChanges()) {
                            // Call the dialog from the activity
                            ((QuestionResponseActivity) requireActivity()).showCancelConfirmationDialog();
                        } else {
                            requireActivity().finish();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDateQuestionResponseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();
        loadResponse(question);

        return root;
    }

    private void createBinding() {
        responseDateQuestion_LBL_question = binding.responseDateQuestionLBLQuestion;
        responseDateQuestion_LBL_mandatory = binding.responseDateQuestionLBLMandatory;
        startDateLayout = binding.responseDateQuestionTILDate;
        startDateTXT = binding.responseDateQuestionTXTDate;
        endDateLayout = binding.responseDateQuestionTILDate2;
        endDateTXT = binding.responseDateQuestionTXTDate2;
        responseDateQuestion_LL_buttons = binding.responseDateQuestionLLButtons;
        responseDateQuestion_BTN_save = binding.responseDateQuestionBTNSave;
        responseDateQuestion_BTN_skip = binding.responseDateQuestionBTNSkip;
        response_LL_navigationButtons = binding.responseLLNavigationButtons;
        response_BTN_previous = binding.responseBTNPrevious;
        response_BTN_next = binding.responseBTNNext;

        if(question != null){
            responseDateQuestion_LBL_question.setText(question.getQuestionTitle());

            if (isSurveyCompleted) {
                responseDateQuestion_LL_buttons.setVisibility(GONE);
                response_LL_navigationButtons.setVisibility(VISIBLE);
                boolean hasNext = ((QuestionResponseActivity) requireActivity()).hasNext();
                boolean hasPrevious = ((QuestionResponseActivity) requireActivity()).hasPrevious();
                response_BTN_next.setVisibility(hasNext ? VISIBLE : GONE);
                response_BTN_previous.setVisibility(hasPrevious ? VISIBLE : GONE);
                response_BTN_next.setOnClickListener(v -> skipQuestion());
                response_BTN_previous.setOnClickListener(v -> previousQuestion());
            }
            else {
                responseDateQuestion_LL_buttons.setVisibility(VISIBLE);
                response_LL_navigationButtons.setVisibility(GONE);

                if (question.isMandatory()) {
                    responseDateQuestion_LBL_mandatory.setVisibility(VISIBLE);
                    responseDateQuestion_BTN_skip.setVisibility(GONE);
                } else {
                    responseDateQuestion_LBL_mandatory.setVisibility(GONE);
                    responseDateQuestion_BTN_skip.setVisibility(VISIBLE);
                }

                responseDateQuestion_BTN_save.setOnClickListener(v -> save());
                responseDateQuestion_BTN_skip.setOnClickListener(v -> skipQuestion());
            }

            setupDateFieldBehavior();

        }
    }

    private void loadResponse(Question question) {
        String userID = AuthenticationManager.getInstance().getCurrentUser().getUid();
        FirestoreManager.getInstance().getResponse(question.getSurveyID(), question.getQuestionID(), userID, new OneResponseCallback() {
            @Override
            public void onResponseLoad(Response theResponse) {
                if(theResponse != null) {
                    response = theResponse;
                    if (!response.getResponseValues().isEmpty()) {
                        if(((DateQuestion)question).getDateMode() == DateSelectionModeEnum.DATE_RANGE && response.getResponseValues().size() == 2){
                            startDateTXT.setText(response.getResponseValues().get(0));
                            endDateTXT.setText(response.getResponseValues().get(1));
                        }
                        else{
                            startDateTXT.setText(response.getResponseValues().get(0));
                        }
                    }
                }
            }

            @Override
            public void onResponseLoadFailure() {
                response = null;
            }
        });
    }

    private void setupDateFieldBehavior() {
        startDateLayout.setEnabled(!isSurveyCompleted);
        endDateLayout.setEnabled(!isSurveyCompleted);
        startDatePicker = new DatePickerHelper(requireActivity(),requireActivity().getSupportFragmentManager(), startDateLayout, startDateTXT);
        if(((DateQuestion)question).getDateMode() == DateSelectionModeEnum.DATE_RANGE) {
            endDateLayout.setVisibility(VISIBLE);
            startDateLayout.setHint(getString(R.string.start_date));
            endDateLayout.setHint(getString(R.string.end_date));
            endDatePicker = new DatePickerHelper(requireActivity(), requireActivity().getSupportFragmentManager(), endDateLayout, endDateTXT);
        } else{
            endDateLayout.setVisibility(GONE);
        }

    }

    private void skipQuestion() {
        ((QuestionResponseActivity) requireActivity()).skipQuestion();
    }

    private void previousQuestion() {
        ((QuestionResponseActivity) requireActivity()).previousQuestion();
    }

    private void save() {
        if (isValidResponse()) {
            ArrayList<String> selectedDates = new ArrayList<>();
            selectedDates.add(startDateTXT.getText().toString());
            if(((DateQuestion)question).getDateMode() == DateSelectionModeEnum.DATE_RANGE)
                selectedDates.add(endDateTXT.getText().toString());

            if(response == null) {
                response = new Response()
                        .setResponseID(UUID.randomUUID().toString())
                        .setSurveyID(question.getSurveyID())
                        .setQuestionID(question.getQuestionID())
                        .setMandatory(question.isMandatory())
                        .setResponseValues(selectedDates);
            }
            else {
                response.setResponseValues(selectedDates);
            }
            ((QuestionResponseActivity)requireActivity()).saveResponse(response);
        }
    }

    private boolean isValidResponse() {
        Date start = startDatePicker.getSelectedDate();
        Date end = endDatePicker.getSelectedDate();

        boolean valid = true;

        if (start == null || !startDatePicker.isSelectedDateValid()) {
            startDateLayout.setError("Start date is invalid");
            valid = false;
        } else {
            startDateLayout.setError(null);
        }

        if (end == null || !endDatePicker.isSelectedDateValid()) {
            endDateLayout.setError("End date is invalid");
            valid = false;
        } else if (start != null && end.before(start)) {
            endDateLayout.setError("End date cannot be before start date");
            valid = false;
        } else {
            endDateLayout.setError(null);
        }

        return valid;
    }

    public boolean hasUnsavedChanges() {
        ArrayList<String> selectedDates = new ArrayList<>();
        String date1 = startDateTXT.getText().toString();
        if(!date1.isEmpty())
            selectedDates.add(date1);
        if(((DateQuestion)question).getDateMode() == DateSelectionModeEnum.DATE_RANGE) {
            String date2 = endDateTXT.getText().toString();
            if(!date2.isEmpty())
                selectedDates.add(date2);
        }

        List<String> originalResponse =  response != null ? response.getResponseValues() : Collections.emptyList();
        return !new HashSet<>(selectedDates).equals(new HashSet<>(originalResponse));
    }
}