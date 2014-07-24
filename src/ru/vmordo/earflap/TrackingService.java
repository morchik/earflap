package ru.vmordo.earflap;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import ru.vmordo.util.Log;

public class TrackingService extends Service {

	final String LOG_TAG = "myLogsServ";

	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(LOG_TAG, "onStartCommand TrackingService");
		int NOTIFICATION_ID = 1;
		Context acontext = getApplicationContext();
		SharedPreferences prefs = 
		        PreferenceManager.getDefaultSharedPreferences(acontext); 
		boolean val = prefs.getBoolean("chb_autostart", false);
		if (val)
			Log.v(LOG_TAG, "chb_autostart on");
		else
			Log.v(LOG_TAG, "chb_autostart off");
		
		if (MainActivity.allowRec) 
		{
			
			int icon = ru.vmordo.earflap.R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			Context context = getBaseContext();

			//Notification notification = new Notification.Builder(context)
        		//.setContentTitle("started foreground " )
        		//.setContentText("EarFlap")
        		//.setSmallIcon(R.drawable.new_mail)
        		//.setLargeIcon(aBitmap)
        		//.build();
			Notification notification =  new android.app.Notification(icon,
					"Sound Servece started foreground", when);
			// Создание намерения с указанием класса вашей Activity, которую хотите
			// вызвать при нажатии на оповещение.
			Intent notificationIntent = new Intent(this, LoginActivity.class);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			String txt = "Earflap started foreground ";
			if (MainActivity.allowRec)
				txt = txt + "ON";
			else
				txt = txt + "OFF";
			notification.setLatestEventInfo(context, "Earflap", txt, contentIntent);
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
		Timer myTimer = new Timer(); // Создаем таймер
		// final Handler uiHandler = new Handler();
		Log.d(LOG_TAG, " someTask ");
		// for(int i=0; i<2; i++)
		// MainActivity.startRecord();

		myTimer.schedule(new TimerTask() { // Определяем задачу
					@Override
					public void run() {
						Log.d(LOG_TAG, " startRecord ");
						MainActivity.startRecord();
					}
				}, 1000L, 60L * 1000); // интервал - 60000 миллисекунд, 1000
										// миллисекунд до первого запуска.

	}
}