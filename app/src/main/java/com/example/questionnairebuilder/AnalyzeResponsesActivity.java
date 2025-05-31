package com.example.questionnairebuilder;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.AnalyzeAdapter;
import com.example.questionnairebuilder.interfaces.AllResponsesCallback;
import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeResponsesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private AnalyzeAdapter adapter;
    private List<Question> questions = new ArrayList<>();
    private String surveyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_responses);

        surveyID = getIntent().getStringExtra("surveyID");
        toolbar = findViewById(R.id.topAppBar);
        recyclerView = findViewById(R.id.analyze_recycler);

        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new AnalyzeAdapter(questions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadQuestions();
    }

    private void loadQuestions() {
        FirestoreManager.getInstance().getSurveyQuestionsOnce(surveyID, new QuestionsCallback() {
            @Override
            public void onQuestionsLoaded(List<Question> questionList) {
                questions.clear();
                questions.addAll(questionList);

                // Now load responses after questions are ready
                FirestoreManager.getInstance().getAllResponsesForSurvey(surveyID, new AllResponsesCallback() {
                    @Override
                    public void onResponsesLoaded(List<DocumentSnapshot> documents) {
                        for (DocumentSnapshot doc : documents) {
                            String questionID = doc.getString("questionID");
                            List<Object> rawValues = (List<Object>) doc.get("responseValues");
                            List<String> values = new ArrayList<>();

                            if (rawValues != null) {
                                for (Object val : rawValues) {
                                    values.add(String.valueOf(val));
                                }
                            }

                            for (Question question : questions) {
                                if (question instanceof AnalyzableQuestion && question.getQuestionID().equals(questionID)) {
                                    ((AnalyzableQuestion) question).accumulateAnswers(values);
                                    break;
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AnalyzeResponsesActivity.this, "Failed to load responses", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AnalyzeResponsesActivity.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }


}