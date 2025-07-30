
package com.example.questionnairebuilder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import static com.example.questionnairebuilder.QuestionResponseActivity.KEY_SURVEY_RESPONSE_STATUS;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.QuestionsAdapter;
import com.example.questionnairebuilder.databinding.ActivityQuestionsBinding;
import com.example.questionnairebuilder.interfaces.ItemMoveCallback;
import com.example.questionnairebuilder.interfaces.OneSurveyCallback;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.interfaces.ResponsesCallback;
import com.example.questionnairebuilder.listeners.OnSurveyResponseStatusListener;
import com.example.questionnairebuilder.models.ChoiceQuestion;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.utilities.QuestionTypeManager;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionsActivity extends AppCompatActivity {
    public static final String KEY_EDIT_MODE = "KEY_EDIT_MODE";

    private ActivityQuestionsBinding binding;
    private MaterialToolbar toolbar;
    private LinearLayout question_LL_add_first_question;
    private FloatingActionButton question_FAB_add;
    private ExtendedFloatingActionButton question_FAB_add_bottom;
    private Map<QuestionTypeEnum, String> menu;
    private RecyclerView recyclerView;
    private View scrollHintBottom;
    private View scrollHintTop;
    private MaterialButton questions_BTN_skip;
    private ExtendedFloatingActionButton questions_FAB_start;
    private MaterialButton questions_BTN_complete;
    private MaterialTextView questions_LBL_completed;
    private String surveyID;
    private String surveyTitle;
    private Survey.SurveyStatus currentSurveyStatus = Survey.SurveyStatus.Draft;
    private String currentUserId;
    private QuestionsAdapter questionsAdapter;
    private List<Question> questionsList = new ArrayList<>();
    private ListenerRegistration questionsListener;
    private boolean canEdit;
    private int answeredCount = 0;
    private int answeredMandatoryCount = 0;
    private int totalCount = 0;
    private int totalMandatoryCount = 0;
    private SurveyResponseStatus surveyResponseStatus = null;
    public static List<Question> cachedQuestionList = null;
    private boolean launchedFromLink = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent intent = getIntent();
        Uri data = intent.getData();
        launchedFromLink = intent.getBooleanExtra("launched_from_link", false);
        if (data != null && data.getPath() != null && data.getPath().contains("/survey")) {
            surveyID = data.getQueryParameter("id");
            launchedFromLink = true; // deep link
        } else {
            surveyID = intent.getStringExtra("surveyID");
        }

        try {
            currentUserId = AuthenticationManager.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
            welcomeIntent.putExtra("surveyID", surveyID);
            welcomeIntent.putExtra("launched_from_link", true);
            startActivity(welcomeIntent);
            finish();
            return;
        }

        if (surveyID == null) {
            Toast.makeText(this, "Survey ID not provided.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        surveyTitle = intent.getStringExtra("survey_title");
        if (surveyTitle == null) {
            FirestoreManager.getInstance().getSurveyById(surveyID, new OneSurveyCallback() {
                @Override
                public void onSurveyLoaded(Survey survey) {
                    if (survey != null) {
                        surveyTitle = survey.getSurveyTitle();
                        currentSurveyStatus = survey.getStatus();
                        toolbar.setTitle(surveyTitle);
                    } else {
                        Log.e("QuestionsActivity", "Survey not found for ID: " + surveyID);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e("QuestionsActivity", "Failed to fetch survey", e);
                }
            });
        }

        canEdit = intent.getBooleanExtra(KEY_EDIT_MODE, false);

        if (!canEdit) {
            maybeAddPendingStatus(surveyID);
            //currentSurveyStatus = Survey.SurveyStatus.valueOf(intent.getStringExtra("survey_status"));
        }

        QuestionTypeManager.init(this);
        menu = QuestionTypeManager.getMenu();

        FirestoreManager.getInstance().getSurveyResponseStatus(
                surveyID,
                currentUserId,
                new OnSurveyResponseStatusListener() {
                    @Override
                    public void onSuccess(SurveyResponseStatus status) {
                        Log.d("Survey", "Status: " + status.getStatus());
                        surveyResponseStatus = status;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Survey", "Failed to get response status", e);
                    }
                }
        );

        createBinding();
        setupViews();
    }


    private void createBinding() {
        toolbar = binding.topAppBar;
        question_LL_add_first_question = binding.questionLLAddFirstQuestion;
        question_FAB_add = binding.questionFABAdd;
        question_FAB_add_bottom = binding.questionFABAddBottom;
        questions_BTN_skip = binding.questionsBTNSkip;
        questions_FAB_start = binding.questionsFABStart;
        questions_BTN_complete = binding.questionsBTNComplete;
        questions_LBL_completed = binding.questionsLBLCompleted;
        recyclerView = binding.recyclerView;
        scrollHintBottom = binding.scrollHintBottom;
        scrollHintTop = binding.scrollHintTop;
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
            if (questionsAdapter.hasUnsavedChanges()) {
                Set<Question> changedQuestions = questionsAdapter.getQuestionsToUpdate();

                // Only save questions that still exist in the current list
                Set<String> currentQuestionIds = questionsList.stream()
                        .map(Question::getQuestionID)
                        .collect(Collectors.toSet());

                Set<Question> questionsToSave = changedQuestions.stream()
                        .filter(q -> currentQuestionIds.contains(q.getQuestionID()))
                        .collect(Collectors.toSet());

                saveToDatabase(questionsToSave);
                changedQuestions.clear(); // Reset for future edits
                cachedQuestionList = null;
                FirestoreManager.getInstance().fixQuestionOrder(surveyID);
            }

            Intent intent = new Intent(this, SurveyManagementActivity.class);
            intent.putExtra("surveyID", surveyID);
            intent.putExtra("survey_title", surveyTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToDatabase(Set<Question> questionsToUpdate) {
        for (Question q : questionsToUpdate) {
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
            if (canEdit)
                changeActivityEditQuestion(question);
            else {
                if (currentSurveyStatus == Survey.SurveyStatus.Close) {
                    Toast.makeText(getApplicationContext(), "Sorry, the survey is already closed\nThank you for participating", LENGTH_SHORT).show();
                    return;
                }
                changeActivityResponse(question);
            }
        });
        recyclerView.setAdapter(questionsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (questions_FAB_start.getVisibility() == VISIBLE) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 10 && questions_FAB_start.isExtended()) {
                        questions_FAB_start.shrink();
                    } else if (dy < -10 && !questions_FAB_start.isExtended()) {
                        questions_FAB_start.extend();
                    }
                }

                if (question_FAB_add_bottom.getVisibility() == VISIBLE) {
                    if (dy > 10 && question_FAB_add_bottom.isExtended()) {
                        question_FAB_add_bottom.shrink();
                    } else if (dy < -10 && !question_FAB_add_bottom.isExtended()) {
                        question_FAB_add_bottom.extend();
                    }
                }
            }
        });


        // Setup Add Question button
        question_FAB_add.setOnClickListener(this::showQuestionTypeMenu);
        question_FAB_add_bottom.setOnClickListener(this::showQuestionTypeMenu);

        // Skip button (handled later depending on list size)
        questions_BTN_skip.setOnClickListener(v -> {
            Intent intent = new Intent(this, SurveyManagementActivity.class);
            intent.putExtra("surveyID", surveyID);
            intent.putExtra("survey_title", surveyTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        if (canEdit) {
            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemMoveCallback(questionsAdapter));
            touchHelper.attachToRecyclerView(recyclerView);

            questionsAdapter.setOnStartDragListener(viewHolder -> {
                touchHelper.startDrag(viewHolder);
            });
        } else {
            questions_BTN_complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirestoreManager.getInstance().updateSurveyResponseStatus(
                            surveyID, currentUserId, SurveyResponseStatus.ResponseStatus.COMPLETED,
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("pttt", "Status updated to completed");
                                    Toast.makeText(getApplicationContext(), getString(R.string.thank_you), LENGTH_SHORT).show();
                                    if (launchedFromLink) {
                                        Intent exploreIntent = new Intent(getApplicationContext(), MainActivity.class);
                                        exploreIntent.putExtra("navigate_to_explore", true);
                                        exploreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(exploreIntent);
                                        finish();
                                    } else {
                                        finish();
                                    }
                                }
                            }, e -> {
                            }
                    );
                }
            });

            questions_FAB_start.setOnClickListener(v -> {
                if (currentSurveyStatus == Survey.SurveyStatus.Close) {
                    Toast.makeText(getApplicationContext(), "Sorry, the survey is already closed\nThank you for participating", LENGTH_SHORT).show();
                    return;
                }
                Question nextQuestion = questionsAdapter.findFirstUnansweredQuestion();
                if (nextQuestion != null) {
                    changeActivityResponse(nextQuestion); // you already have this method
                }

                if (questions_FAB_start.getText().equals(getString(R.string.start_survey))) {
                    FirestoreManager.getInstance().updateSurveyResponseStatus(
                            surveyID, currentUserId, SurveyResponseStatus.ResponseStatus.IN_PROGRESS,
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Survey", "Status updated to in progress");
                                }
                            }, e -> {
                                Log.d("pttt Survey", "ERROR while updating status");
                            }
                    );
                }
            });
        }

        initScrollHint();
    }

    private void initScrollHint() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                updateScrollHints();
            }
        });

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this::updateScrollHints);
    }

    private void updateScrollHints() {
        boolean canScrollUp = recyclerView.canScrollVertically(-1); // up
        boolean canScrollDown = recyclerView.canScrollVertically(1); // down

        scrollHintTop.setVisibility(canScrollUp ? View.VISIBLE : View.GONE);
        scrollHintBottom.setVisibility(canScrollDown ? View.VISIBLE : View.GONE);
    }

    private void onBack() {
        if (questionsAdapter.getQuestionsToUpdate().isEmpty()) {
            if (launchedFromLink) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("navigate_to_explore", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        } else {
            showCancelConfirmationDialog();
        }
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
                if (item.getTitle() != null) {
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
        intent.putExtra(EditQuestionActivity.KEY_QUESTION_ARGS, args);
        startActivity(intent);
    }

    private void changeActivityEditQuestion(Question q) {
        Intent intent = new Intent(this, EditQuestionActivity.class);

        String questionOrder = "Q" + q.getOrder();
        intent.putExtra(EditQuestionActivity.KEY_QUESTION_HEADER, questionOrder);

        Bundle args = createQuestionArgsBundle(q);
        intent.putExtra(EditQuestionActivity.KEY_QUESTION_ARGS, args);

        startActivity(intent);
    }

    private void changeActivityResponse(Question q) {
        Intent intent = new Intent(this, QuestionResponseActivity.class);

        String questionOrder = "Q" + q.getOrder();
        intent.putExtra(QuestionResponseActivity.KEY_TOTAL_QUESTIONS, totalCount);
        intent.putExtra(QuestionResponseActivity.KEY_QUESTION_HEADER, questionOrder);

        if (surveyResponseStatus != null)
            intent.putExtra(KEY_SURVEY_RESPONSE_STATUS, surveyResponseStatus.getStatus().name());

        Bundle args = createQuestionArgsBundle(q);
        intent.putExtra(QuestionResponseActivity.KEY_QUESTION_ARGS, args);

        startActivity(intent);
    }

    private Bundle createQuestionArgsBundle(Question q) {
        Bundle args = new Bundle();
        args.putString("questionTitle", q.getQuestionTitle());
        args.putString("questionID", q.getQuestionID());
        args.putString("surveyID", q.getSurveyID());
        args.putString("questionType", q.getType().toString());
        args.putBoolean("mandatory", q.isMandatory());
        args.putInt("order", q.getOrder());
        args.putString("image", q.getImage());
        if (q instanceof OpenEndedQuestion) {
            args.putBoolean("multipleLineAnswer", ((OpenEndedQuestion) q).isMultipleLineAnswer());
        }
        if (q instanceof ChoiceQuestion) {
            args.putStringArrayList("choices", ((ChoiceQuestion) q).getChoices());
            args.putBoolean("other", ((ChoiceQuestion) q).isOther());
        }
        if (q instanceof MultipleChoiceQuestion)
            args.putInt("allowedSelectionNum", ((MultipleChoiceQuestion) q).getAllowedSelectionNum());
        if (q instanceof DateQuestion)
            args.putString("dateSelectionMode", ((DateQuestion) q).getDateMode().name());
        if (q instanceof RatingScaleQuestion) {
            args.putInt("ratingScaleLevel", ((RatingScaleQuestion) q).getRatingScaleLevel());
            args.putString("iconResourceName", ((RatingScaleQuestion) q).getIconResourceName());
        }
        return args;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cachedQuestionList != null) {
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
        } else {
            startListeningForQuestions();
        }
        if (!canEdit)
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
                totalMandatoryCount = questionsAdapter.getMandatoryCount();
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
     *
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
        totalMandatoryCount = questionsAdapter.getMandatoryCount();
    }

    private void fetchUserResponses() {
        FirestoreManager.getInstance().getUserResponsesForSurvey(surveyID, currentUserId, new ResponsesCallback() {
            @Override
            public void onResponsesLoaded(Map<String, Boolean> answeredQuestions) {
                answeredCount = answeredQuestions.size();
                answeredMandatoryCount = (int) answeredQuestions.values().stream()
                        .filter(Boolean.TRUE::equals)
                        .count();
                String questionsProgress = getString(R.string.survey_responses_subtitle, Objects.requireNonNullElse(answeredCount, 0), Objects.requireNonNullElse(totalCount, 0));
                runOnUiThread(() -> {
                    questionsAdapter.setAnsweredQuestionIds(answeredQuestions.keySet());
                    toolbar.setSubtitle(questionsProgress);
                    if (answeredCount == 0) {
                        questions_FAB_start.setText(getString(R.string.start_survey));
                        questions_FAB_start.setIconResource(R.drawable.ic_start);
                    } else {
                        if (answeredMandatoryCount == totalMandatoryCount /*answeredCount == totalCount || (answeredCount >= totalMandatoryCount && answeredCount > 0)*/) {
                            if (surveyResponseStatus == null || surveyResponseStatus.getStatus() != SurveyResponseStatus.ResponseStatus.COMPLETED) {
                                questions_BTN_complete.setVisibility(VISIBLE);
                                questions_LBL_completed.setVisibility(GONE);
                            } else {
                                questions_BTN_complete.setVisibility(GONE);
                                questions_LBL_completed.setText(formatDateTime(surveyResponseStatus.getCompletedAt()));
                                questions_LBL_completed.setVisibility(VISIBLE);
                            }
                            adjustRecyclerViewPadding();
                        } else {
                            questions_FAB_start.setText(getString(R.string.continue_survey));
                            questions_FAB_start.setIconResource(R.drawable.ic_resume);
                            questions_BTN_complete.setVisibility(GONE);
                            questions_LBL_completed.setVisibility(GONE);
                        }
                    }
                    if (questionsList.isEmpty() || (answeredMandatoryCount == totalMandatoryCount && answeredCount > 0)/*answeredCount == totalCount*/)
                        questions_FAB_start.setVisibility(GONE);
                    else
                        questions_FAB_start.setVisibility(VISIBLE);
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("QuestionsActivity", "Failed to load user responses", e);
            }
        });
    }

    private void maybeAddPendingStatus(String surveyId) {
        String userId = AuthenticationManager.getInstance().getCurrentUser().getUid();
        String docId = surveyId + "_" + userId;

        FirestoreManager.getInstance().getSurveyResponseStatus(surveyId, userId, new OnSurveyResponseStatusListener() {
            @Override
            public void onSuccess(SurveyResponseStatus status) {
                // Do nothing - status already exists
            }

            @Override
            public void onFailure(Exception e) {
                // Only create if document doesn't exist
                SurveyResponseStatus newStatus = new SurveyResponseStatus();
                newStatus.setSurveyId(surveyId);
                newStatus.setUserId(userId);
                newStatus.setStatus(SurveyResponseStatus.ResponseStatus.PENDING);
                newStatus.setStartedAt(new Date());
                newStatus.setCompletedAt(null);

                FirestoreManager.getInstance()
                        .createSurveyResponseStatus(newStatus)
                        .addOnSuccessListener(aVoid -> Log.d("Survey", "Pending status created"))
                        .addOnFailureListener(ex -> Log.e("Survey", "Failed to create pending status", ex));
            }
        });
    }


    private void adjustRecyclerViewPadding() {
        if (questions_BTN_complete.getVisibility() == View.VISIBLE) {
            int bottomPadding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    72,
                    getResources().getDisplayMetrics());
            recyclerView.setPadding(
                    recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    recyclerView.getPaddingRight(),
                    bottomPadding);
        } else {
            recyclerView.setPadding(
                    recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    recyclerView.getPaddingRight(),
                    0);
        }
    }

    private String formatDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String datePart = dateFormat.format(date);
        String timePart = timeFormat.format(date);
        return getString(R.string.survey_submitted, Objects.requireNonNullElse(datePart, ""), Objects.requireNonNullElse(timePart, ""));
    }
}