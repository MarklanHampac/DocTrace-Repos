package com.example.dts_1.Admin_Ui.view_document;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dts_1.Admin_Ui.view_document.viewdocument_PlaceholderContent.PlaceholderItem;
import com.example.dts_1.databinding.AdminViewDocumentItemBinding;


import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class viewdocument_RecyclerViewAdapter extends RecyclerView.Adapter<viewdocument_RecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public viewdocument_RecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(AdminViewDocumentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
        holder.mDocumentTypeNameView.setText(mValues.get(position).documentTypeName);
        holder.mDocumentAuthorView.setText(mValues.get(position).author);
        holder.mDocumentAuthorView.setText(mValues.get(position).documentDescription);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView mIdView;
        public final TextView mDocumentTypeNameView;
        public final TextView mDocumentAuthorView;
        public final TextView mDocumentDescriptionView;
        public PlaceholderItem mItem;

        public ViewHolder(AdminViewDocumentItemBinding binding) {
            super(binding.getRoot());
//            mIdView = binding.itemNumber;
            mDocumentTypeNameView = binding.documentTypeNameText;
            mDocumentAuthorView = binding.documentAuthorText;
            mDocumentDescriptionView = binding.documentDescriptionText;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mDocumentTypeNameView.getText() + "'";
        }
    }
}