package com.example.questionnairebuilder.ui.my_surveys;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import com.example.questionnairebuilder.adapters.ShimmerAdapter;
import com.example.questionnairebuilder.adapters.SurveyAdapter;
import com.example.questionnairebuilder.databinding.FragmentMySurveysBinding;
import com.example.questionnairebuilder.utilities.AppLogger;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MySurveysFragment extends Fragment {
    private MySurveysViewModel viewModel;
    private FragmentMySurveysBinding binding;
    private RecyclerView recyclerView;
    private View scrollHintBottom;
    private View scrollHintTop;
    private MaterialTextView mySurveys_LBL_noSurveys;
    private SurveyAdapter surveyAdapter;
    private ShimmerAdapter shimmerAdapter;

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

        createBinding();
        setupRecyclerView();
        initSurveysList();
        initScrollHint();

        return root;
    }

    private void createBinding(){
        recyclerView = binding.mySurveysRecyclerView;
        scrollHintBottom = binding.scrollHintBottom;
        scrollHintTop = binding.scrollHintTop;
        mySurveys_LBL_noSurveys = binding.mySurveysLBLNoSurveys;

        binding.mySurveysFABAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewSurveyActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView(){
        shimmerAdapter = new ShimmerAdapter(6); // Show 6 shimmer items
        surveyAdapter = new SurveyAdapter(requireContext(), survey -> {
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
    }

    private void initSurveysList(){
        if(testMode){
            viewModel.getFakeSurveys().observe(getViewLifecycleOwner(), surveys -> {
                surveyAdapter.updateSurveys(surveys); // Update UI automatically when LiveData changes
            });
        }
        else {
            viewModel.getSurveys().observe(getViewLifecycleOwner(), surveys -> {
                surveyAdapter.updateSurveys(surveys); // Update UI automatically when LiveData changes
                mySurveys_LBL_noSurveys.setVisibility(surveys.isEmpty() ? VISIBLE : GONE);
            });
        }

        // Observe loading state
        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), this::showShimmerLoading);
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

    private void showShimmerLoading(boolean isLoading){
        recyclerView.setAdapter(isLoading ? shimmerAdapter : surveyAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", "My Surveys");
        AppLogger.logEvent("screen_opened", bundle);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}