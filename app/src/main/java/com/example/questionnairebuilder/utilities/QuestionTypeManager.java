package com.example.questionnairebuilder.utilities;

import android.content.Context;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.models.QuestionTypeEnum;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuestionTypeManager {
    private static final Map<QuestionTypeEnum, String> menu = new LinkedHashMap<>();
    private static final Map<String, QuestionTypeEnum> reverseMenu = new LinkedHashMap<>();
    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (isInitialized)
            return;

        menu.put(QuestionTypeEnum.OPEN_ENDED_QUESTION, context.getString(R.string.open_ended_question));
        menu.put(QuestionTypeEnum.SINGLE_CHOICE, context.getString(R.string.single_choice));
        menu.put(QuestionTypeEnum.DROPDOWN, context.getString(R.string.dropdown));
        menu.put(QuestionTypeEnum.YES_NO, context.getString(R.string.yes_no));
        menu.put(QuestionTypeEnum.MULTIPLE_CHOICE, context.getString(R.string.multiple_choice));
        menu.put(QuestionTypeEnum.DATE, context.getString(R.string.date));
        menu.put(QuestionTypeEnum.RATING_SCALE, context.getString(R.string.rating_scale));
        //menu.put(QuestionTypeEnum.MATRIX_QUESTION, context.getString(R.string.matrix_question));

        // Reverse lookup map
        for (Map.Entry<QuestionTypeEnum, String> entry : menu.entrySet()) {
            reverseMenu.put(entry.getValue(), entry.getKey());
        }

        isInitialized = true;
    }


    public static Map<QuestionTypeEnum, String> getMenu() {
        return menu;
    }

    public static QuestionTypeEnum getKeyByValue(String value) {
        return reverseMenu.get(value);
    }

    public static String getValueByKey(QuestionTypeEnum key) {
        return menu.get(key);
    }
}
