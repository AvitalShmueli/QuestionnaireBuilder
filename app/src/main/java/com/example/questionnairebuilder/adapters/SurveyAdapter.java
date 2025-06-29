package com.example.questionnairebuilder.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;

import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.example.questionnairebuilder.models.SurveyWithResponseCount;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;

import android.view.LayoutInflater;
import android.widget.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {
    private final Context context;
    //private List<Survey> surveys;
    private List<SurveyWithResponseCount> surveysWithCount;
    private OnSurveyClickListener listener;
    private String currentUserId;
    private final Map<SurveyResponseStatus.ResponseStatus, String> statusesMap = new LinkedHashMap<>();


    public interface OnSurveyClickListener {
        void onSurveyClick(Survey survey);
    }

    public SurveyAdapter(Context context, List<Survey> surveys, OnSurveyClickListener listener) {
        this.context = context;
        //this.surveys = surveys;
        this.listener = listener;
        this.surveysWithCount = new ArrayList<>();
        currentUserId = AuthenticationManager.getInstance().getCurrentUser().getUid();

        setHasStableIds(true);

        statusesMap.put(SurveyResponseStatus.ResponseStatus.PENDING,context.getString(R.string.pending));
        statusesMap.put(SurveyResponseStatus.ResponseStatus.IN_PROGRESS,context.getString(R.string.in_progress));
        statusesMap.put(SurveyResponseStatus.ResponseStatus.COMPLETED,context.getString(R.string.completed));
    }

    public void updateSurveys(List<SurveyWithResponseCount> surveysWithCount) {
        this.surveysWithCount = surveysWithCount;
        notifyDataSetChanged();
    }

    /*
    public void updateSurveys(List<Survey> newSurveys) {
        this.surveys = newSurveys;
        notifyDataSetChanged();
    }*/

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_survey, parent, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        SurveyWithResponseCount surveyWithCount = surveysWithCount.get(position);
        Survey survey = surveyWithCount.getSurvey();
        //Survey survey = surveys.get(position);
        holder.title.setText(survey.getSurveyTitle());
        holder.status.setText(survey.getStatus().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDueDate = context.getString(R.string.due_date) + ": " + sdf.format(survey.getDueDate());
        holder.date.setText(strDueDate);

        bindTagsToChipGroup(survey.getTags(), holder.tagChipGroup);

        /*FirestoreManager.getInstance().getSurveyResponseStatusCount(
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
                });*/

        if (surveyWithCount.isLoading()) {
            // Show small loading spinner next to "Responses:"
            holder.responsesCount.setVisibility(GONE);
            holder.responsesLoading.setVisibility(VISIBLE);
        } else {
            // Show actual count
            holder.responsesLoading.setVisibility(GONE);
            holder.responsesCount.setVisibility(VISIBLE);
            holder.responsesCount.setText(String.valueOf(surveyWithCount.getResponseCount()));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSurveyClick(survey);
            }
        });
    }

    @Override
    public int getItemCount() {
        //return surveys.size();
        return surveysWithCount.size();

    }

    private void bindTagsToChipGroup(List<Survey.SurveyTag> tags, ChipGroup chipGroup) {
        chipGroup.removeAllViews();
        for (Survey.SurveyTag tag : tags) {
            Chip chip = createStyledChip(chipGroup.getContext(), tag);
            chipGroup.addView(chip);
            /*Chip chip = new Chip(chipGroup.getContext());
            String tagName = tag.name().charAt(0) + tag.name().substring(1).toLowerCase();
            chip.setText(tagName);
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
            chip.setChipIconVisible(true);*/
        }
        chipGroup.setVisibility(tags.isEmpty() ? GONE : VISIBLE);
    }

    private Chip createStyledChip(Context context, Survey.SurveyTag tag) {
        Chip chip = new Chip(context);

        // Set text first
        String tagName = tag.name().charAt(0) + tag.name().substring(1).toLowerCase();
        chip.setText(tagName);

        // Configure all visual properties before measuring
        chip.setChipBackgroundColorResource(R.color.light_blue);
        chip.setTextColor(ContextCompat.getColor(context, R.color.dark_blue));
        chip.setElevation(4f);
        chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_blue)));
        chip.setChipCornerRadius(24f);
        chip.setTextSize(10);

        // Set fixed dimensions to prevent resizing
        chip.setMinHeight(0);
        chip.setMinimumHeight(0);
        chip.setEnsureMinTouchTargetSize(false);

        // Use consistent padding
        chip.setChipStartPadding(4f);
        chip.setChipEndPadding(4f);
        chip.setTextStartPadding(6f);
        chip.setTextEndPadding(6f);

        // Disable interactions
        chip.setClickable(false);
        chip.setCheckable(false);
        chip.setFocusable(false);

        // Set icon properties
        chip.setChipIconResource(tag.getIconResId());
        chip.setChipIconTintResource(R.color.dark_blue);
        chip.setChipIconSize(36f);
        chip.setChipIconVisible(true);

        // Create layout params with fixed dimensions if needed
        ChipGroup.LayoutParams layoutParams = new ChipGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        // Optional: Set margins to prevent layout shifts
        layoutParams.setMargins(2, 2, 2, 2);
        chip.setLayoutParams(layoutParams);

        return chip;
    }


    public static class SurveyViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView title, status, date, responsesCount;
        ChipGroup tagChipGroup;
        ProgressBar responsesLoading;

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_LBL_surveyTitle);
            status = itemView.findViewById(R.id.item_LBL_surveyStatus);
            date = itemView.findViewById(R.id.item_LBL_surveyDate);
            responsesCount = itemView.findViewById(R.id.item_LBL_surveyResponsesCount);
            tagChipGroup = itemView.findViewById(R.id.item_CHIPGROUP_tags);
            responsesLoading = itemView.findViewById(R.id.item_responses_loading);
        }
    }
}
