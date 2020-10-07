package my.app.cyclingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlertReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context cx, Intent intent) {
            Log.i("received", "ok");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(cx);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean("alarm", true);
            editor.apply();

            Intent newIntent = new Intent(cx, MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
            cx.startActivity(newIntent);
        }
}
