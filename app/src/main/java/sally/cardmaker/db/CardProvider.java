package sally.cardmaker.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

@SuppressWarnings("ConstantConditions")
public class CardProvider extends ContentProvider {

    @SuppressWarnings("SpellCheckingInspection")
    static final Uri URI = Uri.parse("content://sally.cardmaker");
    static final String TABLE_NAME = "cards";

    private Helper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new Helper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = mHelper.getReadableDatabase().query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        mHelper.getReadableDatabase().insert(TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private static class Helper extends SQLiteOpenHelper {

        public Helper(Context context) {
            super(context, new File(context.getFilesDir(), "cards.db").getPath(), null, 1);
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
}
