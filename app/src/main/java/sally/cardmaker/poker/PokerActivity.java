package sally.cardmaker.poker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import sally.cardmaker.R;
import sally.cardmaker.Utils;

public class PokerActivity extends AppCompatActivity implements View.OnClickListener {

    /** static variables */
    @SuppressWarnings("unused")
    private static final String TAG = "PokerActivity";
    private static final String KEY_SUIT = "suit";
    private static final String KEY_RANK = "rank";
    private static final String KEY_RECT = "rect";

    /** USER DATA -- saved member variables */
    CharSequence mSuit;
    CharSequence mRank;

    /** auto-initialized member variables */
    private PokerView mPokerView;
    private TextView mSuitButton;
    private TextView mRankButton;
    private PopupMenu mSuitPopup;
    private PopupMenu mRankPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** views */
        setContentView(R.layout.activity_poker);
        mSuitButton = (TextView) findViewById(R.id.suit);
        mRankButton = (TextView) findViewById(R.id.rank);
        mPokerView = (PokerView) findViewById(R.id.canvas);
        mPokerView.mImageUri = getIntent().getData();

        /** menus */
        mSuitPopup = new PopupMenu(this, mSuitButton);
        mRankPopup = new PopupMenu(this, mRankButton);

        MenuInflater inflater = getMenuInflater();
        Menu suitMenu = mSuitPopup.getMenu();
        Menu rankMenu = mRankPopup.getMenu();
        inflater.inflate(R.menu.poker_suit, suitMenu);
        inflater.inflate(R.menu.poker_rank, rankMenu);

        MenuItem heart = suitMenu.getItem(1);
        MenuItem diamond = suitMenu.getItem(2);
        Utils.colorItem(heart, Color.RED);      /** ♥ */
        Utils.colorItem(diamond, Color.RED);    /** ♦ */

        if (savedInstanceState != null) {
            mSuit = savedInstanceState.getCharSequence(KEY_SUIT);
            mRank = savedInstanceState.getCharSequence(KEY_RANK);
            mPokerView.mBitmapRect = savedInstanceState.getParcelable(KEY_RECT);
        } else {
            /** default */
            MenuItem spade = suitMenu.getItem(0);
            MenuItem ace = rankMenu.getItem(1);
            mSuit = spade.getTitle();   /** ♠ */
            mRank = ace.getTitle();     /** Ace */
        }
        onUserDataChanged();

        /** listeners */
        mSuitPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CharSequence title = item.getTitle();
                if (mSuit != title) {
                    mSuit = title;
                    onUserDataChanged();
                }
                return true;
            }
        });

        mRankPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CharSequence title = item.getTitle();
                if (mRank != title) {
                    mRank = title;
                    onUserDataChanged();
                }
                return true;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_SUIT, mSuit);
        outState.putCharSequence(KEY_RANK, mRank);
        outState.putParcelable(KEY_RECT, mPokerView.mBitmapRect);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suit:
                mSuitPopup.show();
                break;
            case R.id.rank:
                mRankPopup.show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.compress(mPokerView);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (mPokerView.mBitmap != null) {
            mPokerView.mBitmap.recycle();
        }
        super.onDestroy();
    }

    private void onUserDataChanged() {
        mSuitButton.setText(mSuit);
        mRankButton.setText(mRank);
        if (mSuit instanceof Spannable) {
            mRankButton.setTextColor(Color.RED);
        } else {
            mRankButton.setTextColor(Color.BLACK);
        }

        mPokerView.invalidate();
    }
}
