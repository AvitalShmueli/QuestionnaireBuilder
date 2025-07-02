package com.example.questionnairebuilder;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.ResponseAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class OpenResponsesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private MaterialTextView emptyLabel;
    private MaterialTextView questionTitleLBL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_responses);

        findViews();
        toolbar.setNavigationOnClickListener(v -> finish());

        ArrayList<String> responses = getIntent().getStringArrayListExtra("responses");
        if (responses != null && !responses.isEmpty()) {
            emptyLabel.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new ResponseAdapter(responses));
        } else {
            emptyLabel.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        String title = getIntent().getStringExtra("questionTitle");
        //toolbar.setTitle(title);
        questionTitleLBL.setText(title);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (responses != null && !responses.isEmpty()) {
            recyclerView.setAdapter(new ResponseAdapter(responses));
        } else {
            ArrayList<String> fallback = new ArrayList<>();
            fallback.add("No responses to display.");
            recyclerView.setAdapter(new ResponseAdapter(fallback));
        }
    }

    private void findViews() {
        toolbar = findViewById(R.id.topAppBar);
        questionTitleLBL = findViewById(R.id.questionTitleLBL);
        recyclerView = findViewById(R.id.openResponses_recycler);
        emptyLabel = findViewById(R.id.openResponses_LBL_empty);
    }
}