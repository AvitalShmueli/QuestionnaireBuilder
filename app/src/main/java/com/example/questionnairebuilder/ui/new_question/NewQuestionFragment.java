package com.example.questionnairebuilder.ui.new_question;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.questionnairebuilder.EditQuestionActivity;
import com.example.questionnairebuilder.databinding.FragmentNewQuestionBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewQuestionFragment extends Fragment {

    private NewQuestionViewModel mViewModel;

    public static NewQuestionFragment newInstance() {
        return new NewQuestionFragment();
    }

    private FragmentNewQuestionBinding binding;
    private FloatingActionButton question_FAB_add;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(NewQuestionViewModel.class);

        binding = FragmentNewQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        question_FAB_add = binding.questionFABAdd;
        question_FAB_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuestionTypeMenu(v,getActivity());
            }
        });

        return root;
    }

    private void showQuestionTypeMenu(View v, Activity activity) {
        PopupMenu popupMenu = new PopupMenu(this.getActivity(),v,Gravity.NO_GRAVITY);
        popupMenu.getMenu().add("Open question");
        popupMenu.getMenu().add("Single choice");
        popupMenu.getMenu().add("Multiple choice");
        popupMenu.getMenu().add("Dropdown");
        popupMenu.getMenu().add("Rating scale");
        popupMenu.getMenu().add("Matrix question");


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(activity, "Selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: pass selection to next screen
                changeActivity(item.getTitle().toString());
                // Here you can navigate to the screen for that question type
                return true;
            }
        });

        popupMenu.show();
    }


    private void changeActivity(String type) {
        Intent editQuestionActivity = new Intent(requireActivity(), EditQuestionActivity.class);
        editQuestionActivity.putExtra(EditQuestionActivity.KEY_TYPE,type);
        startActivity(editQuestionActivity);
        //requireActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}