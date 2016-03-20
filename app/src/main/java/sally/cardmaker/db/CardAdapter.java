package sally.cardmaker.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import sally.cardmaker.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.Holder> {

    private SQLiteOpenHelper mHelper;
    private Cursor mCursor;

    public CardAdapter() {
        mHelper = new CardHelper();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        mCursor = db.query(CardHelper.TABLE_NAME, null, null, null, null, null, Card.COLUMN_TIME + " DESC");
    }

    public void add(@NonNull Uri uri) {
        String path = uri.getPath();
        File file = new File(path);

        ContentValues contentValues = new ContentValues();
        contentValues.put(Card.COLUMN_PATH, path);
        contentValues.put(Card.COLUMN_TIME, file.lastModified());

        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.insert(CardHelper.TABLE_NAME, null, contentValues);

        mCursor.requery();
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (mCursor.moveToPosition(position)) {
            Card card = new Card(mCursor);
            File file = new File(card.path);
            Uri uri = Uri.fromFile(file);

            SimpleDraweeView itemView = (SimpleDraweeView) holder.itemView;
            itemView.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {  /** normal case */
            return mCursor.getCount();
        } else {                /** mCursor == null, rare case */
            return 0;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
