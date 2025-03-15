package com.example.questionnairebuilder.ui.my_surveys;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.questionnairebuilder.databinding.FragmentMySurveysBinding;

public class MySurveysFragment extends Fragment {

    private FragmentMySurveysBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MySurveysViewModel notificationsViewModel =
                new ViewModelProvider(this).get(MySurveysViewModel.class);

        binding = FragmentMySurveysBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}