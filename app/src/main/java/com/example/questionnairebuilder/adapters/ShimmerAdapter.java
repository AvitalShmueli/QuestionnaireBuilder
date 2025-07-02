package com.example.questionnairebuilder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questionnairebuilder.R;

public class ShimmerAdapter extends RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder> {

    private int itemCount = 5; // Default number of shimmer items

    public ShimmerAdapter() {
        // Default constructor
    }

    public ShimmerAdapter(int itemCount) {
        this.itemCount = itemCount;
    }

    @NonNull
    @Override
    public ShimmerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shimmer_item_layout, parent, false);
        return new ShimmerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShimmerViewHolder holder, int position) {
        // No binding needed for shimmer items
        // The shimmer animation is handled by the ShimmerFrameLayout
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int count) {
        this.itemCount = count;
        notifyDataSetChanged();
    }

    public static class ShimmerViewHolder extends RecyclerView.ViewHolder {

        public ShimmerViewHolder(@NonNull View itemView) {
            super(itemView);
            // You can get references to views here if needed
            // For shimmer, usually not necessary
        }
    }
}
