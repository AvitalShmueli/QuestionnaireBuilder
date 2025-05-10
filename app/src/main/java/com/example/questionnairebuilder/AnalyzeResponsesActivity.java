package com.example.questionnairebuilder;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.AnalyzeAdapter;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.listeners.OnAnalysisCompleteListener;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.example.questionnairebuilder.utilities.VertexAiManager;
import com.google.android.material.appbar.MaterialToolbar;

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
        FirestoreManager.getInstance().listenToSurveyQuestions(surveyID, new QuestionsCallback() {
            @Override
            public void onQuestionsLoaded(List<Question> questionList) {
                questions.clear();
                questions.addAll(questionList);
                adapter.notifyDataSetChanged();
/*
                for (int i = 0; i < questions.size(); i++) {
                    Question q = questions.get(i);
                    if (q instanceof OpenEndedQuestion) {
                        int index = i;
                        VertexAiManager.getInstance().analyzeOpenAnswer(q.getQuestionID(), new OnAnalysisCompleteListener() {
                            @Override
                            public void onAnalysisComplete(String result) {
                                OpenEndedQuestion openQ = (OpenEndedQuestion) questions.get(index);
                                openQ.setAnalysisResult(result);
                                adapter.notifyItemChanged(index);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Analyze", "AI analysis failed", e);
                            }
                        });
                    }
                }*/
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AnalyzeResponsesActivity.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}