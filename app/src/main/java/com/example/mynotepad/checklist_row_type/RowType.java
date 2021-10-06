package com.example.mynotepad.checklist_row_type;

public interface RowType {
    int TITLE_ROW_TYPE = 0;
    int CHECKLIST_ROW_TYPE = 1;
    int BUTTON_PLUS_ROW_TYPE = 2;

    int getItemViewType();
}
