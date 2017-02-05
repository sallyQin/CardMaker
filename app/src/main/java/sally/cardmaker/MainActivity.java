package sally.cardmaker;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;

import sally.cardmaker.poker.PokerActivity;

@SuppressWarnings("SpellCheckingInspection")
public class MainActivity extends AppCompatActivity implements View.OnClickListener { //主界面

    /** static variables */
    private static final String TAG = "MainActivity";
    private static final String KEY_IMAGE_URI = "image_uri";
    private static final String KEY_CAPTURED_URI = "captured_uri";
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAPTURE = 2;
    private static final int REQUEST_PITU = 3;
    private static final int REQUEST_POKER = 4;

    /** USER DATA -- saved member variables */
    private Uri mImageUri;
    private Uri mCapturedUri;

    /** auto-initialized member variables */
    private Intent mPituIntent = new Intent(null, Uri.parse("ttpic://TTPTBEAUTIFY?back=1")).setClassName("com.tencent.ttpic", "com.tencent.ttpic.module.MainActivity");
    private View mControlGroup;
    private View mTipView;
    private View mCaptureButton;
    private View mPituButton;
    private SimpleDraweeView mThumbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** bundles */
        Intent intent = getIntent();
        Log.d(TAG, "[onCreate] intent = " + intent);
        if (savedInstanceState != null) {
            mImageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI);
            mCapturedUri = savedInstanceState.getParcelable(KEY_CAPTURED_URI);
        } else {
            try {
                mImageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            } catch (Exception e) {
                // ignore
            }
        }

        /** views */
        setContentView(R.layout.activity_main);
        View galleyGroup = findViewById(R.id.gallery);
        mThumbView = (SimpleDraweeView) galleyGroup.findViewById(R.id.thumb);
        mTipView = galleyGroup.findViewById(R.id.tip);
        mControlGroup = findViewById(R.id.control);
        mCaptureButton = mControlGroup.findViewById(R.id.capture);
        mPituButton = mControlGroup.findViewById(R.id.pitu);
        if (!Utils.isEmpty(mImageUri)) {
            Log.d(TAG, "[onCreate] mImageUri = " + mImageUri);
            onPickImage();
        }

        /** listener */
        galleyGroup.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_IMAGE_URI, mImageUri);
        outState.putParcelable(KEY_CAPTURED_URI, mCapturedUri);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.gallery:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  /** System Gallery */
                if (Utils.isIntentSafe(intent, R.string.error_no_gallery)) {
                    startActivityForResult(intent, REQUEST_GALLERY);
                }
                break;
            case R.id.capture:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Utils.isIntentSafe(intent, R.string.error_no_capture)) {
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    mCapturedUri = Uri.fromFile(new File(dir, "CM_CAPTURED_" + System.currentTimeMillis() +  ".jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedUri);
                    startActivityForResult(intent, REQUEST_CAPTURE);
                }
                break;
            case R.id.pitu:
//                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("ttpic://TTPTBEAUTIFY?back=1"));
                if (Utils.isIntentSafe(mPituIntent, R.string.error_no_pitu)) {
                    String path = Utils.getMediaPath(mImageUri);
                    if (!TextUtils.isEmpty(path)) {
                        ArrayList<Uri> uris = new ArrayList<>();
                        uris.add(Uri.parse(path));
                        mPituIntent.putExtra(Intent.EXTRA_STREAM, uris);
                    } else {
                        mPituIntent.removeExtra(Intent.EXTRA_STREAM);
                    }
                    startActivityForResult(mPituIntent, REQUEST_PITU);
                }
                break;
            case R.id.poker:
                intent = new Intent(this, PokerActivity.class);
                intent.setData(mImageUri);
                startActivityForResult(intent, REQUEST_POKER);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utils.isIntentSafe(mPituIntent)) {
            mPituButton.setVisibility(View.VISIBLE);
        } else {
            mPituButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "[onActivityResult] requestCode = " + requestCode + ", resultCode = " + resultCode + ", data = " + data);

        Uri uri = null;
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (data != null) {
                    uri = data.getData();
                }
                break;
            case REQUEST_CAPTURE:
                if (RESULT_OK == resultCode) {
                    uri = mCapturedUri;
                    Utils.galleryAddPic(uri);
                }
                break;
            case REQUEST_PITU:
                if (data != null) {
                    try {
                        uri = data.getParcelableExtra(Intent.EXTRA_STREAM);
                        if (!Utils.isEmpty(uri)) {
                            uri = Uri.fromFile(new File(uri.getPath()));
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
                break;
            case REQUEST_POKER:
                break;
        }

        if (!Utils.isEmpty(uri)) {
            mImageUri = uri;
            onPickImage();
        }
    }

    private void onPickImage() {
        mThumbView.setImageURI(mImageUri);
        mTipView.setVisibility(View.GONE);
        mControlGroup.setVisibility(View.VISIBLE);

        if (Utils.hasCamera()) {
            mCaptureButton.setVisibility(View.VISIBLE);
        }

        if (Utils.isIntentSafe(mPituIntent)) {
            mPituButton.setVisibility(View.VISIBLE);
        } else {
            mPituButton.setVisibility(View.GONE);
        }
    }
}
