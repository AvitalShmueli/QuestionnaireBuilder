package com.example.questionnairebuilder.models;

import androidx.annotation.NonNull;

public class IconItem {
    public final int iconResId;

    public IconItem(int iconResId) {
        this.iconResId = iconResId;
    }

    @NonNull
    @Override
    public String toString() {
        // Return an empty string so it doesn't show a number
        return "";
    }
}


