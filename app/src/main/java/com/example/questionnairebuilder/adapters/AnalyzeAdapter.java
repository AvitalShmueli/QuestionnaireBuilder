package com.example.questionnairebuilder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.models.OpenEndedQuestion;
import com.example.questionnairebuilder.models.Question;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AnalyzeAdapter extends RecyclerView.Adapter<AnalyzeAdapter.AnalyzeViewHolder> {

    private List<Question> questionList;

    public AnalyzeAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public AnalyzeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_analysis, parent, false);
        return new AnalyzeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyzeViewHolder holder, int position) {
        Question q = questionList.get(position);
        holder.question_LBL_title.setText(q.getQuestionTitle());

        if (q instanceof OpenEndedQuestion) {
            OpenEndedQuestion openQ = (OpenEndedQuestion) q;
            if (openQ.getAnalysisResult() != null) {
                holder.question_LBL_result.setText(openQ.getAnalysisResult());
            } else {
                holder.question_LBL_result.setText("Analysis not available.");
            }
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class AnalyzeViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView question_LBL_title, question_LBL_result;

        public AnalyzeViewHolder(@NonNull View itemView) {
            super(itemView);
            question_LBL_title = itemView.findViewById(R.id.question_LBL_title);
            question_LBL_result = itemView.findViewById(R.id.question_LBL_result);
        }
    }
}
