package com.example.mynotepad.checklist;

import com.example.mynotepad.checklist_row_type.RowType;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class CheckListDIffUtil extends DiffUtil.Callback {
    private final List<RowType> oldList;
    private final List<RowType> newList;

    public CheckListDIffUtil(List<RowType> oldList, List<RowType> newList) {
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

    //одного ли типа заметки?
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        RowType oldItem = oldList.get(oldItemPosition);
        RowType newItem = newList.get(newItemPosition);
        return oldItem.getItemViewType() == newItem.getItemViewType();
    }

    //одинаково ли содержание заметок?
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        RowType oldItem = oldList.get(oldItemPosition);
        RowType newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
