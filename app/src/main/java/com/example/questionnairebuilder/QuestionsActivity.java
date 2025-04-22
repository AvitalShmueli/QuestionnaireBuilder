package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.questionnairebuilder.databinding.ActivityQuestionsBinding;
import com.example.questionnairebuilder.models.QuestionTypeEnum;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {

    private ActivityQuestionsBinding binding;
    private FloatingActionButton question_FAB_add;
    private Map<QuestionTypeEnum, String> menu;
    private MaterialButton questions_BTN_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        QuestionTypeManager.init(this);
        menu = QuestionTypeManager.getMenu();

        question_FAB_add = binding.questionFABAdd;
        question_FAB_add.setOnClickListener(v -> showQuestionTypeMenu(v));

        questions_BTN_skip = binding.questionsBTNSkip;
        questions_BTN_skip.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigateTo", "navigation_my_surveys");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // close QuestionsActivity
        });
    }

    private void showQuestionTypeMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.NO_GRAVITY);
        for (Map.Entry<QuestionTypeEnum, String> entry : menu.entrySet()) {
            popupMenu.getMenu().add(entry.getValue());
        }

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

    private void changeActivity(QuestionTypeEnum type) {
        Intent intent = new Intent(this, EditQuestionActivity.class);
        intent.putExtra(EditQuestionActivity.KEY_TYPE, type.toString());
        startActivity(intent);
    }
}