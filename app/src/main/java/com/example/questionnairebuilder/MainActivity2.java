package com.example.questionnairebuilder;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.listeners.OnQuestionListChangedListener;
import com.example.questionnairebuilder.listeners.OnStartDragListener;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionType;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements OnQuestionListChangedListener, OnStartDragListener {
    private RecyclerView recyclerView;
    private QuestionsRecyclerView questionsRecyclerView;

    private List<Question> questionListDemo(){
        List<Question> list = new ArrayList<>();
        list.add(new OpenEndedQuestion("Q1 - What is your name?"));
        list.add(new OpenEndedQuestion("Q2 - How old are you?"));
        list.add(new OpenEndedQuestion("Q3?"));
        list.add(new OpenEndedQuestion("Q4?"));
        list.add(new OpenEndedQuestion("Q5?"));
        list.add(new OpenEndedQuestion("Q6?"));
        list.add(new OpenEndedQuestion("Q7?"));
        list.add(new OpenEndedQuestion("Q8?"));
        list.add(new OpenEndedQuestion("Q9?"));
        list.add(new OpenEndedQuestion("Q10?"));
        list.add(new OpenEndedQuestion("Q11?"));
        list.add(new OpenEndedQuestion("Q12?"));
        list.add(new MultipleChoiceQuestion("Q13?"));
        list.add(new SingleChoiceQuestion("Q14?", QuestionType.DROPDOWN));
        list.add(new SingleChoiceQuestion("Q15?", QuestionType.YES_NO));
        return list;
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