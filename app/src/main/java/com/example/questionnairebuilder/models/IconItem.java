package com.example.questionnairebuilder.models;

import androidx.annotation.NonNull;

public class IconItem {
    public final String iconResName;

    public IconItem(String iconResName) {
        this.iconResName = iconResName;
    }

    @NonNull
    @Override
    public String toString() {
        // Return an empty string so it doesn't show a number
        return "";
    }
}


