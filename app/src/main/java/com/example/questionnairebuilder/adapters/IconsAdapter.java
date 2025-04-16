package com.example.questionnairebuilder.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.models.IconItem;

public class IconsAdapter extends ArrayAdapter<IconItem> {
    private final Context context;

    public IconsAdapter(@NonNull Context context, IconItem[] items) {
        super(context, R.layout.dropdown_item_icon, items);
        this.context = context;
    }


    private View createIconView(int position, View convertView) {
        ImageView imageView;
        if (convertView instanceof ImageView) {
            imageView = (ImageView) convertView;
        } else {
            imageView = new ImageView(context);
            imageView.setPadding(16, 16, 16, 16);
        }
        imageView.setImageResource(getItem(position).iconResId);
        return imageView;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createIconView(position, convertView);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createIconView(position, convertView);
    }
}

