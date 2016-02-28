package sally.cardmaker;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Utils {

    public static void close(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static void colorItem(@NonNull MenuItem item, int color) {   /** suit colors' setting */
        Spannable colored = new SpannableString(item.getTitle());
        colored.setSpan(new ForegroundColorSpan(color), 0, colored.length(), 0);
        item.setTitle(colored);
    }

    public static boolean hasCamera() {
        return App.context().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isEmpty(@Nullable Object object) {
        return null == object || object.toString().isEmpty();
    }

    public static boolean isIntentSafe(@NonNull Intent intent) {
        return isIntentSafe(intent, 0);
    }

    public static boolean isIntentSafe(@NonNull Intent intent, @StringRes int errorRes) {
        Context context = App.context();
        List activities = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean safe = activities.size() > 0;
        if (!safe && errorRes > 0) {
            Toast.makeText(context, errorRes, Toast.LENGTH_LONG).show();
        }
        return safe;
    }

    /** media methods */
    public static String getMediaPath(@NonNull Uri uri) {
        if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            String path = null;
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = App.context().getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
                }
                cursor.close();
            }
            return path;
        } else {
            return uri.getPath();
        }
    }

    public static Bitmap decode(@NonNull Uri uri, @NonNull View view) {
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        String path = getMediaPath(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        for (options.inSampleSize = 1; options.outWidth > viewWidth && options.outHeight > viewHeight; options.inSampleSize <<= 1) {
            options.outWidth >>= 1;
            options.outHeight >>= 1;

            if (options.outWidth < viewWidth || options.outHeight < viewHeight) {
                break;
            }
        }

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decode(@NonNull String file) {
        return decode(file, 1);
    }

    public static Bitmap decode(@NonNull String file, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        try {
            return BitmapFactory.decodeFile(file, options);
        } catch (OutOfMemoryError e) {  /** rare case */
            inSampleSize <<= 1;
            if (inSampleSize <= 0x10) { /** inSampleSize = 2 -> 4 -> 8 -> 16 */
                return decode(file, inSampleSize);
            } else {                    /** inSampleSize = 32, memory low, extremely rare case */
                return null;
            }
        }
    }

    /** view --> bitmap --> png file */
    public static void compress(@NonNull View view) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            try {
                OutputStream stream = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "CM_" + System.currentTimeMillis() + ".png"));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bitmap.recycle();
                close(stream);
            } catch (FileNotFoundException e) {
                // ignore
            }
        } catch (OutOfMemoryError e) {
            // ignore
        }
    }
}
