package com.example.questionnairebuilder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ResponseViewHolder> {

    private List<String> responses;

    public ResponseAdapter(List<String> responses) {
        this.responses = responses;
    }

    @NonNull
    @Override
    public ResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_response, parent, false);
        return new ResponseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseViewHolder holder, int position) {
        holder.responseText.setText(responses.get(position));
    }

    @Override
    public int getItemCount() {
        return responses.size();
    }

    static class ResponseViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView responseText;

        public ResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            responseText = itemView.findViewById(R.id.itemResponse_LBL_text);
        }
    }
}
