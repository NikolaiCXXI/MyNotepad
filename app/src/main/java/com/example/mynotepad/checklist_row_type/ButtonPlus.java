package com.example.mynotepad.checklist_row_type;

public class ButtonPlus implements RowType {

    public ButtonPlus() {
    }

    @Override
    public int getItemViewType() {
        return RowType.BUTTON_PLUS_ROW_TYPE;
    }
}
