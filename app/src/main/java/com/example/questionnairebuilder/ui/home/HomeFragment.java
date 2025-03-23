package com.example.questionnairebuilder.ui.home;

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

import com.example.questionnairebuilder.NewSurveyActivity;
import com.example.questionnairebuilder.Survey;
import com.example.questionnairebuilder.adapters.SurveyAdapter;
import com.example.questionnairebuilder.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.homeBTNCreateSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewSurveyActivity.class);
            startActivity(intent);
        });

        // final TextView textView = binding.textHome;
        // homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Dummy data
        List<Survey> fakeSurveys = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Survey survey = new Survey();
            survey.setID(i);
            survey.setSurveyTitle("Survey Title " + i);
            survey.setDescription("This is description " + i);
            survey.setDueDate(new Date());
            survey.setStatus(i % 2 == 0 ? Survey.SurveyStatus.Draft : Survey.SurveyStatus.Published);
            fakeSurveys.add(survey);
        }

        // Setup RecyclerView
        RecyclerView recyclerView = binding.homeLSTSurveys;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SurveyAdapter(fakeSurveys));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}