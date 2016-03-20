package sally.cardmaker.db;

import android.database.Cursor;
import android.support.annotation.NonNull;

public class Card {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_SRC = "src";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_TIME = "time";

    public int id;
    public String category;
    public String src;
    public String path;
    public String key;
    public long time;

    public Card(@NonNull Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
        src = cursor.getString(cursor.getColumnIndex(COLUMN_SRC));
        path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
        key = cursor.getString(cursor.getColumnIndex(COLUMN_KEY));
        time = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME));
    }
}
