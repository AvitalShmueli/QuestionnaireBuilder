package com.example.questionnairebuilder.ui.new_question;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.questionnairebuilder.databinding.FragmentNewQuestionBinding;
import com.example.questionnairebuilder.ui.explore.ExploreViewModel;

public class NewQuestionFragment extends Fragment {

    private NewQuestionViewModel mViewModel;

    public static NewQuestionFragment newInstance() {
        return new NewQuestionFragment();
    }

    private FragmentNewQuestionBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel =
                new ViewModelProvider(this).get(NewQuestionViewModel.class);

        binding = FragmentNewQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}