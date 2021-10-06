package com.example.mynotepad.checklist;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.mynotepad.MainActivity;
import com.example.mynotepad.R;
import com.example.mynotepad.databinding.ItemChecklistBtnPlusBinding;
import com.example.mynotepad.databinding.ItemChecklistNoteBinding;
import com.example.mynotepad.databinding.ItemChecklistTitleBinding;
import com.example.mynotepad.checklist_row_type.ButtonPlus;
import com.example.mynotepad.checklist_row_type.CheckListItem;
import com.example.mynotepad.checklist_row_type.RowType;
import com.example.mynotepad.checklist_row_type.TitleCheckNotes;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class CheckNotesAdapter extends ListAdapter<CheckListFragment, RecyclerView.ViewHolder> {

    //пригодный для сериализации List, содержащий Title, Note1..n, ButtonPlus
    List<RowType> data;
    private CheckListFragment checkList;

    CheckNotesAdapter(List<RowType> data, CheckListFragment checkList) {
        super(CheckListFragment.DIFF_CALLBACK);
        this.data = data;
        this.checkList = checkList;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof TitleCheckNotes)
            return RowType.TITLE_ROW_TYPE;
        else if (data.get(position) instanceof CheckListItem)
            return RowType.CHECKLIST_ROW_TYPE;
        else if (data.get(position) instanceof ButtonPlus)
            return RowType.BUTTON_PLUS_ROW_TYPE;
        else return -1;
    }

    private View.OnClickListener getOnClickListener() {
        return v -> checkList.addCheckNote();
    }

    private final View.OnFocusChangeListener getFocusListener = (view, b) -> {
        if (b) {
            checkList.addCheckNote();
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case RowType.TITLE_ROW_TYPE:
                ItemChecklistTitleBinding titleChecklistBinding = DataBindingUtil.inflate(inflater, R.layout.item_checklist_title, parent, false);
                return new TitleViewHolder(titleChecklistBinding);
            case RowType.CHECKLIST_ROW_TYPE:
                ItemChecklistNoteBinding checkListItemBinding = DataBindingUtil.inflate(inflater, R.layout.item_checklist_note, parent, false);
                return new CheckListViewHolder(checkListItemBinding);
            default:
                ItemChecklistBtnPlusBinding itemChecklistBtnPlusBinding = ItemChecklistBtnPlusBinding.inflate(inflater, parent, false);
                return new ButtonViewHolder(itemChecklistBtnPlusBinding);
        }
    }

    //инициализация каждого ViewHolder значениями из массива data
    //Вьюха в OnCreateView добавляется тогда, когда до неё дошла позиция в OnBindViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int k = getItemViewType(position);
        switch (k) {
            case RowType.TITLE_ROW_TYPE:
                ((TitleViewHolder) holder).bind((TitleCheckNotes) data.get(position));
                break;
            case RowType.CHECKLIST_ROW_TYPE:
                ((CheckListViewHolder) holder).bind((CheckListItem) data.get(position));
                /*if (position == data.size() - 2)
                    ((CheckListViewHolder) holder).checkListItemBinding.noteCheckList.setOnFocusChangeListener(getFocusListener);*/
                break;
            case RowType.BUTTON_PLUS_ROW_TYPE:
                //чзх
                ((ButtonViewHolder) holder).button.setOnClickListener(getOnClickListener());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class TitleViewHolder extends RecyclerView.ViewHolder {
        ItemChecklistTitleBinding titleChecklistBinding;

        public TitleViewHolder(ItemChecklistTitleBinding titleChecklistBinding) {
            super(titleChecklistBinding.getRoot());
            this.titleChecklistBinding = titleChecklistBinding;
        }

        public void bind(TitleCheckNotes titleCheckNotes) {
            titleChecklistBinding.setTitleCheckNotes(titleCheckNotes);
            titleChecklistBinding.titleTextCheckList.setTextSize(Math.round(MainActivity.mScaleFactor * 52));
            titleChecklistBinding.executePendingBindings();
        }
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        public ImageButton button;

        public ButtonViewHolder(ItemChecklistBtnPlusBinding binding) {
            super(binding.getRoot());
            button = binding.buttonPlus;
        }
    }

    public static class CheckListViewHolder extends RecyclerView.ViewHolder {
        //нет надобности хранить notetext и checkbox, так как используется двусторонний data binding?
        ItemChecklistNoteBinding checkListItemBinding;

        public CheckListViewHolder(ItemChecklistNoteBinding checkListItemBinding) {
            super(checkListItemBinding.getRoot());
            this.checkListItemBinding = checkListItemBinding;
        }

        public void bind(CheckListItem checkListItem) {
            checkListItemBinding.setCheckListItem(checkListItem);
            checkListItemBinding.noteCheckList.setTextSize(Math.round(MainActivity.mScaleFactor * 39));
            checkListItemBinding.executePendingBindings();
        }
    }
}


