package com.example.questionnairebuilder.models;

import android.content.Context;

import com.example.questionnairebuilder.R;

import java.util.HashMap;
import java.util.Map;

public class QuestionTypeManager {
    private static final Map<QuestionType, String> menu = new HashMap<>();
    private static final Map<String, QuestionType> reverseMenu = new HashMap<>();
    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (isInitialized)
            return;

        menu.put(QuestionType.OPEN_ENDED_QUESTION, context.getString(R.string.open_ended_question));
        menu.put(QuestionType.SINGLE_CHOICE, context.getString(R.string.single_choice));
        menu.put(QuestionType.DROPDOWN, context.getString(R.string.dropdown));
        menu.put(QuestionType.YES_NO, context.getString(R.string.yes_no));
        menu.put(QuestionType.MULTIPLE_CHOICE, context.getString(R.string.multiple_choice));
        menu.put(QuestionType.DATE, context.getString(R.string.date));
        menu.put(QuestionType.RATING_SCALE, context.getString(R.string.rating_scale));
        menu.put(QuestionType.MATRIX_QUESTION, context.getString(R.string.matrix_question));

        // Reverse lookup map
        for (Map.Entry<QuestionType, String> entry : menu.entrySet()) {
            reverseMenu.put(entry.getValue(), entry.getKey());
        }

        isInitialized = true;
    }


    public static Map<QuestionType, String> getMenu() {
        return menu;
    }

    public static QuestionType getKeyByValue(String value) {
        return reverseMenu.get(value);
    }

    public static String getValueByKey(QuestionType key) {
        return menu.get(key);
    }
}
