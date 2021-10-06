package com.example.mynotepad;

import com.example.mynotepad.multiline.MultilineDao;
import com.example.mynotepad.multiline.MultilineText;
import com.example.mynotepad.checklist.CheckListConverter;
import com.example.mynotepad.checklist.CheckListDao;
import com.example.mynotepad.checklist.CheckListText;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {MultilineText.class, CheckListText.class}, version = 1)
@TypeConverters({CheckListConverter.class})
//provides warning while build
public abstract class AppDatabase extends RoomDatabase {
    //абстрактные методы для получения Dao объектов
    public abstract MultilineDao multilineDao();
    public abstract CheckListDao checkListDao();
}
