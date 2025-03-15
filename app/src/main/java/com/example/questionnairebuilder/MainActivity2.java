package com.example.questionnairebuilder;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.Listeners.OnQuestionListChangedListener;
import com.example.questionnairebuilder.Listeners.OnStartDragListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements OnQuestionListChangedListener, OnStartDragListener {
    private RecyclerView recyclerView;
    private QuestionsRecyclerView questionsRecyclerView;

    private List<Question> questionListDemo(){
        List<Question> list = new ArrayList<>();
        list.add(new Question("Q1 - What is your name?","open"));
        list.add(new Question("Q2 - How old are you?","number"));
        list.add(new Question("Q3?","open"));
        list.add(new Question("Q4?","open"));
        list.add(new Question("Q5?","open"));
        list.add(new Question("Q6?","open"));
        list.add(new Question("Q7?","open"));
        list.add(new Question("Q8?","open"));
        list.add(new Question("Q9?","open"));
        list.add(new Question("Q10?","open"));
        list.add(new Question("Q11?","open"));
        list.add(new Question("Q12?","open"));
        list.add(new Question("Q13?","open"));
        list.add(new Question("Q14?","open"));
        list.add(new Question("Q15?","open"));
        return  list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViews();
        questionsRecyclerView = new QuestionsRecyclerView(this,recyclerView,questionListDemo());
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    public void onNoteListChanged(List<Question> questions) {

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }


}