package com.example.questionnairebuilder.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.models.SurveyWithResponseStatus;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_survey_with_response, parent, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        SurveyWithResponseStatus item  = surveysWithResponses.get(position);
        holder.title.setText(item.getSurvey().getSurveyTitle());
        holder.status.setText(statusesMap.get(item.getResponseStatus().getStatus()));

        bindTagsToChipGroup(item.getSurvey().getTags(), holder.tagChipGroup);

        if(item.getResponseStatus().getStartedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String strDate = context.getString(R.string.sent) + ": " + sdf.format(item.getResponseStatus().getStartedAt());
            holder.sentDate.setText(strDate);
        } else {
            String strDate = context.getString(R.string.sent) + ": ";
            holder.sentDate.setText(strDate);
        }

        if(item.getResponseStatus().getCompletedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String strDate = context.getString(R.string.completed) + ": " + sdf.format(item.getResponseStatus().getCompletedAt());
            holder.completedDate.setText(strDate);
        } else {
            String strDate = context.getString(R.string.completed) + ": ";
            holder.completedDate.setText(strDate);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSurveyClick(item.getSurvey());
            }
        });
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

    @Override
    public int getItemCount() {
        return surveysWithResponses.size();
    }

    public static class SurveyViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView title, status, sentDate, completedDate;
        ChipGroup tagChipGroup;

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_LBL_surveyTitle);
            status = itemView.findViewById(R.id.item_LBL_surveyStatus);
            sentDate = itemView.findViewById(R.id.item_LBL_surveySent);
            completedDate = itemView.findViewById(R.id.item_LBL_surveyCompleted);
            tagChipGroup = itemView.findViewById(R.id.item_CHIPGROUP_tags);
        }
    }
}
