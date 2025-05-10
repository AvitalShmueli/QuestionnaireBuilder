package com.example.questionnairebuilder.interfaces;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.adapters.QuestionsAdapter;
import com.example.questionnairebuilder.models.Question;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final QuestionsAdapter adapter;

    public ItemMoveCallback(QuestionsAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false; // we use the drag handle instead
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        adapter.onItemMove(
                viewHolder.getAdapterPosition(),
                target.getAdapterPosition()
        );
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Not used
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        // Update the order field in each Question
        for (int i = 0; i < adapter.getItemCount(); i++) {
            Question q = adapter.getQuestionAt(i);
            int newOrder = i + 1;
            if (q.getOrder() != newOrder) {
                q.setOrder(newOrder);
                adapter.notifyItemChanged(i);
                adapter.getQuestionsToUpdate().add(q);
            }
        }

        Log.d("pttt","changed questions: "+adapter.getQuestionsToUpdate());
    }

}

