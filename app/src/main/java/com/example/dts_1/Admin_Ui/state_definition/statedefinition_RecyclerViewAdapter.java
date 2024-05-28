package com.example.dts_1.Admin_Ui.state_definition;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dts_1.Admin_Ui.state_definition.statedefinition_PlaceholderContent.PlaceholderItem;
import com.example.dts_1.databinding.AdminStateDefinitionItemBinding;
import com.example.dts_1.databinding.FragmentItemBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class statedefinition_RecyclerViewAdapter extends RecyclerView.Adapter<statedefinition_RecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public statedefinition_RecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(AdminStateDefinitionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
        holder.mAuthorView.setText(mValues.get(position).content);
        holder.mDetailsView.setText(mValues.get(position).details);
        holder.mDocumentTypeNameView.setText(mValues.get(position).documentTypeName);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView mIdView;
        public final TextView mAuthorView;
        public final TextView mDetailsView;
        public final TextView mDocumentTypeNameView;
        public PlaceholderItem mItem;

        public ViewHolder(AdminStateDefinitionItemBinding binding) {
            super(binding.getRoot());
//            mIdView = binding.itemNumber;
            mAuthorView = binding.author;
            mDetailsView = binding.details;
            mDocumentTypeNameView = binding.documentTypeName;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mAuthorView.getText() + "'";
        }
    }
}