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
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.adapters.SurveyWithResponseAdapter;
import com.example.questionnairebuilder.databinding.FragmentExploreBinding;
import com.example.questionnairebuilder.models.SurveyResponseStatus;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {
    private ExploreViewModel viewModel;
    private FragmentExploreBinding binding;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private String currentStatusFilter = SurveyResponseStatus.ResponseStatus.PENDING.name();
    private SurveyWithResponseAdapter surveyAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tabLayout = binding.tabLayout;
        // Add tabs
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.pending)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.completed)));

        // Setup RecyclerView
        recyclerView = binding.exploreSurveysRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        surveyAdapter = new SurveyWithResponseAdapter(requireContext(), new ArrayList<>(), survey -> {
            // OnSurveyClickListener
            //Intent intent = new Intent(getActivity(), SurveyManagementActivity.class);
            Intent intent = new Intent(getActivity(), QuestionsActivity.class);
            intent.putExtra(QuestionsActivity.KEY_EDIT_MODE, false);
            intent.putExtra("survey_title", survey.getSurveyTitle());
            intent.putExtra("surveyID", survey.getID());
            //intent.putExtra("status", survey.getStatus().toString());
            //intent.putExtra("created_date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(survey.getCreated()));
            //intent.putExtra("modified_date", new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(survey.getModified()));
            //intent.putExtra("responses_total", survey.getSurveyViewers() != null ? survey.getSurveyViewers().size() : 0);
            startActivity(intent);
        });

        recyclerView.setAdapter(surveyAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedTab = tab.getText().toString();

                // Determine status filter based on tab
                if (selectedTab.equals(getString(R.string.pending))) {
                    currentStatusFilter = SurveyResponseStatus.ResponseStatus.PENDING.toString();
                } else if (selectedTab.equals(getString(R.string.in_progress))) {
                    currentStatusFilter = SurveyResponseStatus.ResponseStatus.IN_PROGRESS.toString();
                } else if (selectedTab.equals(getString(R.string.completed))) {
                    currentStatusFilter = SurveyResponseStatus.ResponseStatus.COMPLETED.toString();
                }

                // Trigger filtering in ViewModel
                viewModel.setStatusFilter(currentStatusFilter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewModel.getFilteredSurveys().observe(getViewLifecycleOwner(), surveysWithResponses  -> {
            surveyAdapter.updateSurveys(surveysWithResponses); // Update UI automatically when LiveData changes
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