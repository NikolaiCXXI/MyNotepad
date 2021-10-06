package com.example.mynotepad.folder;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;



public class FolderDiffUtil extends DiffUtil.Callback{

    private final List<FolderItem> oldList;
    private final List<FolderItem> newList;

    public FolderDiffUtil(List<FolderItem> oldList, List<FolderItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    //для multiline и checklist один и тот же тип данных
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        FolderItem oldItem = oldList.get(oldItemPosition);
        FolderItem newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
