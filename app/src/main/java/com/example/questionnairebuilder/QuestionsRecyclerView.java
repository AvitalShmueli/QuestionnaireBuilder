package com.example.questionnairebuilder;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsRecyclerView {
    private Context context;
    private RecyclerView recyclerView;
    private QuestionsAdapter questionsAdapter;
    private List<Question> questionsList;


    public QuestionsRecyclerView(Context context, RecyclerView recyclerView, List<Question>questionsList) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.questionsList = questionsList;
        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //questionsAdapter = new QuestionsAdapter(context, questionListDemo());
        questionsAdapter = new QuestionsAdapter(context, questionsList);
        recyclerView.setAdapter(questionsAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper
            .SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            //Collections.swap(questionListDemo(), fromPosition, toPosition);

            Question from = questionsList.get(fromPosition);
            Question to = questionsList.get(toPosition);
            Log.d("TEST","1 fromPosition: " + fromPosition + " " + questionsList.get(fromPosition).getQuestion() +", #" + questionsList.get(fromPosition).getOrder());
            Log.d("TEST","1 toPosition: " + toPosition + " " + questionsList.get(toPosition).getQuestion() +", #" + questionsList.get(toPosition).getOrder());
            questionsList.set(toPosition,from).setOrder(toPosition+1);
            questionsList.set(fromPosition,to).setOrder(fromPosition+1);


            Log.d("TEST","2 fromPosition: " + fromPosition + " " + questionsList.get(fromPosition).getQuestion() +", #" + questionsList.get(fromPosition).getOrder());
            Log.d("TEST","2 toPosition: " + toPosition + " " + questionsList.get(toPosition).getQuestion() +", #" + questionsList.get(toPosition).getOrder());


            if( recyclerView.getAdapter() != null)
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}
