package sally.cardmaker;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.ScaleGestureDetector;

public class BitmapRect extends ScaleGestureDetector.SimpleOnScaleGestureListener implements Parcelable {

    @SuppressWarnings("unused")
    private static final String TAG = "BitmapRect";

    /** USER DATA -- saved member variables */
    public Rect mRect;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mWindowWidth;
    private int mWindowHeight;
    private float mMinScale;
    private float mMaxScale;
    private float mScale;

    /** no need to save */
    private OnChangeListener mListener;
    private float mX;
    private float mY;

    public BitmapRect(Bitmap bitmap, Rect window) {
        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
        mWindowWidth = window.width();
        mWindowHeight = window.height();

        mScale = mMaxScale = Math.min((float) mBitmapWidth / mWindowWidth, (float) mBitmapHeight / mWindowHeight);
        mMinScale = Math.min(0.25f, mMaxScale);

        int left = (int) ((mBitmapWidth - mWindowWidth * mScale) / 2);
        int top = (int) ((mBitmapHeight - mWindowHeight * mScale) / 2);
        mRect = new Rect(left, top, mBitmapWidth - left, mBitmapHeight - top);
    }

    public void setOnChangeListener(@Nullable OnChangeListener listener) {
        mListener = listener;
    }

    public void updatePosition(float x, float y) {
        mX = x;
        mY = y;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = detector.getScaleFactor();
        if (scale != 1) {
            scale = Math.max(mMinScale, Math.min(mMaxScale, mScale / scale));
            if (scale != mScale) {
                float diff = scale - mScale;
                mRect.left = (int) (mRect.left - diff * mX + 0.5f);
                mRect.top = (int) (mRect.top - diff * mY + 0.5f);
                mRect.right = (int) (mRect.left + scale * mWindowWidth);
                mRect.bottom = (int) (mRect.top + scale * mWindowHeight);
                mScale = scale;
                checkBounds();
                notifyChange();
            }
        }
        return true;
    }

    public void onMove(float x, float y) {
        float dx = x - mX;
        float dy = y - mY;
        if (dx != 0 && dy != 0) {
            int width = mRect.width();
            int height = mRect.height();
            int oldLeft = mRect.left;
            int oldTop = mRect.top;

            mRect.left = (int) (mRect.left - mScale * dx + 0.5f);
            mRect.top = (int) (mRect.top - mScale * dy + 0.5f);
            mRect.right = mRect.left + width;
            mRect.bottom = mRect.top + height;
            checkBounds();

            if (mRect.left != oldLeft ||  mRect.top != oldTop) {
                notifyChange();
            }
        }
    }

    private void checkBounds() {
        int width = mRect.width();
        int height = mRect.height();
        mRect.left = Math.max(0, Math.min(mRect.left, mBitmapWidth - width));
        mRect.top = Math.max(0, Math.min(mRect.top, mBitmapHeight - height));
        mRect.right = mRect.left + width;
        mRect.bottom = mRect.top + height;
    }

    private void notifyChange() {
        if (mListener != null) {
            mListener.onChange();
        }
    }

    protected BitmapRect(Parcel in) {
        mRect = in.readParcelable(Rect.class.getClassLoader());
        mBitmapWidth = in.readInt();
        mBitmapHeight = in.readInt();
        mWindowWidth = in.readInt();
        mWindowHeight = in.readInt();
        mMinScale = in.readFloat();
        mMaxScale = in.readFloat();
        mScale = in.readFloat();
    }

    public static final Creator<BitmapRect> CREATOR = new Creator<BitmapRect>() {
        @Override
        public BitmapRect createFromParcel(Parcel in) {
            return new BitmapRect(in);
        }

        @Override
        public BitmapRect[] newArray(int size) {
            return new BitmapRect[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mRect, i);
        parcel.writeInt(mBitmapWidth);
        parcel.writeInt(mBitmapHeight);
        parcel.writeInt(mWindowWidth);
        parcel.writeInt(mWindowHeight);
        parcel.writeFloat(mMinScale);
        parcel.writeFloat(mMaxScale);
        parcel.writeFloat(mScale);
    }

    public interface OnChangeListener {
        void onChange();
    }
}
