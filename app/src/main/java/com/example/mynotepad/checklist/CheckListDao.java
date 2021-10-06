package com.example.mynotepad.checklist;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CheckListDao {
    @Query("SELECT * FROM checklisttext")
    List<CheckListText> getAll();
    @Query("SELECT * FROM checklisttext WHERE id = :id")
    CheckListText getById(long id);
    @Insert(onConflict = REPLACE)
    long insert(CheckListText checklisttext);
    @Update
    void update(CheckListText checklisttext);
    @Delete
    void delete(CheckListText checklisttext);
}
