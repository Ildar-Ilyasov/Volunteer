package com.example.volunteer;

import android.app.Application;
import com.yandex.mapkit.MapKitFactory;

public class keyAPI extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey("7ddba7a8-fbff-4016-8b8d-482ace2dc43b");
        MapKitFactory.initialize(this);
    }
}
