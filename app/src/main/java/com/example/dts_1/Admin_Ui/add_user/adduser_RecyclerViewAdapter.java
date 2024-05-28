package com.example.dts_1.Admin_Ui.add_user;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dts_1.Admin_Ui.add_user.adduser_PlaceholderContent.PlaceholderItem;
import com.example.dts_1.databinding.AdminAddUserItemBinding;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class adduser_RecyclerViewAdapter extends RecyclerView.Adapter<adduser_RecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public adduser_RecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(AdminAddUserItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
        holder.mAuthorView.setText(mValues.get(position).content);
        holder.mDetailsView.setText(mValues.get(position).details);
        holder.mAccessTypeView.setText(mValues.get(position).accessType);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView mIdView;
        public final TextView mAuthorView;
        public final TextView mDetailsView;
        public final TextView mAccessTypeView;
        public PlaceholderItem mItem;

        public ViewHolder(AdminAddUserItemBinding binding) {
            super(binding.getRoot());
//            mIdView = binding.itemNumber;
            mAuthorView = binding.author;
            mDetailsView = binding.details;
            mAccessTypeView = binding.accessType;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mAuthorView.getText() + "'";
        }
    }
}