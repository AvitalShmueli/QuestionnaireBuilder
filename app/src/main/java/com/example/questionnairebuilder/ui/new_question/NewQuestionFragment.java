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

import com.example.questionnairebuilder.EditQuestionActivity;
import com.example.questionnairebuilder.databinding.FragmentNewQuestionBinding;
import com.example.questionnairebuilder.models.QuestionType;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class NewQuestionFragment extends Fragment {

    private NewQuestionViewModel mViewModel;

    public static NewQuestionFragment newInstance() {
        return new NewQuestionFragment();
    }

    private FragmentNewQuestionBinding binding;
    private FloatingActionButton question_FAB_add;
    private Map<QuestionType,String> menu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(NewQuestionViewModel.class);

        binding = FragmentNewQuestionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        QuestionTypeManager.init(requireActivity());
        menu = QuestionTypeManager.getMenu();

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
        popupMenu.getMenu().add(menu.get(QuestionType.OPEN_ENDED_QUESTION));
        popupMenu.getMenu().add(menu.get(QuestionType.SINGLE_CHOICE));
        popupMenu.getMenu().add(menu.get(QuestionType.DROPDOWN));
        popupMenu.getMenu().add(menu.get(QuestionType.YES_NO));
        popupMenu.getMenu().add(menu.get(QuestionType.MULTIPLE_CHOICE));
        popupMenu.getMenu().add(menu.get(QuestionType.RATING_SCALE));
        popupMenu.getMenu().add(menu.get(QuestionType.MATRIX_QUESTION));


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedItem = item.getTitle().toString();
                changeActivity(QuestionTypeManager.getKeyByValue(selectedItem));
                return true;
            }
        });

        popupMenu.show();
    }


    private void changeActivity(QuestionType type) {
        Intent editQuestionActivity = new Intent(requireActivity(), EditQuestionActivity.class);
        editQuestionActivity.putExtra(EditQuestionActivity.KEY_TYPE,type.toString());
        startActivity(editQuestionActivity);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}