package bdnath.lictproject.info.ghur.FireBasePojoClass;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Mobile App Develop on 5/15/2018.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
