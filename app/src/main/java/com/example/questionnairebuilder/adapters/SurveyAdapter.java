package com.example.questionnairebuilder.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;

import com.example.questionnairebuilder.models.Survey;
import com.google.android.material.textview.MaterialTextView;

import android.view.LayoutInflater;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {
    private List<Survey> surveys;
    private OnSurveyClickListener listener;

    public interface OnSurveyClickListener {
        void onSurveyClick(Survey survey);
    }

    public SurveyAdapter(List<Survey> surveys, OnSurveyClickListener listener) {
        this.surveys = surveys;
        this.listener = listener;
    }

    public void updateSurveys(List<Survey> newSurveys) {
        this.surveys = newSurveys;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_item, parent, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        Survey survey = surveys.get(position);
        holder.title.setText(survey.getSurveyTitle());
        holder.status.setText(survey.getStatus().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.date.setText("Date: " + sdf.format(survey.getDueDate()));
        holder.responses.setText("Responses: " + (survey.getSurveyViewers() != null ? survey.getSurveyViewers().size() : 0));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSurveyClick(survey);
            }
        });
    }

    @Override
    public int getItemCount() {
        return surveys.size();
    }

    public static class SurveyViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView title, status, date, responses;

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_LBL_surveyTitle);
            status = itemView.findViewById(R.id.item_LBL_surveyStatus);
            date = itemView.findViewById(R.id.item_LBL_surveyDate);
            responses = itemView.findViewById(R.id.item_LBL_surveyResponses);
        }
    }
}
