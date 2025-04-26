package com.example.questionnairebuilder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.QuestionsAdapter;
import com.example.questionnairebuilder.databinding.ActivityQuestionsBinding;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.example.questionnairebuilder.utilities.FirebaseManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {

    private ActivityQuestionsBinding binding;
    private FloatingActionButton question_FAB_add;
    private Map<QuestionTypeEnum, String> menu;
    private RecyclerView recyclerView;
    private MaterialButton questions_BTN_skip;
    private String surveyID;

    private QuestionsAdapter questionAdapter;
    private List<Question> questionList = new ArrayList<>();
    private ListenerRegistration questionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        QuestionTypeManager.init(this);
        menu = QuestionTypeManager.getMenu();

        surveyID = getIntent().getStringExtra("surveyID");

        setupViews();
    }

    private void setupViews() {
        question_FAB_add = binding.questionFABAdd;
        questions_BTN_skip = binding.questionsBTNSkip;
        recyclerView = binding.recyclerView;

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionsAdapter(questionList);
        recyclerView.setAdapter(questionAdapter);

        // Setup Add Question button
        question_FAB_add.setOnClickListener(this::showQuestionTypeMenu);

        // Skip button (handled later depending on list size)
        questions_BTN_skip.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigateTo", "navigation_my_surveys");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showQuestionTypeMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.NO_GRAVITY);
        for (Map.Entry<QuestionTypeEnum, String> entry : menu.entrySet()) {
            popupMenu.getMenu().add(entry.getValue());
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedItem = item.getTitle().toString();
                changeActivity(QuestionTypeManager.getKeyByValue(selectedItem));
                return true;
            }
        });

        popupMenu.show();
    }

    private void changeActivity(QuestionTypeEnum type) {
        Intent intent = new Intent(this, EditQuestionActivity.class);
        intent.putExtra(EditQuestionActivity.KEY_TYPE, type.toString());
        intent.putExtra("surveyID", surveyID);
        startActivity(intent);
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

                if (questionList.isEmpty()) {
                    questions_BTN_skip.setVisibility(VISIBLE);
                } else {
                    questions_BTN_skip.setVisibility(GONE);
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

}