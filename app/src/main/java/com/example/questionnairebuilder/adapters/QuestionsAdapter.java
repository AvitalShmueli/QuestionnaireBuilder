package com.example.questionnairebuilder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.interfaces.Callback_questionSelected;
import com.example.questionnairebuilder.listeners.OnQuestionListChangedListener;
import com.example.questionnairebuilder.models.Question;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    private Context context;
    private final List<Question> questionList;
    private OnQuestionListChangedListener mListChangedListener;

    private Callback_questionSelected callback_questionSelected;

    /*
    public QuestionsAdapter( Context context, List<Question> questionList, OnQuestionListChangedListener listChangedListener) {
        this.context = context;
        this.questionList = questionList;
        this.mListChangedListener = listChangedListener;
    }*/

    public QuestionsAdapter( Context context, List<Question> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    public QuestionsAdapter setCallbackQuestionSelected(Callback_questionSelected callback_questionSelected) {
        this.callback_questionSelected = callback_questionSelected;
        return this;
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position) {
        if (!questionList.isEmpty()) {
            Question question = questionList.get(position);
            String title = question.getQuestionTitle();
            int order = question.setOrder(position + 1).getOrder() ;
            holder.rv_title.setText(title + " | " + order);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(callback_questionSelected != null)
                        callback_questionSelected.select(question);
                }
            };



            holder.rv_title.setOnClickListener(listener);
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
