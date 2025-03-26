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
import com.example.questionnairebuilder.models.ChoiceQuestion;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;
import com.example.questionnairebuilder.models.SingleChoiceType;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements OnQuestionListChangedListener, OnStartDragListener {
    private RecyclerView recyclerView;
    private QuestionsRecyclerView questionsRecyclerView;

    private List<Question> questionListDemo(){
        List<Question> list = new ArrayList<>();
        list.add(new OpenQuestion("Q1 - What is your name?","open"));
        list.add(new OpenQuestion("Q2 - How old are you?","number"));
        list.add(new OpenQuestion("Q3?","open"));
        list.add(new OpenQuestion("Q4?","open"));
        list.add(new OpenQuestion("Q5?","open"));
        list.add(new OpenQuestion("Q6?","open"));
        list.add(new OpenQuestion("Q7?","open"));
        list.add(new OpenQuestion("Q8?","open"));
        list.add(new OpenQuestion("Q9?","open"));
        list.add(new OpenQuestion("Q10?","open"));
        list.add(new OpenQuestion("Q11?","open"));
        list.add(new OpenQuestion("Q12?","open"));
        list.add(new MultipleChoiceQuestion("Q13?"));
        list.add(new SingleChoiceQuestion("Q14?",SingleChoiceType.DROPDOWN));
        list.add(new SingleChoiceQuestion("Q15?", SingleChoiceType.YES_NO));
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