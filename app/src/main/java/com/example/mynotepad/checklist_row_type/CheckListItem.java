package com.example.mynotepad.checklist_row_type;

import com.example.mynotepad.MainActivity;

public class CheckListItem implements RowType {
    public Boolean checkBox;
    public String noteText;

    public CheckListItem() {
        checkBox = false;
        noteText = "";
    }

    @Override
    public int getItemViewType() {
        return RowType.CHECKLIST_ROW_TYPE;
    }
}
