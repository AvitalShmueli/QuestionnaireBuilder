package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.questionnairebuilder.databinding.ActivityQuestionsBinding;
import com.example.questionnairebuilder.models.QuestionType;
import com.example.questionnairebuilder.models.QuestionTypeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {

    private ActivityQuestionsBinding binding;
    private FloatingActionButton question_FAB_add;
    private Map<QuestionType, String> menu;
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
            startActivity(intent);
            finish(); // close QuestionsActivity
        });
    }

    private void showQuestionTypeMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v, Gravity.NO_GRAVITY);
        popupMenu.getMenu().add(menu.get(QuestionType.Open_Ended_Question));
        popupMenu.getMenu().add(menu.get(QuestionType.Single_Choice));
        popupMenu.getMenu().add(menu.get(QuestionType.Dropdown));
        popupMenu.getMenu().add(menu.get(QuestionType.Yes_No));
        popupMenu.getMenu().add(menu.get(QuestionType.Multiple_Choice));
        popupMenu.getMenu().add(menu.get(QuestionType.Rating_Scale));
        popupMenu.getMenu().add(menu.get(QuestionType.Matrix_Question));

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
        Intent intent = new Intent(this, EditQuestionActivity.class);
        intent.putExtra(EditQuestionActivity.KEY_TYPE, type.toString());
        startActivity(intent);
    }
}