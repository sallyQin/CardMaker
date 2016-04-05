package sally.cardmaker.db;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import sally.cardmaker.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.Holder> implements LoaderManager.LoaderCallbacks<Cursor> {

    private FragmentActivity mActivity;
    private Cursor mCursor;

    public CardAdapter(FragmentActivity activity) {
        mActivity = activity;
        activity.getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mActivity, CardProvider.URI, null, null, null, Card.COLUMN_TIME + " DESC");
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (mCursor != data) {
            if (mCursor != null) {
                mCursor.close();
            }
            mCursor = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = null;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
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
