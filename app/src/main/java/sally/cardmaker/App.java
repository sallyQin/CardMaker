package sally.cardmaker;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        Fresco.initialize(this);
    }

    public static Context context() {
        return context;
    }
}
