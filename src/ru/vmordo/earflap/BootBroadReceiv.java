package ru.vmordo.earflap;

import java.util.List;

import ru.vmordo.util.Net;
import ru.vmordo.util.upLoader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ru.vmordo.util.Log;

public class BootBroadReceiv extends BroadcastReceiver {
	public static BootBroadReceiv odin = null;
	final String LOG_TAG = "myLogsBootBroadReceiv";

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			boolean val = prefs.getBoolean("chb_autostart", true);

			if (val) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				context.startService(new Intent(context, TrackingService.class));
			}
		}
		if (intent.getAction() == Intent.ACTION_TIME_TICK) {
			//ru.vmordo.util.Vibr.vibrate(context,new long[] { 0, 30, 100, 40 });

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			boolean bWifi = prefs.getBoolean("chb_wifi_up", false);

			// выгрузка
			if ((!bWifi) || (Net.getInetType(context) == "WIFI")) {
				List<String> list = MainActivity.dbHelper.get_list(20);
				(new upLoader()).listTaskUpLoad(list, MainActivity.bCnt);
			}
		}
		// Toast.makeText(context,
		// "BootBroadReceiv " + intent.toString() + " " + intent.toURI(),
		// Toast.LENGTH_LONG).show();
		Log.d(LOG_TAG, "BootBroadReceiv " + intent.toString());
	}

	/*
	 * how to use it
	 * 
	 * if (BootBroadReceiv.odin == null) { BootBroadReceiv.odin = new
	 * BootBroadReceiv(); bCnt.registerReceiver(BootBroadReceiv.odin, new
	 * IntentFilter( "android.intent.action.TIME_TICK")); Log.d(LOG_TAG,
	 * "registerReceiver "); }
	 */

}