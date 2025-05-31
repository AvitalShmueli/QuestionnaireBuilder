package com.example.questionnairebuilder;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.questionnairebuilder.databinding.ActivityQuestionResponseBinding;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.interfaces.UnsavedChangesHandler;
import com.example.questionnairebuilder.models.ChoiceQuestion;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.DateSelectionModeEnum;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.example.questionnairebuilder.models.Response;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;
import com.example.questionnairebuilder.ui.response_types.ChoiceQuestionResponseFragment;
import com.example.questionnairebuilder.ui.response_types.DateQuestionResponseFragment;
import com.example.questionnairebuilder.ui.response_types.OpenQuestionResponseFragment;
import com.example.questionnairebuilder.ui.response_types.RatingQuestionResponseFragment;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuestionResponseActivity extends AppCompatActivity {
    public static final String KEY_QUESTION_HEADER = "KEY_QUESTION_HEADER";
    public static final String KEY_QUESTION_ARGS = "KEY_QUESTION_ARGS";
    public static final String KEY_SURVEY_COMPLETED = "KEY_SURVEY_COMPLETED";

    private ActivityQuestionResponseBinding binding;
    private OpenQuestionResponseFragment openQuestionResponseFragment;
    private ChoiceQuestionResponseFragment choiceQuestionResponseFragment;
    private DateQuestionResponseFragment dateQuestionResponseFragment;
    private RatingQuestionResponseFragment ratingQuestionResponseFragment;

    private int currentQuestionOrder = -1;
    private String surveyID = null;
    boolean surveyCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQuestionResponseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            QuestionTypeManager.init(this);

            Intent previousIntent = getIntent();
            String title = previousIntent.getStringExtra(KEY_QUESTION_HEADER);
            surveyCompleted = previousIntent.getBooleanExtra(KEY_SURVEY_COMPLETED,false);
            Bundle args = previousIntent.getBundleExtra(KEY_QUESTION_ARGS);
            if (args != null) {
                String type = args.getString("questionType");
                QuestionTypeEnum selectedType = QuestionTypeEnum.valueOf(type);
                type = QuestionTypeManager.getValueByKey(selectedType);

                currentQuestionOrder = args.getInt("order");
                surveyID = args.getString("surveyID");

                initView(title);

                if (type != null) {
                    loadQuestionFragment(selectedType, args);
                }
            }
        }
    }


    private void initView(String title){
        MaterialToolbar myToolbar = binding.topAppBar;
        setSupportActionBar(myToolbar);
        setTitle(title);

        // listeners
        myToolbar.setNavigationOnClickListener(v -> onBack());
    }

    private void setTitle(String title){
        if (getSupportActionBar() != null) {
            if (title == null)
                getSupportActionBar().setTitle(R.string.new_question);
            else
                getSupportActionBar().setTitle(title);
        }
    }

    private void loadQuestionFragment(QuestionTypeEnum selectedType, Bundle args){
        switch (selectedType) {
            case OPEN_ENDED_QUESTION:
                openQuestionResponseFragment = OpenQuestionResponseFragment.newInstance(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.questionResponse_FRAME_question,openQuestionResponseFragment)
                        .commit();
                break;
            case SINGLE_CHOICE:
            case DROPDOWN:
            case YES_NO:
            case MULTIPLE_CHOICE:
                choiceQuestionResponseFragment = ChoiceQuestionResponseFragment.newInstance(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.questionResponse_FRAME_question,choiceQuestionResponseFragment)
                        .commit();
                break;
            case DATE:
                dateQuestionResponseFragment = DateQuestionResponseFragment.newInstance(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.questionResponse_FRAME_question,dateQuestionResponseFragment)
                        .commit();
                break;
            case RATING_SCALE:
                ratingQuestionResponseFragment = RatingQuestionResponseFragment.newInstance(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.questionResponse_FRAME_question,ratingQuestionResponseFragment)
                        .commit();
                break;
        }
    }


    private void onBack(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.editQuestion_FRAME_question);
        if (fragment instanceof UnsavedChangesHandler) {
            if (((UnsavedChangesHandler) fragment).hasUnsavedChanges()) {
                showCancelConfirmationDialog();
            } else {
                finish();
            }
        } else {
            finish(); // If the fragment doesn't care about unsaved changes
        }
    }


    public void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.discard_changes_title)
                .setMessage(R.string.discard_changes_msg)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }


    public void skipQuestion(){
        FirestoreManager.getInstance().listenToSurveyQuestions(surveyID, new QuestionsCallback() {
            @Override
            public void onQuestionsLoaded(List<Question> questions) {
                if (currentQuestionOrder < questions.size() - 1) {
                    Question q = questions.get(currentQuestionOrder++);
                    String questionOrder = "Q" + currentQuestionOrder;
                    setTitle(questionOrder);
                    loadQuestionFragment(q.getType(),createQuestionArgsBundle(q));
                } else {
                    finish(); // or show a "done" screen
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }

    public void saveResponse(Response response){
        String userID = AuthenticationManager.getInstance().getCurrentUser().getUid();
        response.setUserID(userID);
        response.setModified(new Timestamp(new Date()));
        response.save();
        skipQuestion();
    }

    private Bundle createQuestionArgsBundle(Question q){
        Bundle args = new Bundle();
        args.putString("questionTitle",q.getQuestionTitle());
        args.putString("questionID",q.getQuestionID());
        args.putString("surveyID",q.getSurveyID());
        args.putString("questionType", q.getType().toString());
        args.putBoolean("mandatory",q.isMandatory());
        args.putInt("order",q.getOrder());
        args.putString("image",q.getImage());
        if(q instanceof OpenEndedQuestion)
            args.putBoolean("multipleLineAnswer",((OpenEndedQuestion)q).isMultipleLineAnswer());
        if(q instanceof ChoiceQuestion)
            args.putStringArrayList("choices",((ChoiceQuestion)q).getChoices());
        if(q instanceof MultipleChoiceQuestion)
            args.putInt("allowedSelectionNum",((MultipleChoiceQuestion)q).getAllowedSelectionNum());
        if(q instanceof DateQuestion)
            args.putString("dateSelectionMode",((DateQuestion)q).getDateMode().name());
        if(q instanceof RatingScaleQuestion) {
            args.putInt("ratingScaleLevel", ((RatingScaleQuestion) q).getRatingScaleLevel());
            args.putInt("iconResourceId", ((RatingScaleQuestion) q).getIconResourceId());
        }
        return args;
    }

    // TODO: remove
    private List<Question> questionListDemo(){
        List<Question> list = new ArrayList<>();
        list.add(new OpenEndedQuestion("Q1 - What is your name?").setMandatory(true));
        list.add(new OpenEndedQuestion("Q2 - How old are you?"));
        list.add(new DateQuestion("Q3? - Birth date").setDateMode(DateSelectionModeEnum.SINGLE_DATE).setMandatory(true));
        list.add(new DateQuestion("Q4? - Vacation dates").setDateMode(DateSelectionModeEnum.DATE_RANGE).setMandatory(false));
        list.add(new SingleChoiceQuestion("Q5? Yes No Mandatory Question?", QuestionTypeEnum.YES_NO)
                .addChoice("Yes")
                .addChoice("No")
                .setMandatory(true)
        );
        list.add(new RatingScaleQuestion("Q6? Rating question")
                .setIconResourceId(R.drawable.ic_heart_filled)
                .setRatingScaleLevel(3)
                .setMandatory(true)
        );
        list.add(new OpenEndedQuestion("Q7?"));
        list.add(new OpenEndedQuestion("Q8?"));
        list.add(new OpenEndedQuestion("Q9?"));
        list.add(new OpenEndedQuestion("Q10?"));
        list.add(new OpenEndedQuestion("Q11?"));
        list.add(new OpenEndedQuestion("Q12?"));
        list.add(new MultipleChoiceQuestion("Q13? Multi selection")
                .setAllowedSelectionNum(2)
                .addChoice("Option A")
                .addChoice("Option B")
                .addChoice("Option C")
                .addChoice("Option D")
                .setMandatory(true)
        );
        list.add(new SingleChoiceQuestion("Q14?", QuestionTypeEnum.DROPDOWN));
        list.add(new SingleChoiceQuestion("Q15? Yes No Question?", QuestionTypeEnum.YES_NO)
                .addChoice("Yes")
                .addChoice("No")
        );
        return list;
    }

    public boolean isSurveyCompleted(){
        return surveyCompleted;
    }
}