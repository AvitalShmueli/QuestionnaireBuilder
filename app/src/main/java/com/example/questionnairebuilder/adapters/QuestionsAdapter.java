package com.example.questionnairebuilder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.models.Question;
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.listeners.OnQuestionListChangedListener;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    private Context context;
    private final List<Question> questionList;
    private OnQuestionListChangedListener mListChangedListener;

    /*
    public QuestionsAdapter( Context context, List<Question> questionList, OnQuestionListChangedListener listChangedListener) {
        this.context = context;
        this.questionList = questionList;
        this.mListChangedListener = listChangedListener;
    }*/

    public QuestionsAdapter(Context context, List<Question> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!questionList.isEmpty()) {
            Question question = questionList.get(position);
            String title = question.getQuestionTitle();
            int order = question.setOrder(position + 1).getOrder() ;
            holder.rv_title.setText(title + " | " + order);
        }

    }

    @Override
    public int getItemCount() {
        return questionList == null ? 0 : questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView rv_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rv_title = itemView.findViewById(R.id.rv_title);
        }
    }


}
