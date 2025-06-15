package com.example.questionnairebuilder.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
    private RecyclerView recyclerView;

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public ChoicesAdapter(OnRowCountChangeListener listener) {
        this.dataList = new ArrayList<>();
        this.dataList.add(""); // Initial empty row
        this.rowCountListener = listener;
        this.isFixedMode = false;
    }

    public ChoicesAdapter(OnRowCountChangeListener listener, @NonNull List<String> choices, boolean isFixedMode) {
        this.dataList = new ArrayList<>(choices);
        this.rowCountListener = listener;
        this.isFixedMode = isFixedMode;
        if (!isFixedMode) {
            dataList.add(""); // Add initial empty row if no predefined choices
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.rowItemEditText.setText(dataList.get(position));
        holder.rowItemEditText.setEnabled(!isFixedMode);
        holder.rowItemEditText.setError(null);

        // Remove existing TextWatcher if any
        if (holder.textWatcher != null) {
            holder.rowItemEditText.removeTextChangedListener(holder.textWatcher);
        }

        // Create and attach new TextWatcher
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                String text = s.toString();
                if (position < dataList.size()) {
                    dataList.set(position, text);
                }
            }
        };
        holder.rowItemEditText.addTextChangedListener(watcher);
        holder.textWatcher = watcher;

        holder.rowItemEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return false;

                String text = holder.rowItemEditText.getText().toString();
                if (isDuplicate(text, currentPosition)) {
                    holder.rowItemEditText.setError("Duplicate choice");
                    return true;
                } else {
                    holder.rowItemEditText.setError(null);
                }

                if (currentPosition >= 0 && currentPosition < dataList.size()) {
                    dataList.set(currentPosition, text);
                }

                if (currentPosition == dataList.size() - 1) {
                    // If it's the last item, hide the keyboard and clear focus
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    v.clearFocus();
                } else {
                    // Move focus to the next row
                    int nextPosition = currentPosition + 1;
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

        holder.rowItemEditText.setOnFocusChangeListener((v, hasFocus) -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            if (hasFocus) {
                // If this is the last row, add a new one (but only if it's not already added)
                if (currentPosition == dataList.size() - 1) {
                    String currentText = dataList.get(currentPosition);
                    dataList.add("");
                    notifyItemInserted(dataList.size() - 1);

                    if (rowCountListener != null) {
                        rowCountListener.onRowCountChanged(getValidChoiceCount());
                    }
                }

                // Update IME options: last row should have "Done", others should have "Next"
                if (currentPosition == dataList.size() - 1) {
                    holder.rowItemEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else {
                    holder.rowItemEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                }
            }
            else{
                String text = holder.rowItemEditText.getText().toString();
                if (isDuplicate(text, currentPosition)) {
                    holder.rowItemEditText.setError("Duplicate choice");
                    return;
                } else {
                    holder.rowItemEditText.setError(null);
                }

                if (currentPosition >= 0 && currentPosition < dataList.size()) {
                    dataList.set(currentPosition, text);
                }

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
            }
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

    private boolean isDuplicate(String text, int currentIndex) {
        String normalized = text.trim().toLowerCase();
        if (normalized.isEmpty()) return false;
        for (int i = 0; i < dataList.size(); i++) {
            if (i == currentIndex) continue;
            if (dataList.get(i).trim().equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasValidationError() {
        for (int i = 0; i < dataList.size(); i++) {
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
            if (holder instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder) holder;
                CharSequence error = viewHolder.rowItemEditText.getError();
                if (error != null && error.length() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private int getValidChoiceCount(){
        return (int) dataList.stream().filter(s -> !s.isEmpty()).count();
    }

    /**
     * @return Arraylist of non-empty choices
     */
    public ArrayList<String> getDataList() {
        return dataList.stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText rowItemEditText;
        ShapeableImageView deleteIcon;
        TextWatcher textWatcher;

        public ViewHolder(View itemView) {
            super(itemView);
            rowItemEditText = itemView.findViewById(R.id.row_TXT_item);
            deleteIcon = itemView.findViewById(R.id.row_IC_delete);
        }
    }
}
