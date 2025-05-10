package com.example.questionnairebuilder.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.example.questionnairebuilder.interfaces.Callback_questionSelected;
import com.example.questionnairebuilder.listeners.OnStartDragListener;
import com.example.questionnairebuilder.models.Question;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    private List<Question> questionList;
    private Callback_questionSelected callback_questionSelected;
    private boolean reorderEnabled = false;
    private OnStartDragListener startDragListener;
    private final Set<Question> questionsToUpdate = new HashSet<>();


    public QuestionsAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    public QuestionsAdapter setCallbackQuestionSelected(Callback_questionSelected callback_questionSelected) {
        this.callback_questionSelected = callback_questionSelected;
        return this;
    }

    public QuestionsAdapter setOnStartDragListener(OnStartDragListener startDragListener) {
        this.startDragListener = startDragListener;
        return this;
    }

    public void updateQuestions(List<Question> newQuestions) {
        this.questionList = newQuestions;
        notifyDataSetChanged();
    }

    public void setReorderEnabled(boolean enabled){
        this.reorderEnabled = enabled;
        notifyItemRangeChanged(0, getItemCount(), "toggleDrag");
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position,  @NonNull List<Object> payloads) {
        if (payloads.contains("toggleDrag")) {
            holder.dragHandle.setVisibility(reorderEnabled ? VISIBLE : GONE);
            return;
        }

        if (!questionList.isEmpty()) {
            Question question = questionList.get(position);
            int order = question.getOrder();
            holder.rv_mandatory_star.setVisibility(question.isMandatory()? VISIBLE : GONE);
            holder.rv_title.setText(question.getQuestionTitle() + " | " + order);
            holder.dragHandle.setVisibility(reorderEnabled ? VISIBLE : GONE);

            holder.item_CARD_data.setOnClickListener(v -> {
                if (callback_questionSelected != null)
                    callback_questionSelected.select(question);
            });

            setupDragHandleTouch(holder.dragHandle, holder);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDragHandleTouch(ShapeableImageView dragHandle, RecyclerView.ViewHolder holder) {
        dragHandle.setOnTouchListener((v, event) -> {
            if (reorderEnabled && event.getActionMasked() == MotionEvent.ACTION_DOWN && startDragListener != null) {
                startDragListener.onStartDrag(holder);
                v.performClick(); // good practice for accessibility
            }
            return false;
        });
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onBindViewHolder(holder, position, new ArrayList<>());
    }

    @Override
    public int getItemCount() {
        return questionList == null ? 0 : questionList.size();
    }

    public Question getQuestionAt(int position) {
        return questionList.get(position);
    }

    public Set<Question> getQuestionsToUpdate() {
        return questionsToUpdate;
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(questionList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView item_CARD_data;
        private MaterialTextView rv_title;
        private MaterialTextView rv_mandatory_star;
        private ShapeableImageView dragHandle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_CARD_data = itemView.findViewById(R.id.item_CARD_data);
            rv_title = itemView.findViewById(R.id.rv_title);
            rv_mandatory_star = itemView.findViewById(R.id.rv_mandatory_star);
            dragHandle = itemView.findViewById(R.id.rv_dragHandle);
        }
    }
}
