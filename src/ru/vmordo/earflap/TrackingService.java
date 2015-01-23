package ru.vmordo.earflap;

import java.util.Timer;
import java.util.TimerTask;

import kz.alfa.util.Cnt;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import ru.vmordo.util.Log;
import ru.vmordo.util.Loc;
import ru.vmordo.util.TakePhoto;

public class TrackingService extends Service {

	final String LOG_TAG = "myLogsServ";

	public void onCreate() {
		Cnt.set(getApplicationContext());
		super.onCreate();
		ru.vmordo.util.Log.context = getBaseContext();
		Log.d(LOG_TAG, "onCreate");
		startService(new Intent(this, TrackingService.class));
	}

	@SuppressWarnings("deprecation")
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(LOG_TAG, "onStartCommand TrackingService");
		int NOTIFICATION_ID = 1;
		Context acontext = getApplicationContext();
		Loc.start(acontext);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(acontext);
		boolean val = prefs.getBoolean("chb_autostart", false);
		if (val)
			Log.v(LOG_TAG, "chb_autostart on");
		else
			Log.v(LOG_TAG, "chb_autostart off");
		// / каждую минуту
		/*
		 * if (BootBroadReceiv.odin == null) { BootBroadReceiv.odin = new
		 * BootBroadReceiv(); registerReceiver(BootBroadReceiv.odin, new
		 * IntentFilter( "android.intent.action.TIME_TICK")); Log.d(LOG_TAG,
		 * "registerReceiver "); }
		 */
		if (MainActivity.allowRec) {

			int icon = 0; // not visible
							// //ru.vmordo.earflap.R.drawable.ic_plusone_tall_off_client;//.ic_launcher;
			long when = System.currentTimeMillis();
			Context context = getBaseContext();

			// Notification notification = new Notification.Builder(context)
			// .setContentTitle("started foreground " )
			// .setContentText("EarFlap")
			// .setSmallIcon(R.drawable.new_mail)
			// .setLargeIcon(aBitmap)
			// .build();
			Notification notification = new android.app.Notification(icon, "",
					when);
			// —оздание намерени€ с указанием класса вашей Activity, которую
			// хотите
			// вызвать при нажатии на оповещение.
			Intent notificationIntent = new Intent(this, LoginActivity.class);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
			String txt = "";
			if (MainActivity.allowRec)
				txt = txt + ""; // on
			else
				txt = txt + ""; // off
			notification.setLatestEventInfo(context, "", txt, contentIntent);
			startForeground(NOTIFICATION_ID, notification);
			someTask();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
	}

	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "onBind");
		return null;
	}

	void someTask() {
		Timer myTimer = new Timer(); // —оздаем таймер
		// final Handler uiHandler = new Handler();
		Log.d(LOG_TAG, " someTask ");
		// for(int i=0; i<2; i++)
		// MainActivity.startRecord();

		myTimer.schedule(new TimerTask() { // ќпредел€ем задачу
					@Override
					public void run() {
						Log.d(LOG_TAG, " startRecord ");
						MainActivity.startRecord();
					}
				}, 1000L, 60L * 1000); // интервал - 60000 миллисекунд, 1000
										// миллисекунд до первого запуска.

		myTimer.schedule(new TimerTask() { // ќпредел€ем задачу
					@Override
					public void run() {
						Log.d(LOG_TAG, " start to take foto ");
						Looper.prepare();
						try {
							TakePhoto.switchFlash();
						} catch (Exception e) {
							e.printStackTrace();
							Log.e(LOG_TAG, e.getMessage());
						}
						Looper.loop();
					}
				}, 2000L, 10L * 1000); // интервал - 60000 миллисекунд, 1000
										// миллисекунд до первого запуска.

	}
}