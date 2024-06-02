package com.example.dts_1.User_Ui.pending;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dts_1.User_Ui.pending.user_pending_PlaceholderContent.PlaceholderItem;
import com.example.dts_1.User_Ui.view_document.user_viewdocument_PlaceholderContent;
import com.example.dts_1.User_Ui.view_document.user_viewdocument_RecyclerViewAdapter;
import com.example.dts_1.databinding.FragmentItemBinding;
import com.example.dts_1.databinding.UserPendingItemBinding;
import com.example.dts_1.databinding.UserViewdocumentItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class user_pending_RecyclerViewAdapter extends RecyclerView.Adapter<user_pending_RecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;
    private final OnItemClickListener listener;

    public user_pending_RecyclerViewAdapter(List<PlaceholderItem> items, OnItemClickListener listener) {
        mValues = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(UserPendingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
        holder.mAuthorView.setText(mValues.get(position).author);
        holder.mDetailsView.setText(mValues.get(position).documentDescription);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView mIdView;
        public final TextView mAuthorView;
        public final TextView mDetailsView;
        public PlaceholderItem mItem;

        public ViewHolder(UserPendingItemBinding binding) {
            super(binding.getRoot());
            mAuthorView = binding.author;
            mDetailsView = binding.details;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(mValues.get(position));
                    }
                }
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mAuthorView.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PlaceholderItem item);
    }
}