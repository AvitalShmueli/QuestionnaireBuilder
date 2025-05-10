package com.example.questionnairebuilder.ui.my_surveys;

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

import com.example.questionnairebuilder.NewSurveyActivity;
import com.example.questionnairebuilder.SurveyManagementActivity;
import com.example.questionnairebuilder.adapters.SurveyAdapter;
import com.example.questionnairebuilder.databinding.FragmentMySurveysBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MySurveysFragment extends Fragment {
    private MySurveysViewModel viewModel;
    private FragmentMySurveysBinding binding;
    private RecyclerView recyclerView;

    private SurveyAdapter surveyAdapter;

    private final boolean testMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MySurveysViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMySurveysBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup RecyclerView
        recyclerView = binding.mySurveysRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.mySurveysFABAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewSurveyActivity.class);
            startActivity(intent);
        });

        surveyAdapter = new SurveyAdapter(requireContext(), new ArrayList<>(), survey -> {
            // OnSurveyClickListener
            Intent intent = new Intent(getActivity(), SurveyManagementActivity.class);
            intent.putExtra("survey_title", survey.getSurveyTitle());
            intent.putExtra("surveyID", survey.getID());
            intent.putExtra("status", survey.getStatus().toString());
            intent.putExtra("created_date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(survey.getCreated()));
            intent.putExtra("modified_date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(survey.getModified()));
            intent.putExtra("responses_total", survey.getSurveyViewers() != null ? survey.getSurveyViewers().size() : 0);
            startActivity(intent);
        });

        recyclerView.setAdapter(surveyAdapter);

        if(testMode){
            viewModel.getFakeSurveys().observe(getViewLifecycleOwner(), surveys -> {
                surveyAdapter.updateSurveys(surveys); // Update UI automatically when LiveData changes
            });
        }
        else {
            viewModel.getSurveys().observe(getViewLifecycleOwner(), surveys -> {
                surveyAdapter.updateSurveys(surveys); // Update UI automatically when LiveData changes
            });
        }

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
        if(testMode){
            viewModel.startListeningFake();
        }
        else {
            viewModel.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.stopListening();
    }
}