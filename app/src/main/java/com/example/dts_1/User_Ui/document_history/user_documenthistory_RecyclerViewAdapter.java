package com.example.dts_1.User_Ui.document_history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dts_1.User_Ui.document_history.user_documenthistory_PlaceholderContent.PlaceholderItem;
import com.example.dts_1.User_Ui.view_document.user_viewdocument_PlaceholderContent;
import com.example.dts_1.databinding.FragmentItemBinding;
import com.example.dts_1.databinding.UserDocumenthistoryItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class user_documenthistory_RecyclerViewAdapter extends RecyclerView.Adapter<user_documenthistory_RecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;
    private  OnItemClickListener listener;
    public user_documenthistory_RecyclerViewAdapter(List<PlaceholderItem> items, OnItemClickListener listener) {
        mValues = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(UserDocumenthistoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

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

        public ViewHolder(UserDocumenthistoryItemBinding binding) {
            super(binding.getRoot());
//            mIdView = binding.itemNumber;
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
        void onItemClick(user_documenthistory_PlaceholderContent.PlaceholderItem item);
    }
}