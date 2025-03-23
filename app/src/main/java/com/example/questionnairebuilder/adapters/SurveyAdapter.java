package com.example.questionnairebuilder.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.Survey;
import com.google.android.material.textview.MaterialTextView;

import android.view.LayoutInflater;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {
    private List<Survey> surveys;

    public SurveyAdapter(List<Survey> surveys) {
        this.surveys = surveys;
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
