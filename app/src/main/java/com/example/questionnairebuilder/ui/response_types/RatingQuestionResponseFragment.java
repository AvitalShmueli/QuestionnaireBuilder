package com.example.questionnairebuilder.ui.response_types;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.example.questionnairebuilder.QuestionResponseActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.databinding.FragmentRatingQuestionResponseBinding;
import com.example.questionnairebuilder.interfaces.OneResponseCallback;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.example.questionnairebuilder.models.Response;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingQuestionResponseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingQuestionResponseFragment extends Fragment {
    private FragmentRatingQuestionResponseBinding binding;
    private MaterialTextView responseRatingQuestion_LBL_question;
    private MaterialTextView responseRatingQuestion_LBL_mandatory;
    private RatingBar customRatingBar;
    private MaterialTextView responseRatingQuestion_LBL_error;
    private MaterialButton responseRatingQuestion_BTN_save;
    private MaterialButton responseRatingQuestion_BTN_skip;
    private Question question;
    private Response response;
    private float selectedRating = 0;
    private boolean isSurveyCompleted;

    public RatingQuestionResponseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionArgs bundle of question's details.
     * @return A new instance of fragment RatingQuestionResponseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingQuestionResponseFragment newInstance(Bundle questionArgs) {
        RatingQuestionResponseFragment fragment = new RatingQuestionResponseFragment();
        fragment.setArguments(questionArgs);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            question = new RatingScaleQuestion(args.getString("questionTitle"))
                    .setRatingScaleLevel(args.getInt("ratingScaleLevel"))
                    .setIconResourceName(args.getString("iconResourceName"))
                    .setMandatory(args.getBoolean("mandatory"))
                    .setQuestionID(args.getString("questionID"))
                    .setSurveyID(args.getString("surveyID"))
                    .setOrder(args.getInt("order"));
        }
        isSurveyCompleted = ((QuestionResponseActivity) requireActivity()).isSurveyResponseCompleted();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRatingQuestionResponseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();
        loadResponse(question);

        return root;
    }

    private void createBinding() {
        responseRatingQuestion_LBL_question = binding.responseRatingQuestionLBLQuestion;
        responseRatingQuestion_LBL_mandatory = binding.responseRatingQuestionLBLMandatory;
        customRatingBar = binding.customRatingBar;
        responseRatingQuestion_LBL_error = binding.responseRatingQuestionLBLError;
        responseRatingQuestion_BTN_save = binding.responseRatingQuestionBTNSave;
        responseRatingQuestion_BTN_skip = binding.responseRatingQuestionBTNSkip;

        if(question != null){
            responseRatingQuestion_LBL_question.setText(question.getQuestionTitle());
            customRatingBar.setEnabled(!isSurveyCompleted);

            if (isSurveyCompleted) {
                responseRatingQuestion_BTN_skip.setVisibility(GONE);
                responseRatingQuestion_BTN_save.setVisibility(GONE);
            }
            else {
                if (question.isMandatory()) {
                    responseRatingQuestion_LBL_mandatory.setVisibility(VISIBLE);
                    responseRatingQuestion_BTN_skip.setVisibility(GONE);
                } else {
                    responseRatingQuestion_LBL_mandatory.setVisibility(GONE);
                    responseRatingQuestion_BTN_skip.setVisibility(VISIBLE);
                }

                // listeners
                responseRatingQuestion_BTN_save.setOnClickListener(v -> save());
                responseRatingQuestion_BTN_skip.setOnClickListener(v -> skipQuestion());

                customRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        selectedRating = rating;
                    }
                });
            }

            if (((RatingScaleQuestion)question).getIconResourceName().equals("ic_heart_filled")) {
                switchRatingBarStyle(customRatingBar, R.drawable.custom_ratingbar_heart_selector);
            }
            else if (((RatingScaleQuestion)question).getIconResourceName().equals("ic_thumb_up_filled")) {
                switchRatingBarStyle(customRatingBar, R.drawable.custom_ratingbar_thumb_up_selector);
            } else
                switchRatingBarStyle(customRatingBar, R.drawable.custom_ratingbar_star_selector);

            customRatingBar.setNumStars(((RatingScaleQuestion)question).getRatingScaleLevel());
            customRatingBar.setStepSize(1);
        }
    }

    private void loadResponse(Question question) {
        String userID = AuthenticationManager.getInstance().getCurrentUser().getUid();
        FirestoreManager.getInstance().getResponse(question.getSurveyID(), question.getQuestionID(), userID, new OneResponseCallback() {
            @Override
            public void onResponseLoad(Response theResponse) {
                if(theResponse != null) {
                    response = theResponse;
                    selectedRating = Float.parseFloat(response.getResponseValues().get(0));
                    customRatingBar.setRating(selectedRating);
                }
            }

            @Override
            public void onResponseLoadFailure() {
                response = null;
            }
        });
    }

    private void switchRatingBarStyle(RatingBar ratingBar, int styleResId) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), styleResId);
        ratingBar.setProgressDrawableTiled(drawable);
    }

    private void skipQuestion() {
        ((QuestionResponseActivity) requireActivity()).skipQuestion();
    }

    private void save() {
       if(isValidResponse()) {
           responseRatingQuestion_LBL_error.setVisibility(GONE);
           if(response == null) {
               response = new Response()
                       .setResponseID(UUID.randomUUID().toString())
                       .setSurveyID(question.getSurveyID())
                       .setQuestionID(question.getQuestionID())
                       .setMandatory(question.isMandatory())
                       .addResponse(String.valueOf(selectedRating));
           }
           else {
               response.getResponseValues().clear();
               response.addResponse(String.valueOf(selectedRating));
           }
           ((QuestionResponseActivity)requireActivity()).saveResponse(response);
       }
       else
           responseRatingQuestion_LBL_error.setVisibility(VISIBLE);
    }

    private boolean isValidResponse() {
        return !question.isMandatory() || selectedRating > 0;
    }
}