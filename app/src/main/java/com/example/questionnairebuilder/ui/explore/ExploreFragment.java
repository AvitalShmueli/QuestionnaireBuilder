package com.example.questionnairebuilder.ui.explore;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.QuestionsActivity;
import com.example.questionnairebuilder.adapters.SurveyAdapter;
import com.example.questionnairebuilder.databinding.FragmentExploreBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExploreFragment extends Fragment {
    private ExploreViewModel viewModel;
    private FragmentExploreBinding binding;
    private RecyclerView recyclerView;

    private SurveyAdapter surveyAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup RecyclerView
        recyclerView = binding.exploreSurveysRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        surveyAdapter = new SurveyAdapter(requireContext(), new ArrayList<>(), survey -> {
            // OnSurveyClickListener
            //Intent intent = new Intent(getActivity(), SurveyManagementActivity.class);
            Intent intent = new Intent(getActivity(), QuestionsActivity.class);
            intent.putExtra(QuestionsActivity.KEY_EDIT_MODE, false);
            intent.putExtra("survey_title", survey.getSurveyTitle());
            intent.putExtra("surveyID", survey.getID());
            intent.putExtra("status", survey.getStatus().toString());
            intent.putExtra("created_date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(survey.getCreated()));
            intent.putExtra("modified_date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(survey.getModified()));
            intent.putExtra("responses_total", survey.getSurveyViewers() != null ? survey.getSurveyViewers().size() : 0);
            startActivity(intent);
        });

        recyclerView.setAdapter(surveyAdapter);

        viewModel.getSurveys().observe(getViewLifecycleOwner(), surveys -> {
            surveyAdapter.updateSurveys(surveys); // Update UI automatically when LiveData changes
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.stopListening();
    }
}