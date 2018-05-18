package com.easoll.raisepriorityhack;

import android.app.Notification;
import android.app.Service;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author easoll on 2018/5/18.
 */

public class RaisePriorityHack {
    private static final String TAG = "RaisePriorityHack";

    private static boolean mHasHookH = false;

    private static int mScheduleCrashMsgWhat = 134;

    private static final int NOTIFICATION_ID = 1;


    public static void raisePriority(Service service){
        hookH();

        RemoteViews remoteViews = new RemoteViews(service.getPackageName(), 1);

        Notification notification = new NotificationCompat.Builder(service, "test channel").
                setSmallIcon(R.drawable.ic_launcher_foreground).
                setContentTitle("content title").
                setSubText("sub text").
                setCustomContentView(remoteViews).
                build();

        service.startForeground(NOTIFICATION_ID, notification);
    }



    private static void hookH(){
        if(mHasHookH){
            return;
        }

        mHasHookH = true;

        try {
            try {
                Class hClass = Class.forName("android.app.ActivityThread$H");
                Field scheduleCrashField = hClass.getDeclaredField("SCHEDULE_CRASH");
                mScheduleCrashMsgWhat = (int)scheduleCrashField.get(null);
                Log.i(TAG, "get mScheduleCrashMsgWhat success");
            }catch (Exception e){
                Log.i(TAG, "get mScheduleCrashMsgWhat failed");

                e.printStackTrace();
            }

            Handler.Callback callback = new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Log.i(TAG, msg.toString());

                    if(msg.what == mScheduleCrashMsgWhat){
                        return true;
                    }

                    return false;
                }
            };

            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field mH = activityThreadClass.getDeclaredField("mH");
            mH.setAccessible(true);
            Method currentActivityThread = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object activityThreadInstance = currentActivityThread.invoke(null);
            Handler hInstance = (Handler) mH.get(activityThreadInstance);
            Class handlerClass = Handler.class;
            Field mCallbackField = handlerClass.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            mCallbackField.set(hInstance, callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
