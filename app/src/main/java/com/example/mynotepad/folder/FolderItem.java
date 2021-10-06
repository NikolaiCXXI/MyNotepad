package com.example.mynotepad.folder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class FolderItem {
    public int id_image;
    public long id_db;
    public String title;
    public String date;

    public FolderItem(int id_image, long id_db, String title, String date) {
        this.id_image = id_image;
        this.id_db = id_db;
        this.title = title;
        this.date = date;
    }

    //по убыванию
    public static Comparator<FolderItem> CompareByDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ROOT);
        return (o1, o2) -> {
            Date date1 = null,
                    date2 = null;
            try {
                date1 = sdf.parse(o1.date);
                date2 = sdf.parse(o2.date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert date2 != null;
            return date2.compareTo(date1);
        };
    }
}
