package com.example.questionnairebuilder.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;

import com.example.questionnairebuilder.listeners.OnCountListener;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;

import android.view.LayoutInflater;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {
    private final Context context;
    private List<Survey> surveys;
    private OnSurveyClickListener listener;
    private String currentUserId;
    private final Map<SurveyResponseStatus.ResponseStatus, String> statusesMap = new LinkedHashMap<>();


    public interface OnSurveyClickListener {
        void onSurveyClick(Survey survey);
    }

    public SurveyAdapter(Context context, List<Survey> surveys, OnSurveyClickListener listener) {
        this.context = context;
        this.surveys = surveys;
        this.listener = listener;
        currentUserId = AuthenticationManager.getInstance().getCurrentUser().getUid();

        statusesMap.put(SurveyResponseStatus.ResponseStatus.PENDING,context.getString(R.string.pending));
        statusesMap.put(SurveyResponseStatus.ResponseStatus.IN_PROGRESS,context.getString(R.string.in_progress));
        statusesMap.put(SurveyResponseStatus.ResponseStatus.COMPLETED,context.getString(R.string.completed));
    }

    public void updateSurveys(List<Survey> newSurveys) {
        this.surveys = newSurveys;
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
        Survey survey = surveys.get(position);
        holder.title.setText(survey.getSurveyTitle());
        holder.status.setText(survey.getStatus().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDueDate = context.getString(R.string.due_date) + ": " + sdf.format(survey.getDueDate());
        holder.date.setText(strDueDate);

        bindTagsToChipGroup(survey.getTags(), holder.tagChipGroup);

        FirestoreManager.getInstance().getSurveyResponseStatusCount(
                survey.getID(),
                Arrays.asList(SurveyResponseStatus.ResponseStatus.IN_PROGRESS, SurveyResponseStatus.ResponseStatus.COMPLETED),
                new OnCountListener() {
                    @Override
                    public void onCountSuccess(int count) {
                        String strResponses = context.getString(R.string.responses) + ": " + count;
                        holder.responses.setText(strResponses);
                    }

                    @Override
                    public void onCountFailure(Exception e) {
                        // Handle the error - set to 0 as default
                        String strResponses = context.getString(R.string.responses) + ": " + 0;
                        holder.responses.setText(strResponses);
                    }
                });

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

    private void bindTagsToChipGroup(List<Survey.SurveyTag> tags, ChipGroup chipGroup) {
        chipGroup.removeAllViews();
        for (Survey.SurveyTag tag : tags) {
            Chip chip = new Chip(chipGroup.getContext());
            chip.setText(tag.name().charAt(0) + tag.name().substring(1).toLowerCase());
            chip.setChipBackgroundColorResource(R.color.light_blue);
            chip.setTextColor(ContextCompat.getColor(chipGroup.getContext(), R.color.dark_blue));
            chip.setElevation(4f);
            chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(chipGroup.getContext(), R.color.light_blue)));
            chip.setChipCornerRadius(24f);
            chip.setTextSize(10);
            chip.setMinHeight(0);
            chip.setMinimumHeight(0);
            chip.setEnsureMinTouchTargetSize(false);
            chip.setChipStartPadding(4f);
            chip.setChipEndPadding(4f);
            chip.setTextStartPadding(6f);
            chip.setTextEndPadding(6f);
            chip.setClickable(false);
            chip.setCheckable(false);
            ChipGroup.LayoutParams layoutParams = new ChipGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            chip.setLayoutParams(layoutParams);
            chipGroup.addView(chip);

            chip.setChipIconResource(tag.getIconResId());
            chip.setChipIconTintResource(R.color.dark_blue); // Optional tint
            chip.setChipIconSize(36f); // Adjust size if needed
            chip.setChipIconVisible(true);
        }
    }


    public static class SurveyViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView title, status, date, responses;
        ChipGroup tagChipGroup;

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_LBL_surveyTitle);
            status = itemView.findViewById(R.id.item_LBL_surveyStatus);
            date = itemView.findViewById(R.id.item_LBL_surveyDate);
            responses = itemView.findViewById(R.id.item_LBL_surveyResponses);
            tagChipGroup = itemView.findViewById(R.id.item_CHIPGROUP_tags);
        }
    }
}
