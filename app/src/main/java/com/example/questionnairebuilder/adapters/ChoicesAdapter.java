package com.example.questionnairebuilder.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.listeners.OnRowCountChangeListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.ViewHolder> {

    private final ArrayList<String> dataList;
    private OnRowCountChangeListener rowCountListener;
    private final boolean isFixedMode;

    public ChoicesAdapter(OnRowCountChangeListener listener) {
        this.dataList = new ArrayList<>();
        this.dataList.add(""); // Initial empty row
        this.rowCountListener = listener;
        this.isFixedMode = false;
    }

    public ChoicesAdapter(OnRowCountChangeListener listener, @NonNull List<String> predefinedChoices) {
        this.dataList = new ArrayList<>(predefinedChoices);
        //this.dataList.add(""); // Initial empty row
        this.rowCountListener = listener;
        this.isFixedMode = !predefinedChoices.isEmpty();
        if (!isFixedMode) {
            dataList.add(""); // Add initial empty row if no predefined choices
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rowItemEditText.setText(dataList.get(position));
        holder.rowItemEditText.setEnabled(!isFixedMode);

        /* keep the keyboard enabled after each row insertion */
        holder.rowItemEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return false;

                String text = holder.rowItemEditText.getText().toString();
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
                        RecyclerView recyclerView = (RecyclerView) holder.itemView.getParent();
                        RecyclerView.ViewHolder nextHolder = recyclerView.findViewHolderForAdapterPosition(nextPosition);
                        if (nextHolder instanceof ViewHolder) {
                            ViewHolder nextViewHolder = (ViewHolder) nextHolder;
                            nextViewHolder.rowItemEditText.requestFocus();

                            // Show keyboard
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(nextViewHolder.rowItemEditText, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                    });
                }

                return true; // Consume the "Done" key event
            }
            return false;
        });

        holder.deleteIcon.setVisibility(!isFixedMode? VISIBLE : GONE);
        holder.deleteIcon.setEnabled(!isFixedMode && dataList.size() > 1);
        holder.deleteIcon.setAlpha(!isFixedMode && dataList.size() > 1 ? 1.0f : 0.5f); // Dim icon if disabled

        // Delete row
        holder.deleteIcon.setOnClickListener(v -> {
            if (!isFixedMode) {
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


    public ArrayList<String> getDataList() {
        //return dataList;
        /*
        ArrayList<String> tmpList = new ArrayList<>();
        for(String s : dataList)
            if(!s.isEmpty())
                tmpList.add(s);
        return tmpList;
        */
        return dataList.stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText rowItemEditText;
        ShapeableImageView deleteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            rowItemEditText = itemView.findViewById(R.id.row_TXT_item);
            deleteIcon = itemView.findViewById(R.id.row_IC_delete);
        }
    }
}
