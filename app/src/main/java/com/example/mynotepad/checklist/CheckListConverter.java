package com.example.mynotepad.checklist;

import com.example.mynotepad.checklist_row_type.ButtonPlus;
import com.example.mynotepad.checklist_row_type.CheckListItem;
import com.example.mynotepad.checklist_row_type.RowType;
import com.example.mynotepad.checklist_row_type.TitleCheckNotes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.room.TypeConverter;

public class CheckListConverter {
    @TypeConverter
    public String listToString(List<RowType> list) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int k = list.get(i).getItemViewType();
            switch (k) {
                case RowType.TITLE_ROW_TYPE:
                    strings.add(((TitleCheckNotes) list.get(i)).titleText);
                    break;
                case RowType.CHECKLIST_ROW_TYPE:
                    strings.add(((CheckListItem) list.get(i)).checkBox ? "1" : "0");
                    strings.add(((CheckListItem) list.get(i)).noteText);
                    break;
                //кнопка ButtonPlus всегда будет в самом конце
                case RowType.BUTTON_PLUS_ROW_TYPE:
                    //strings.add(null);
                    break;
            }
        }
        Gson gson = new Gson();
        return gson.toJson(strings);
    }

    @TypeConverter
    public List<RowType> stringToList(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> strings = new Gson().fromJson(value, listType);
        List<RowType> list = new ArrayList<>();
        //алгоритм рассчитан на то, что в полученном массиве нет ButtonPlus
        TitleCheckNotes titleCheckNotes;
        CheckListItem checkListItem;
        if (strings.size() % 2 == 0)
            for (int i = 0; i < strings.size(); i += 2) {
                checkListItem = new CheckListItem();
                checkListItem.checkBox = Objects.equals(strings.get(i), "1");
                checkListItem.noteText = strings.get(i + 1);
                list.add(checkListItem);
            }
        else {
            titleCheckNotes = new TitleCheckNotes();
            titleCheckNotes.titleText = strings.get(0);
            list.add(titleCheckNotes);
            for (int i = 1; i < strings.size(); i += 2) {
                checkListItem = new CheckListItem();
                checkListItem.checkBox = Objects.equals(strings.get(i), "1");
                checkListItem.noteText = strings.get(i + 1);
                list.add(checkListItem);
            }
            list.add(new ButtonPlus());
        }
        return list;
    }
}
