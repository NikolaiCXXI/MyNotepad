package com.example.mynotepad.folder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynotepad.App;
import com.example.mynotepad.MainActivity;
import com.example.mynotepad.R;
import com.example.mynotepad.databinding.ItemFolderBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FolderItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FolderItem> data;
    private OnFolderListener mOnFolderListener;

    FolderItemsAdapter(List<FolderItem> data, OnFolderListener onFolderListener) {
        this.data = data;
        this.mOnFolderListener = onFolderListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFolderBinding itemFolderBinding = ItemFolderBinding.inflate(layoutInflater, parent, false);
        return new ItemViewHolder(itemFolderBinding, mOnFolderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageFolder;
        public TextView title;
        public TextView date;
        OnFolderListener onFolderListener;

        public ItemViewHolder(ItemFolderBinding itemFolderBinding, OnFolderListener onFolderListener) {
            super(itemFolderBinding.getRoot());
            imageFolder = itemFolderBinding.buttonFolderItem;
            title = itemFolderBinding.titleFolderItem;
            date = itemFolderBinding.dateFolderItem;
            this.onFolderListener = onFolderListener;
            itemView.setOnClickListener(this);
        }

        public void bind(FolderItem foldersItem) {
            imageFolder.setImageResource(foldersItem.id_image);
            title.setTextSize(Math.round(MainActivity.mScaleFactor * 39));
            date.setTextSize(Math.round(MainActivity.mScaleFactor * 31));
            title.setText(foldersItem.title);
            date.setText(App.getInstance().getString(R.string.upd_str) + " " + foldersItem.date.substring(0, 14));
        }

        @Override
        public void onClick(View v) {
            onFolderListener.onFolderClick(getBindingAdapterPosition());
        }
    }

    public interface OnFolderListener {
        void onFolderClick(int position);
    }
}
