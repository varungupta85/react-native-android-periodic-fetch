package com.acintyo.reactnativeandroidperiodicfetch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

class ReactNativeAndroidPeriodicFetchModule extends ReactContextBaseJavaModule {
    private final Context context;
    private final PeriodicFetchBroadcastReceiver periodicFetchBroadcastReceiver;
    public ReactNativeAndroidPeriodicFetchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
        this.periodicFetchBroadcastReceiver = new PeriodicFetchBroadcastReceiver();
    }

    private void registerPeriodicFetchEvent() {
        IntentFilter intentFilter = new IntentFilter("RNPeriodicFetch");
        getReactApplicationContext().registerReceiver(periodicFetchBroadcastReceiver, intentFilter);
    }

    private void unregisterPeriodicFetchEvent() {
        getReactApplicationContext().unregisterReceiver(periodicFetchBroadcastReceiver);
    }

    private void sendEvent(String eventName, Object params) {
        ReactContext reactContext = getReactApplicationContext();

        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Override
    public String getName() {
        return "ReactNativeAndroidPeriodicFetch";
    }

    private void schedulePeriodicFetch(Integer delayInMsec) {
        PendingIntent pendingIntent = this.getPendingIntentForPeriodicFetch(delayInMsec);
        this.getAlarmManager().set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInMsec.intValue(), pendingIntent);
    }

    private PendingIntent getPendingIntentForPeriodicFetch(Integer delayInMsec) {
        Intent intent = new Intent("RNPeriodicFetch");
        intent.putExtra("delay", delayInMsec.intValue());
        // Use a constant ID such that we only have one such intent
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @ReactMethod
    public void start(Integer delayInMsec) {
        Log.d("RNAndroidPeriodicFetch", "Starting the periodic fetch");
        this.registerPeriodicFetchEvent();
        this.schedulePeriodicFetch(delayInMsec);
    }

    @ReactMethod
    public void stop(Integer delayInMsec) {
        this.unregisterPeriodicFetchEvent();
        PendingIntent pendingIntent = this.getPendingIntentForPeriodicFetch(delayInMsec);
        this.getAlarmManager().cancel(pendingIntent);
    }

    private class PeriodicFetchBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RNAndroidPeriodicFetch", "Received broadcast to perform periodic fetch");
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                WritableMap params = Arguments.createMap();
                ReactNativeAndroidPeriodicFetchModule.this.sendEvent("periodicFetch", params);
            }
            int delayInMsec = intent.getIntExtra("delay", 0);
            Log.d("RNAndroidPeriodicFetch", String.format("Scheduling next periodic fetch after %s msecs", delayInMsec));
            ReactNativeAndroidPeriodicFetchModule.this.schedulePeriodicFetch(new Integer(delayInMsec));
        }
    }
}
