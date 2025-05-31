package com.example.questionnairebuilder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.models.SurveyWithResponseStatus;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SurveyWithResponseAdapter extends RecyclerView.Adapter<SurveyWithResponseAdapter.SurveyViewHolder> {
    private final Context context;
    private List<SurveyWithResponseStatus> surveysWithResponses;
    private OnSurveyClickListener listener;
    private String currentUserId;
    private final Map<SurveyResponseStatus.ResponseStatus, String> statusesMap = new LinkedHashMap<>();


    public interface OnSurveyClickListener {
        void onSurveyClick(Survey survey);
    }

    public SurveyWithResponseAdapter(Context context, List<SurveyWithResponseStatus> surveysWithResponses, OnSurveyClickListener listener) {
        this.context = context;
        this.surveysWithResponses = surveysWithResponses;
        this.listener = listener;
        currentUserId = AuthenticationManager.getInstance().getCurrentUser().getUid();

        statusesMap.put(SurveyResponseStatus.ResponseStatus.PENDING,context.getString(R.string.pending));
        statusesMap.put(SurveyResponseStatus.ResponseStatus.IN_PROGRESS,context.getString(R.string.in_progress));
        statusesMap.put(SurveyResponseStatus.ResponseStatus.COMPLETED,context.getString(R.string.completed));
    }

    public void updateSurveys(List<SurveyWithResponseStatus> newSurveys) {
        this.surveysWithResponses = newSurveys;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_survey, parent, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        SurveyWithResponseStatus item  = surveysWithResponses.get(position);
        holder.title.setText(item.getSurvey().getSurveyTitle());
        holder.status.setText(statusesMap.get(item.getResponseStatus().getStatus()));
        if(item.getResponseStatus().getStartedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String strDate = context.getString(R.string.response_date) + ": " + sdf.format(item.getResponseStatus().getStartedAt());
            holder.date.setText(strDate);
        } else {
            String strDate = context.getString(R.string.response_date) + ": ";
            holder.date.setText(strDate);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSurveyClick(item.getSurvey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return surveysWithResponses.size();
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
