package com.example.questionnairebuilder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.QuestionsAdapter;
import com.example.questionnairebuilder.databinding.ActivityQuestionsBinding;
import com.example.questionnairebuilder.interfaces.Callback_questionSelected;
import com.example.questionnairebuilder.interfaces.ItemMoveCallback;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.models.ChoiceQuestion;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.example.questionnairebuilder.utilities.FirebaseManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuestionsActivity extends AppCompatActivity {
    private ActivityQuestionsBinding binding;
    private MaterialToolbar toolbar;
    private LinearLayout question_LL_add_first_question;
    private FloatingActionButton question_FAB_add;
    private FloatingActionButton question_FAB_add_bottom;
    private Map<QuestionTypeEnum, String> menu;
    private RecyclerView recyclerView;
    private MaterialButton questions_BTN_skip;
    private String surveyID;
    private String surveyTitle;

    private QuestionsAdapter questionAdapter;
    private List<Question> questionList = new ArrayList<>();
    private ListenerRegistration questionsListener;
    private MenuItem editMenuItem;
    private boolean canEdit = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        QuestionTypeManager.init(this);
        menu = QuestionTypeManager.getMenu();

        surveyID = getIntent().getStringExtra("surveyID");
        surveyTitle = getIntent().getStringExtra("survey_title");

        createBinding();
        setupViews();
    }

    private void createBinding(){
        toolbar = binding.topAppBar;
        question_LL_add_first_question = binding.questionLLAddFirstQuestion;
        question_FAB_add = binding.questionFABAdd;
        question_FAB_add_bottom = binding.questionFABAddBottom;
        questions_BTN_skip = binding.questionsBTNSkip;
        recyclerView = binding.recyclerView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        editMenuItem = menu.findItem(R.id.action_done);
        questionAdapter.setReorderEnabled(canEdit);
        editMenuItem.setVisible(canEdit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            Set<Question> changedQuestions = questionAdapter.getQuestionsToUpdate();
            saveToDatabase(changedQuestions);
            changedQuestions.clear(); // Reset for future edits
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveToDatabase(Set<Question> questionsToUpdate) {
        for(Question q : questionsToUpdate){
            q.save();
        }
    }

    private void setupViews() {
        setSupportActionBar(toolbar);
        if (surveyTitle != null) {
            toolbar.setTitle(surveyTitle);
        }
        toolbar.setNavigationOnClickListener(v -> onBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        });

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionsAdapter(questionList);
        questionAdapter.setCallbackQuestionSelected(new Callback_questionSelected() {
            @Override
            public void select(Question question) {
                // TODO: add logic of edit mode
                if(canEdit)
                    changeActivityEditQuestion(question);
                else changeActivityResponse(question);
            }
        });
        recyclerView.setAdapter(questionAdapter);

        // Setup Add Question button
        question_FAB_add.setOnClickListener(this::showQuestionTypeMenu);
        question_FAB_add_bottom.setOnClickListener(this::showQuestionTypeMenu);

        // Skip button (handled later depending on list size)
        questions_BTN_skip.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigateTo", "navigation_my_surveys");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemMoveCallback(questionAdapter));
        touchHelper.attachToRecyclerView(recyclerView);

        questionAdapter.setOnStartDragListener(viewHolder -> {
            touchHelper.startDrag(viewHolder);
        });
    }

    private void onBack() {
        if(questionAdapter.getQuestionsToUpdate().isEmpty())
            finish();
        else showCancelConfirmationDialog();
    }

    private void showQuestionTypeMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.NO_GRAVITY);
        for (Map.Entry<QuestionTypeEnum, String> entry : menu.entrySet()) {
            popupMenu.getMenu().add(entry.getValue());
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedItem;
                if(item.getTitle() != null) {
                    selectedItem = item.getTitle().toString();
                    changeActivityNewQuestion(QuestionTypeManager.getKeyByValue(selectedItem));
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void changeActivityNewQuestion(QuestionTypeEnum type) {
        Intent intent = new Intent(this, EditQuestionActivity.class);
        Bundle args = new Bundle();
        args.putString("questionType", type.toString());
        args.putString("surveyID", surveyID);
        args.putInt("order", questionAdapter.getItemCount() + 1);
        intent.putExtra(EditQuestionActivity.KEY_QUESTION_ARGS,args);
        startActivity(intent);
    }

    private void changeActivityEditQuestion(Question q) {
        Intent intent = new Intent(this, EditQuestionActivity.class);

        String questionOrder = "Q" + q.getOrder();
        intent.putExtra(EditQuestionActivity.KEY_QUESTION_HEADER, questionOrder);

        Bundle args = createQuestionArgsBundle(q);
        intent.putExtra(EditQuestionActivity.KEY_QUESTION_ARGS,args);

        startActivity(intent);
    }

    private void changeActivityResponse(Question q) {
        Intent intent = new Intent(this, QuestionResponseActivity.class);

        String questionOrder = "Q" + q.getOrder();
        intent.putExtra(QuestionResponseActivity.KEY_QUESTION_HEADER, questionOrder);

        Bundle args = createQuestionArgsBundle(q);
        intent.putExtra(QuestionResponseActivity.KEY_QUESTION_ARGS,args);

        startActivity(intent);
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
        if(q instanceof OpenEndedQuestion) {
            args.putBoolean("multipleLineAnswer",((OpenEndedQuestion)q).isMultipleLineAnswer());
        }
        if(q instanceof ChoiceQuestion) {
            args.putStringArrayList("choices", ((ChoiceQuestion) q).getChoices());
            args.putBoolean("other",((ChoiceQuestion)q).isOther());
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        startListeningForQuestions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListeningForQuestions();
    }

    private void startListeningForQuestions() {
        questionsListener = FirebaseManager.getInstance().listenToSurveyQuestions(surveyID, new QuestionsCallback() {
            @Override
            public void onQuestionsLoaded(List<Question> questions) {
                questionList = questions;
                questionAdapter.updateQuestions(questionList);
                int editVisibility = canEdit ? VISIBLE : GONE;
                if (questionList.isEmpty()) {
                    questions_BTN_skip.setVisibility(editVisibility);
                    question_LL_add_first_question.setVisibility(editVisibility);
                    question_FAB_add_bottom.setVisibility(GONE);
                } else {
                    questions_BTN_skip.setVisibility(GONE);
                    question_LL_add_first_question.setVisibility(GONE);
                    question_FAB_add_bottom.setVisibility(editVisibility);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("pttt", "Failed to load questions: " + e.getMessage());
            }
        });
    }

    private void stopListeningForQuestions() {
        if (questionsListener != null) {
            questionsListener.remove();
            questionsListener = null;
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

}