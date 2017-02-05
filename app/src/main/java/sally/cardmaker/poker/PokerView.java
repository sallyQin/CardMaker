package sally.cardmaker.poker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.view.MotionEventCompat;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import sally.cardmaker.BitmapRect;
import sally.cardmaker.Utils;

class PokerView extends View implements BitmapRect.OnChangeListener { //自定义View(poker图的部分)

    @SuppressWarnings("unused")
    private static final String TAG = "PokerView";
    private static final float RATIO = 0.64f;
    private static final float SCALE_X_DEFAULT = 0.85f;
    private static final float SCALE_X_SUIT = 1;
    private static final float SCALE_X_10 = 0.75f;

    Uri mImageUri;
    Bitmap mBitmap;
    BitmapRect mBitmapRect;

    private Rect mWindowRect;
    private Paint mPaint;
    private ScaleGestureDetector mScaleDetector;

    public PokerView(Context context) {
        super(context);
        init();
    }

    public PokerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PokerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mWindowRect = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  // 反锯齿
        mPaint.setTypeface(Typeface.SERIF);         // 衬线字体
    }

    @Override
    public void onChange() {
        PokerActivity activity = (PokerActivity) getContext();
        activity.mChanged = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width >= height * RATIO) {
            width = (int) (height * RATIO + 0.5f);
        } else {
            height = (int) (width / RATIO + 0.5f);
        }
        setMeasuredDimension(width, height); /** modified poker size */
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            int width = right - left;
            int height = bottom - top;
            ((GradientDrawable) getBackground()).setCornerRadius(width / 16f);

            int padding = (int) (width / 8f + 0.5f);
            mPaint.setTextSize(padding);
            mWindowRect.left = padding;
            mWindowRect.top = padding;
            mWindowRect.right = width - padding;
            mWindowRect.bottom = height - padding;

            mBitmap = Utils.decode(mImageUri, this);
            if (null == mBitmapRect) {
                mBitmapRect = new BitmapRect(mBitmap, mWindowRect);
            }
            mBitmapRect.setOnChangeListener(this);
            mScaleDetector = new ScaleGestureDetector(getContext(), mBitmapRect);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        float x, y;
        int count = MotionEventCompat.getPointerCount(event);
        if (1 == count) {
            x = MotionEventCompat.getX(event, 0);
            y = MotionEventCompat.getY(event, 0);
        } else {
            x = (MotionEventCompat.getX(event, 0) + MotionEventCompat.getX(event, 1)) / 2;
            y = (MotionEventCompat.getY(event, 0) + MotionEventCompat.getY(event, 1)) / 2;
        }

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_POINTER_UP:
                if (MotionEventCompat.getActionIndex(event) == 0) {
                    x = MotionEventCompat.getX(event, 1);
                    y = MotionEventCompat.getY(event, 1);
                } else {
                    x = MotionEventCompat.getX(event, 0);
                    y = MotionEventCompat.getY(event, 0);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (1 == count) {
                    mBitmapRect.onMove(x, y);
                }
                invalidate();
                break;
        }

        mBitmapRect.updatePosition(x, y);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, mBitmapRect.mRect, mWindowRect, mPaint);
        }

        PokerActivity activity = (PokerActivity) getContext();
        if (activity.mSuit instanceof Spannable) {
            mPaint.setColor(Color.RED);
        } else {
            mPaint.setColor(Color.BLACK);
        }

        switch (activity.mRank.toString()) {
            case "JOKER":
                mPaint.setTextScaleX(SCALE_X_DEFAULT);
                drawText(canvas, "J", 0);
                drawText(canvas, "O", 1);
                drawText(canvas, "K", 2);
                drawText(canvas, "E", 3);
                drawText(canvas, "R", 4);
                break;
            case "10":
                mPaint.setTextScaleX(SCALE_X_10);
                drawText(canvas, activity.mRank, 0);
                mPaint.setTextScaleX(SCALE_X_SUIT);
                drawText(canvas, activity.mSuit, 1);
                break;
            default:
                mPaint.setTextScaleX(SCALE_X_DEFAULT);
                drawText(canvas, activity.mRank, 0);
                mPaint.setTextScaleX(SCALE_X_SUIT);
                drawText(canvas, activity.mSuit, 1);
        }
    }

    private void drawText(Canvas canvas, CharSequence text, int index) {
        int length = text.length();
        float width = mPaint.measureText(text, 0, length);
        float height = mPaint.getTextSize();
        float x = (height - width) / 2;
        float y = height * 1.05f  * (1.2f + index);

        canvas.drawText(text, 0, length, x, y, mPaint);
        canvas.save();
        canvas.rotate(180, getWidth() / 2, getHeight() / 2);
        canvas.drawText(text, 0, length, x, y, mPaint);
        canvas.restore();
    }
}
