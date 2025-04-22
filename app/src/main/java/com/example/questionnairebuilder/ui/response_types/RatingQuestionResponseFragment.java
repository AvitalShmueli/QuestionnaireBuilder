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
import android.widget.Toast;

import com.example.questionnairebuilder.QuestionResponseActivity;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.databinding.FragmentRatingQuestionResponseBinding;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

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
    private float selectedRating = 0;

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
                    .setIconResourceId(args.getInt("iconResourceId"))
                    .setMandatory(args.getBoolean("mandatory"))
                    .setQuestionID(args.getString("questionID"))
                    .setSurveyID(args.getString("surveyID"))
                    .setOrder(args.getInt("order"));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRatingQuestionResponseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBinding();

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
            if(question.isMandatory()) {
                responseRatingQuestion_LBL_mandatory.setVisibility(VISIBLE);
                responseRatingQuestion_BTN_skip.setVisibility(GONE);
            }
            else {
                responseRatingQuestion_LBL_mandatory.setVisibility(GONE);
                responseRatingQuestion_BTN_skip.setVisibility(VISIBLE);
            }

            if (((RatingScaleQuestion)question).getIconResourceId() == R.drawable.ic_heart_filled){
                switchRatingBarStyle(customRatingBar, R.drawable.custom_ratingbar_heart_selector);
            }
            else if (((RatingScaleQuestion)question).getIconResourceId() == R.drawable.ic_thumb_up_filled){
                switchRatingBarStyle(customRatingBar, R.drawable.custom_ratingbar_thumb_up_selector);
            } else
                switchRatingBarStyle(customRatingBar, R.drawable.custom_ratingbar_star_selector);

            customRatingBar.setNumStars(((RatingScaleQuestion)question).getRatingScaleLevel());
            customRatingBar.setStepSize(1);
            customRatingBar.setRating(selectedRating);

            customRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    Toast.makeText(requireContext(), "Rated: " + rating, Toast.LENGTH_SHORT).show();
                    selectedRating = rating;
                }
            });


            // listeners
            responseRatingQuestion_BTN_save.setOnClickListener(v -> save());
            responseRatingQuestion_BTN_skip.setOnClickListener(v -> skipQuestion());
        }
    }

    private void switchRatingBarStyle(RatingBar ratingBar, int styleResId) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), styleResId);
        ratingBar.setProgressDrawableTiled(drawable);
    }


    private void skipQuestion() {
        // TODO
        ((QuestionResponseActivity) requireActivity()).skipQuestion();
    }


    private void save() {
       if(isValidResponse()) {
           responseRatingQuestion_LBL_error.setVisibility(GONE);
           // TODO: save to firebase + update question order
       }
       else
           responseRatingQuestion_LBL_error.setVisibility(VISIBLE);
    }

    private boolean isValidResponse() {
        return !question.isMandatory() || selectedRating > 0;
    }


}