package com.example.questionnairebuilder.utilities;

import android.content.Context;

import com.example.questionnairebuilder.R;

import java.util.HashMap;
import java.util.Map;

public class RatingDrawableManager {
    static Map<String, Integer> drawableMap = new HashMap<>();
    private static boolean isInitialized = false;

    public static void init() {
        if (isInitialized)
            return;

        drawableMap.put("ic_star_filled",R.drawable.ic_star_filled);
        drawableMap.put("ic_heart_filled",R.drawable.ic_heart_filled);
        drawableMap.put("ic_thumb_up_filled",R.drawable.ic_thumb_up_filled);

        isInitialized = true;
    }

    public static Map<String, Integer> getDrawableMap() {
        return drawableMap;
    }

    public static Integer getValueByKey(String key) {
        return drawableMap.get(key);
    }
}
