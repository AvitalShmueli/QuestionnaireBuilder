package com.example.questionnairebuilder.ui.home;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.questionnairebuilder.models.User.USERNAME;

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
import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.SurveyManagementActivity;
import com.example.questionnairebuilder.adapters.ShimmerAdapter;
import com.example.questionnairebuilder.adapters.SurveyAdapter;
import com.example.questionnairebuilder.databinding.FragmentHomeBinding;
import com.example.questionnairebuilder.utilities.AppLogger;
import com.example.questionnairebuilder.utilities.SharedPreferencesManager;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private View scrollHintBottom;
    private View scrollHintTop;
    private MaterialTextView home_LBL_noActiveSurveys;
    private SurveyAdapter surveyAdapter;
    private ShimmerAdapter shimmerAdapter;
    private final boolean testMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initUserGreeting();
        setupRecyclerView();
        initSurveysList();
        initScrollHint();

        binding.homeBTNCreateSurvey.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewSurveyActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void initUserGreeting() {
        SharedPreferencesManager.init(requireContext());
        String prefUsername = SharedPreferencesManager.getInstance().getString(USERNAME,"<username>");

        final TextView home_LBL_greeting = binding.homeLBLGreeting;
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            home_LBL_greeting.setText(getString(R.string.hello_user, Objects.requireNonNullElse(username, prefUsername)));
        });
    }

    private void setupRecyclerView(){
        recyclerView = binding.homeLSTSurveys;
        scrollHintBottom = binding.scrollHintBottom;
        scrollHintTop = binding.scrollHintTop;
        home_LBL_noActiveSurveys = binding.homeLBLNoActiveSurveys;

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
                home_LBL_noActiveSurveys.setVisibility(surveys.isEmpty() ? VISIBLE : GONE);
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

        scrollHintBottom.setVisibility(canScrollDown ? VISIBLE : GONE);
    }

    private void showShimmerLoading(boolean isLoading){
        recyclerView.setAdapter(isLoading ? shimmerAdapter : surveyAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", "Home");
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