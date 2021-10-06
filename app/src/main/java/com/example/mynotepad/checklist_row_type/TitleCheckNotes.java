package com.example.mynotepad.checklist_row_type;

public class TitleCheckNotes implements RowType {
    public String titleText;

    public TitleCheckNotes() {
        titleText = "";
    }

    @Override
    public int getItemViewType() {
        return RowType.TITLE_ROW_TYPE;
    }
}
