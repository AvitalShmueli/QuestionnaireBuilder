package com.example.questionnairebuilder.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.interfaces.Callback_questionSelected;
import com.example.questionnairebuilder.models.Question;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    private List<Question> questionList;

    private Callback_questionSelected callback_questionSelected;

    public QuestionsAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    public QuestionsAdapter setCallbackQuestionSelected(Callback_questionSelected callback_questionSelected) {
        this.callback_questionSelected = callback_questionSelected;
        return this;
    }

    public void updateQuestions(List<Question> newQuestions) {
        this.questionList = newQuestions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position) {
        if (!questionList.isEmpty()) {
            Question question = questionList.get(position);
            String title = question.getQuestionTitle();
            //int order = question.setOrder(position + 1).getOrder();
            int order = question.getOrder();
            holder.rv_mandatory_star.setVisibility(question.isMandatory()? VISIBLE : GONE);
            holder.rv_title.setText(title + " | " + order);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(callback_questionSelected != null)
                        callback_questionSelected.select(question);
                }
            };

            holder.item_CARD_data.setOnClickListener(listener);
        }
    }

    @Override
    public int getItemCount() {
        return questionList == null ? 0 : questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView item_CARD_data;
        private MaterialTextView rv_title;
        private MaterialTextView rv_mandatory_star;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_CARD_data = itemView.findViewById(R.id.item_CARD_data);
            rv_title = itemView.findViewById(R.id.rv_title);
            rv_mandatory_star = itemView.findViewById(R.id.rv_mandatory_star);
        }
    }


}
