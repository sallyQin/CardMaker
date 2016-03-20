package sally.cardmaker.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import sally.cardmaker.App;

public class CardHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "cards";

    public CardHelper() {
        super(App.context(), new File(App.context().getFilesDir(), "cards.db").getPath(), null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + Card.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + Card.COLUMN_CATEGORY + " VARCHAR,"
                + Card.COLUMN_SRC + " VARCHAR,"
                + Card.COLUMN_PATH + " VARCHAR,"
                + Card.COLUMN_KEY + " VARCHAR,"
                + Card.COLUMN_TIME + " BIGINT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
