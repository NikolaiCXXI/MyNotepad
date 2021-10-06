package com.example.mynotepad.checklist;

import com.example.mynotepad.checklist_row_type.RowType;

import java.io.Serializable;
import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CheckListText implements Serializable {
    //генерится с 1
    @PrimaryKey(autoGenerate = true)
    public long id;

    public List<RowType> Notes;

    public String datetime;

    public CheckListText(){}

}
