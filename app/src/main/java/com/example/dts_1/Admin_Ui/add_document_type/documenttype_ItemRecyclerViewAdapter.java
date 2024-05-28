package com.example.dts_1.Admin_Ui.add_document_type;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dts_1.Admin_Ui.accesscontrol.accesscontrol_PlaceholderContent;
import com.example.dts_1.Admin_Ui.add_document_type.documenttype_PlaceholderContent.PlaceholderItem;
import com.example.dts_1.databinding.AdminDocumentTypeItemBinding;
import com.example.dts_1.databinding.FragmentItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class documenttype_ItemRecyclerViewAdapter extends RecyclerView.Adapter<documenttype_ItemRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public documenttype_ItemRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(AdminDocumentTypeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
        holder.mAuthorView.setText(mValues.get(position).content);
//        holder.mDetailsView.setText(mValues.get(position).details);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView mIdView;
        public final TextView mAuthorView;
//        public final TextView mDetailsView;
        public PlaceholderItem mItem;

        public ViewHolder(AdminDocumentTypeItemBinding binding) {
            super(binding.getRoot());
//            mIdView = binding.itemNumber;
            mAuthorView = binding.author;
//            mDetailsView = binding.details;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mAuthorView.getText() + "'";
        }
    }
}