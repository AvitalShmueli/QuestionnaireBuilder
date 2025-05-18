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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.QuestionsAdapter;
import com.example.questionnairebuilder.databinding.ActivityQuestionsBinding;
import com.example.questionnairebuilder.interfaces.ItemMoveCallback;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.interfaces.ResponsesCallback;
import com.example.questionnairebuilder.models.ChoiceQuestion;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class QuestionsActivity extends AppCompatActivity {
    public static final String KEY_EDIT_MODE = "KEY_EDIT_MODE";

    private ActivityQuestionsBinding binding;
    private MaterialToolbar toolbar;
    private LinearLayout question_LL_add_first_question;
    private FloatingActionButton question_FAB_add;
    private FloatingActionButton question_FAB_add_bottom;
    private Map<QuestionTypeEnum, String> menu;
    private RecyclerView recyclerView;
    private MaterialButton questions_BTN_skip;
    private ExtendedFloatingActionButton questions_FAB_start;
    private String surveyID;
    private String surveyTitle;

    private QuestionsAdapter questionsAdapter;
    private List<Question> questionsList = new ArrayList<>();
    private ListenerRegistration questionsListener;
    private boolean canEdit;
    private int answeredCount = 0;
    private int totalCount = 0;
    public static List<Question> cachedQuestionList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent previousIntent = getIntent();
        canEdit = previousIntent.getBooleanExtra(KEY_EDIT_MODE,false);

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
        questions_FAB_start = binding.questionsFABStart;
        recyclerView = binding.recyclerView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_done, menu);
        MenuItem doneMenuItem = menu.findItem(R.id.action_done);
        questionsAdapter.setReorderEnabled(canEdit);
        doneMenuItem.setVisible(canEdit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if(questionsAdapter.hasUnsavedChanges()) {
                Set<Question> changedQuestions = questionsAdapter.getQuestionsToUpdate();
                saveToDatabase(changedQuestions);
                changedQuestions.clear(); // Reset for future edits
                cachedQuestionList = null;
                FirestoreManager.getInstance().fixQuestionOrder(surveyID);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToDatabase(Set<Question> questionsToUpdate) {
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
        questionsAdapter = new QuestionsAdapter(questionsList);
        questionsAdapter.setCallbackQuestionSelected(question -> {
            if(canEdit)
                changeActivityEditQuestion(question);
            else changeActivityResponse(question);
        });
        recyclerView.setAdapter(questionsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 && questions_FAB_start.isExtended()) {
                    questions_FAB_start.shrink();
                } else if (dy < -10 && !questions_FAB_start.isExtended()) {
                    questions_FAB_start.extend();
                }
            }
        });


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

        if (canEdit) {
            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemMoveCallback(questionsAdapter));
            touchHelper.attachToRecyclerView(recyclerView);

            questionsAdapter.setOnStartDragListener(viewHolder -> {
                touchHelper.startDrag(viewHolder);
            });
        }
        else{
            questions_FAB_start.setOnClickListener(v -> {
                Question nextQuestion = questionsAdapter.findFirstUnansweredQuestion();
                if (nextQuestion != null) {
                    changeActivityResponse(nextQuestion); // you already have this method
                }
            });
        }
    }

    private void onBack() {
        if(questionsAdapter.getQuestionsToUpdate().isEmpty())
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
        args.putInt("order", questionsAdapter.getItemCount() + 1);
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
        if(cachedQuestionList != null){
            FirestoreManager.getInstance().getSurveyQuestionsOnce(surveyID, new QuestionsCallback() {
                @Override
                public void onQuestionsLoaded(List<Question> questions) {
                    mergeLocalAndRemoteQuestionsWithDeletion(questions);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("pttt", "Failed to load questions: " + e.getMessage());
                }
            });
        }
        else {
            startListeningForQuestions();
        }
        if(!canEdit)
            fetchUserResponses();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListeningForQuestions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cachedQuestionList = null;
    }

    private void startListeningForQuestions() {
        questionsListener = FirestoreManager.getInstance().listenToSurveyQuestions(surveyID, new QuestionsCallback() {
            @Override
            public void onQuestionsLoaded(List<Question> questions) {
                //questionList = questions;
                //questionAdapter.updateQuestions(questionList);
                if (cachedQuestionList != null) {
                    questionsList = cachedQuestionList;
                } else {
                    questionsList = questions;
                }
                questionsAdapter.updateQuestions(questionsList);
                totalCount = questionsAdapter.getItemCount();
                int editVisibility = canEdit ? VISIBLE : GONE;
                if (questionsList.isEmpty()) {
                    questions_BTN_skip.setVisibility(editVisibility);
                    question_LL_add_first_question.setVisibility(editVisibility);
                    question_FAB_add_bottom.setVisibility(GONE);
                    questions_FAB_start.setVisibility(GONE);
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
                .setTitle(R.string.discard_reordering_title)
                .setMessage(R.string.discard_reordering_msg)
                .setPositiveButton(R.string.continue_btn, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    /**
     * merge between new questions, updated questions and deleted questions
     * @param remoteQuestions - fetched question from Firestore
     */
    private void mergeLocalAndRemoteQuestionsWithDeletion(List<Question> remoteQuestions) {
        Map<String, Question> remoteMap = new HashMap<>();
        for (Question q : remoteQuestions) {
            remoteMap.put(q.getQuestionID(), q);
        }

        List<Question> updatedList = new ArrayList<>();

        for (Question local : cachedQuestionList) {
            Question remote = remoteMap.get(local.getQuestionID());

            if (remote != null) {
                // Question still exists — update its fields and preserve order
                local.setQuestionTitle(remote.getQuestionTitle());
                local.setImage(remote.getImage());
                local.setMandatory(remote.isMandatory());
                updatedList.add(local);
            }
            // else - deleted question, do NOT add it to updatedList
        }

        // Add new questions (in Firestore but not in local)
        for (Question remote : remoteQuestions) {
            boolean found = false;
            for (Question local : updatedList) {
                if (local.getQuestionID().equals(remote.getQuestionID())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Assign order at end of list
                remote.setOrder(updatedList.size() + 1);
                updatedList.add(remote);
            }
        }

        cachedQuestionList = updatedList;
        questionsList = updatedList;
        questionsAdapter.updateQuestions(updatedList);
        totalCount = questionsAdapter.getItemCount();
    }

    private void fetchUserResponses() {
        String userId = AuthenticationManager.getInstance().getCurrentUser().getUid();
        FirestoreManager.getInstance().getUserResponsesForSurvey(surveyID, userId, new ResponsesCallback() {
            @Override
            public void onResponsesLoaded(Set<String> answeredQuestionIds) {
                answeredCount = answeredQuestionIds.size();
                String questionsProgress = getString(R.string.survey_responses_subtitle, Objects.requireNonNullElse(answeredCount,0), Objects.requireNonNullElse(totalCount,0));
                runOnUiThread(() -> {
                    questionsAdapter.setAnsweredQuestionIds(answeredQuestionIds);
                    toolbar.setSubtitle(questionsProgress);
                    if (answeredCount == 0) {
                        questions_FAB_start.setText(getString(R.string.start_survey));
                        questions_FAB_start.setIconResource(R.drawable.ic_start);
                    } else {
                        questions_FAB_start.setText(getString(R.string.continue_survey));
                        questions_FAB_start.setIconResource(R.drawable.ic_resume);
                    }
                    questions_FAB_start.setVisibility(questionsList.isEmpty() || answeredCount == totalCount ? GONE : VISIBLE);
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("QuestionsActivity", "Failed to load user responses", e);
            }
        });
    }
}