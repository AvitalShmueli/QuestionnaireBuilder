package com.example.questionnairebuilder;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.AnalyzeAdapter;
import com.example.questionnairebuilder.interfaces.AllResponsesCallback;
import com.example.questionnairebuilder.interfaces.AnalyzableQuestion;
import com.example.questionnairebuilder.interfaces.QuestionsCallback;
import com.example.questionnairebuilder.listeners.OnAnalysisCompleteListener;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.example.questionnairebuilder.utilities.AILogicManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeResponsesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private AnalyzeAdapter adapter;
    private List<Question> questions = new ArrayList<>();
    private String surveyID;
    private MaterialTextView analyze_LBL_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_responses);

        surveyID = getIntent().getStringExtra("surveyID");
        toolbar = findViewById(R.id.topAppBar);
        recyclerView = findViewById(R.id.analyze_recycler);
        analyze_LBL_empty = findViewById(R.id.analyze_LBL_empty);

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
                if (questionList.isEmpty()) {
                    analyze_LBL_empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    return;
                } else {
                    analyze_LBL_empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                questions.clear();
                questions.addAll(questionList);

                // Load responses after questions are ready
                FirestoreManager.getInstance().getAllResponsesForSurvey(surveyID, new AllResponsesCallback() {
                    @Override
                    public void onResponsesLoaded(List<DocumentSnapshot> documents) {
                        // Accumulate responses
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
                                if (question.getQuestionID().equals(questionID)) {

                                    if (question instanceof AnalyzableQuestion) {
                                        ((AnalyzableQuestion) question).accumulateAnswers(values);
                                    }

                                    if (question instanceof OpenEndedQuestion) {
                                        OpenEndedQuestion openQuestion = (OpenEndedQuestion) question;

                                        // Append (not overwrite) responses!
                                        openQuestion.getAllResponses().addAll(values);
                                    }

                                    break;
                                }
                            }
                        }

                        // Now run AI summarization once per open-ended question
                        for (Question question : questions) {
                            if (question instanceof OpenEndedQuestion) {
                                OpenEndedQuestion openQuestion = (OpenEndedQuestion) question;

                                if (openQuestion.getAllResponses().isEmpty()) {
                                    openQuestion.setAnalysisResult("No analysis to display");
                                } else {
                                    String questionTitle = openQuestion.getQuestionTitle();

                                    StringBuilder combinedText = new StringBuilder();
                                    for (String response : openQuestion.getAllResponses()) {
                                        combinedText.append("- ").append(response).append("\n");
                                    }

                                    AILogicManager.getInstance().analyzeOpenAnswer(questionTitle, String.valueOf(combinedText), new OnAnalysisCompleteListener() {
                                        @Override
                                        public void onAnalysisComplete(String summary) {
                                            openQuestion.setAnalysisResult(summary);
                                            runOnUiThread(() -> adapter.notifyDataSetChanged());
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            openQuestion.setAnalysisResult("Error generating summary.");
                                            runOnUiThread(() -> adapter.notifyDataSetChanged());
                                        }
                                    });
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