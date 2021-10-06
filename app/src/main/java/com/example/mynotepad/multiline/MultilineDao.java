package com.example.mynotepad.multiline;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MultilineDao {
    @Query("SELECT * FROM multilinetext")
    List<MultilineText> getAll();
    @Query("SELECT * FROM multilinetext WHERE id = :id")
    MultilineText getById(long id);
    @Insert(onConflict = REPLACE)
    long insert(MultilineText multilineText);
    @Update(onConflict = REPLACE)
    void update(MultilineText multilineText);
    @Delete
    void delete(MultilineText multilineText);
}
