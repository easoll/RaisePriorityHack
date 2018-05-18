package com.easoll.raisepriorityhack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NormalService extends Service {
    public NormalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
