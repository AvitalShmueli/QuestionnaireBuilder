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
    private View scrollHintBottom;
    private View scrollHintTop;
    private TabLayout tabLayout;
    private int selectedTabPosition = 0;
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
        if (savedInstanceState != null) {
            selectedTabPosition = savedInstanceState.getInt("selected_tab", 0);
        }

        createBinding();
        initTabs();
        initSurveysList();
        initScrollHint();

        return root;
    }

    private void createBinding() {
        tabLayout = binding.tabLayout;
        recyclerView = binding.exploreSurveysRecyclerView;
        scrollHintBottom = binding.scrollHintBottom;
        scrollHintTop = binding.scrollHintTop;
    }

    private void initTabs() {
        // Add tabs
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.pending)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.completed)));

        // Select the correct tab immediately after adding them
        TabLayout.Tab initialTab = tabLayout.getTabAt(selectedTabPosition);
        if (initialTab != null) {
            initialTab.select(); // This triggers onTabSelected
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tab.getPosition();
                getStatusFromTab(selectedTabPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void initSurveysList() {
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

        viewModel.getFilteredSurveys().observe(getViewLifecycleOwner(), surveysWithResponses  -> {
            surveyAdapter.updateSurveys(surveysWithResponses); // Update UI automatically when LiveData changes
        });
    }

    private void initScrollHint() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                updateScrollHints();
            }
        });

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this::updateScrollHints);
    }

    private void updateScrollHints() {
        boolean canScrollUp = recyclerView.canScrollVertically(-1); // up
        boolean canScrollDown = recyclerView.canScrollVertically(1); // down

        scrollHintTop.setVisibility(canScrollUp ? View.VISIBLE : View.GONE);
        scrollHintBottom.setVisibility(canScrollDown ? View.VISIBLE : View.GONE);
    }

    private void getStatusFromTab(int selectedTabPosition){
        // Determine status filter based on tab
        if (selectedTabPosition == 0) {
            currentStatusFilter = SurveyResponseStatus.ResponseStatus.PENDING.toString();
        } else if (selectedTabPosition == 1) {
            currentStatusFilter = SurveyResponseStatus.ResponseStatus.COMPLETED.toString();
        }

        viewModel.setStatusFilter(currentStatusFilter); // Trigger filtering in ViewModel
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

    @Override
    public void onResume() {
        super.onResume();
        // Re-apply filter in case data didn't refresh
        getStatusFromTab(selectedTabPosition);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected_tab", selectedTabPosition);
    }

}