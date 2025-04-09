package com.example.questionnairebuilder.ui.my_surveys;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.SurveyManagementActivity;
import com.example.questionnairebuilder.adapters.SurveyAdapter;
import com.example.questionnairebuilder.databinding.FragmentMySurveysBinding;
import com.example.questionnairebuilder.models.Survey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MySurveysFragment extends Fragment {

    private FragmentMySurveysBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMySurveysBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Dummy data for now
        List<Survey> dummySurveys = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Survey survey = new Survey();
            survey.setID(i);
            survey.setSurveyTitle("Survey Title " + i);
            survey.setDescription("This is description " + i);
            survey.setDueDate(new Date());
            survey.setStatus(i % 2 == 0 ? Survey.SurveyStatus.Draft : Survey.SurveyStatus.Published);
            dummySurveys.add(survey);
        }

        // Setup RecyclerView
        RecyclerView recyclerView = binding.mySurveysRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SurveyAdapter(dummySurveys, survey -> {
            Intent intent = new Intent(getActivity(), SurveyManagementActivity.class);
            intent.putExtra("survey_title", survey.getSurveyTitle());
            intent.putExtra("status", survey.getStatus().toString());
            intent.putExtra("created_date", "31/12/2024"); // replace with actual if available
            intent.putExtra("modified_date", "06/01/2025"); // replace with actual if available
            intent.putExtra("questions", 8); // replace if needed
            intent.putExtra("pages", 2); // replace if needed
            intent.putExtra("responses_total", survey.getSurveyViewers() != null ? survey.getSurveyViewers().size() : 0);
            intent.putExtra("responses_completed", 5); // hardcoded for now
            startActivity(intent);
        }));

        // FAB action (optional)
        binding.mySurveysFABAdd.setOnClickListener(v -> {
            // TODO: Start NewSurveyActivity
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}