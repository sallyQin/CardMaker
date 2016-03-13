package sally.cardmaker;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.Holder> {

    private List<Uri> mUris = new ArrayList<>();

    public void add(@NonNull Uri uri) {
        mUris.add(0, uri);  /** insert to the first position */
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
        SimpleDraweeView itemView = (SimpleDraweeView) holder.itemView;
        itemView.setImageURI(mUris.get(position));
    }

    @Override
    public int getItemCount() {
        return mUris.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
