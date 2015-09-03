package org.akvo.caddisfly.sensor.turbidity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TurbidityStartReceiver extends BroadcastReceiver {

    public TurbidityStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (TurbidityConfig.ACTION_ALARM_RECEIVER.equals(intent.getAction())) {
                String date = intent.getStringExtra("startDateTime");
                Intent intentActivity = new Intent(context, TurbidityActivity.class);
                intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentActivity.putExtra("startDateTime", date);
                context.startActivity(intentActivity);
            }
        }
    }
}