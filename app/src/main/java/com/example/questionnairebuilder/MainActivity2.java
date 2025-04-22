package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.QuestionsAdapter;
import com.example.questionnairebuilder.interfaces.Callback_questionSelected;
import com.example.questionnairebuilder.listeners.OnQuestionListChangedListener;
import com.example.questionnairebuilder.listeners.OnStartDragListener;
import com.example.questionnairebuilder.models.ChoiceQuestion;
import com.example.questionnairebuilder.models.DateQuestion;
import com.example.questionnairebuilder.models.DateSelectionModeEnum;
import com.example.questionnairebuilder.models.MultipleChoiceQuestion;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.RatingScaleQuestion;
import com.example.questionnairebuilder.models.SingleChoiceQuestion;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements OnQuestionListChangedListener, OnStartDragListener {
    private RecyclerView recyclerView;

    private List<Question> questionListDemo(){
        List<Question> list = new ArrayList<>();
        list.add(new OpenEndedQuestion("Q1 - What is your name?").setMandatory(true));
        list.add(new OpenEndedQuestion("Q2 - How old are you?"));
        list.add(new DateQuestion("Q3? - Birth date").setDateMode(DateSelectionModeEnum.SINGLE_DATE).setMandatory(true));
        list.add(new DateQuestion("Q4? - Vacation dates").setDateMode(DateSelectionModeEnum.DATE_RANGE).setMandatory(false));
        list.add(new SingleChoiceQuestion("Q5? Yes No Mandatory Question?", QuestionTypeEnum.YES_NO)
                .addChoice("Yes")
                .addChoice("No")
                .setMandatory(true)
        );
        list.add(new RatingScaleQuestion("Q6? Rating question")
                .setIconResourceId(R.drawable.ic_heart_filled)
                .setRatingScaleLevel(3)
                .setMandatory(true)
        );
        list.add(new OpenEndedQuestion("Q7?"));
        list.add(new OpenEndedQuestion("Q8?"));
        list.add(new OpenEndedQuestion("Q9?"));
        list.add(new OpenEndedQuestion("Q10?"));
        list.add(new OpenEndedQuestion("Q11?"));
        list.add(new OpenEndedQuestion("Q12?"));
        list.add(new MultipleChoiceQuestion("Q13? Multi selection")
                .setAllowedSelectionNum(2)
                .addChoice("Option A")
                .addChoice("Option B")
                .addChoice("Option C")
                .addChoice("Option D")
                .setMandatory(true)
        );
        list.add(new SingleChoiceQuestion("Q14?", QuestionTypeEnum.DROPDOWN));
        list.add(new SingleChoiceQuestion("Q15? Yes No Question?", QuestionTypeEnum.YES_NO)
                .addChoice("Yes")
                .addChoice("No")
        );
        return list;
    }

    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        findViews();
        questionList = questionListDemo();
        QuestionsAdapter questionsAdapter = new QuestionsAdapter(this,questionList);
        questionsAdapter.setCallbackQuestionSelected(new Callback_questionSelected() {
            @Override
            public void select(Question question) {
                changeActivity(question);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(questionsAdapter);
        //questionsRecyclerView = new QuestionsRecyclerView(this,recyclerView,questionListDemo());
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


    private void changeActivity(Question q) {
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
        if(q instanceof OpenEndedQuestion)
            args.putBoolean("multipleLineAnswer",((OpenEndedQuestion)q).isMultipleLineAnswer());
        if(q instanceof ChoiceQuestion)
            args.putStringArrayList("choices",((ChoiceQuestion)q).getChoices());
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


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
            .SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            //Collections.swap(questionListDemo(), fromPosition, toPosition);

            Question from = questionList.get(fromPosition);
            Question to = questionList.get(toPosition);
            Log.d("TEST","1 fromPosition: " + fromPosition + " " + questionList.get(fromPosition).getQuestionTitle() +", #" + questionList.get(fromPosition).getOrder());
            Log.d("TEST","1 toPosition: " + toPosition + " " + questionList.get(toPosition).getQuestionTitle() +", #" + questionList.get(toPosition).getOrder());
            questionList.set(toPosition,from).setOrder(toPosition+1);
            questionList.set(fromPosition,to).setOrder(fromPosition+1);


            Log.d("TEST","2 fromPosition: " + fromPosition + " " + questionList.get(fromPosition).getQuestionTitle() +", #" + questionList.get(fromPosition).getOrder());
            Log.d("TEST","2 toPosition: " + toPosition + " " + questionList.get(toPosition).getQuestionTitle() +", #" + questionList.get(toPosition).getOrder());


            if( recyclerView.getAdapter() != null)
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}