package com.example.questionnairebuilder.ui.my_surveys;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.SurveyManagementActivity;
import com.example.questionnairebuilder.adapters.SurveyAdapter;
import com.example.questionnairebuilder.databinding.FragmentMySurveysBinding;
import com.example.questionnairebuilder.models.Survey;
import com.example.questionnairebuilder.utilities.FirebaseManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
            survey.setID(UUID.randomUUID().toString());
            survey.setSurveyTitle("Survey Title " + i);
            survey.setDescription("This is description " + i);
            survey.setDueDate(new Date());
            survey.setStatus(i % 2 == 0 ? Survey.SurveyStatus.Draft : Survey.SurveyStatus.Published);
            dummySurveys.add(survey);
        }

        // Setup RecyclerView
        RecyclerView recyclerView = binding.mySurveysRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseManager.getInstance().getAllSurveys(surveys -> {
            recyclerView.setAdapter(new SurveyAdapter(surveys, survey -> {
                Intent intent = new Intent(getActivity(), SurveyManagementActivity.class);
                intent.putExtra("survey_title", survey.getSurveyTitle());
                intent.putExtra("status", survey.getStatus().toString());
                intent.putExtra("created_date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(survey.getCreated()));
                intent.putExtra("modified_date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(survey.getModified()));
                intent.putExtra("responses_total", survey.getSurveyViewers() != null ? survey.getSurveyViewers().size() : 0);
                startActivity(intent);
            }));
        });

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