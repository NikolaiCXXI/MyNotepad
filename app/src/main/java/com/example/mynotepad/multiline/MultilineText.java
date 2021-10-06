package com.example.mynotepad.multiline;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MultilineText implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String titleText;
    public String noteText;
    public String datetime;
}
