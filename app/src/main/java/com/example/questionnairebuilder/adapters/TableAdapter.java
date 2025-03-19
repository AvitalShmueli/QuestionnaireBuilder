package com.example.questionnairebuilder.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private final ArrayList<String> dataList;

    public TableAdapter() {
        this.dataList = new ArrayList<>();
        this.dataList.add(""); // Initial empty row
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.editText.setText(dataList.get(position));
        holder.deleteIcon.setEnabled(dataList.size() > 1);
        holder.deleteIcon.setAlpha(dataList.size() > 1 ? 1.0f : 0.5f); // Dim icon if disabled

        // TextWatcher to handle text changes
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    dataList.set(currentPosition, s.toString());

                    // Add a new row if the last row is being edited
                    if (currentPosition == dataList.size() - 1 && !s.toString().isEmpty()) {
                        dataList.add("");
                        notifyItemInserted(dataList.size() - 1);
                    }
                }
                holder.editText.clearFocus();
            }
        });

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        ImageView deleteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editText);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
