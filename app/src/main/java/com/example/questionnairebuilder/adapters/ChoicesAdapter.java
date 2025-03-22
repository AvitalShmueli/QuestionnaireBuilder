package com.example.questionnairebuilder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.listeners.OnRowCountChangeListener;

import java.util.ArrayList;

public class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.ViewHolder> {

    private final ArrayList<String> dataList;
    private OnRowCountChangeListener rowCountListener;

    public ChoicesAdapter(OnRowCountChangeListener listener) {
        this.dataList = new ArrayList<>();
        this.dataList.add(""); // Initial empty row
        this.rowCountListener = listener;
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

          /* close the keyboard after each row insertion */
//        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) { // User exits the EditText
//                    int currentPosition = holder.getAdapterPosition();
//                    String s = holder.editText.getText().toString();
//                    if (currentPosition != RecyclerView.NO_POSITION) {
//                        dataList.set(currentPosition, s);
//
//                        // Add a new row only if it's the last row and has content
//                        if (currentPosition == dataList.size() - 1 && !s.isEmpty()) {
//                            dataList.add("");
//                            notifyItemInserted(dataList.size() - 1);
//                            notifyItemRangeChanged(currentPosition, dataList.size()); // Ensure correct item positions
//
//                            // Notify the activity about the new row count
//                            if (rowCountListener != null) {
//                                rowCountListener.onRowCountChanged(getValidChoiceCount());
//                            }
//                        }
//                    }
//                }
//
//                // Hide the keyboard
//                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        });

        /* keep the keyboard enabled after each row insertion */
        holder.editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // User exits the EditText
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                String text = holder.editText.getText().toString().trim();
                dataList.set(currentPosition, text);

                // Add a new row only if it's the last row and has content
                if (currentPosition == dataList.size() - 1 && !text.isEmpty()) {
                    dataList.add("");
                    notifyItemInserted(dataList.size() - 1);
                    notifyItemRangeChanged(currentPosition, dataList.size());

                    // Notify listener about row count change
                    if (rowCountListener != null) {
                        rowCountListener.onRowCountChanged(getValidChoiceCount());
                    }
                }

                // Move focus to the next row
                int nextPosition = currentPosition + 1;
                if (nextPosition < dataList.size()) {
                    holder.itemView.post(() -> {
                        RecyclerView.ViewHolder nextHolder = ((RecyclerView) holder.itemView.getParent()).findViewHolderForAdapterPosition(nextPosition);
                        if (nextHolder instanceof ViewHolder) {
                            ((ViewHolder) nextHolder).editText.requestFocus();
                        }
                    });
                }
            }
        });


        // Delete row
        holder.deleteIcon.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION && dataList.size() > 1) {
                dataList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, dataList.size()); // Ensure correct item positions

                // Notify the activity about the new row count
                if (rowCountListener != null) {
                    rowCountListener.onRowCountChanged(getValidChoiceCount());
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private int getValidChoiceCount(){
        return (int) dataList.stream().filter(s -> !s.isEmpty()).count();
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
