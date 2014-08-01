package ru.vmordo.util;

import ru.vmordo.earflap.Db_Helper;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Loc implements LocationListener {
	public String provider;
	protected static Loc loc = null;
	protected static Db_Helper dbHelper = null;
	protected static long counter = 0;
	
	public Loc(String pr){
		provider = pr;
	}
	
	public static String DateTimetoStr(long dt){ // for google track
		/*		Date d = new Date(dt);
		@SuppressWarnings("deprecation")
		String result =  // 2013-09-04T06:01:47.718Z
			(d.getYear()+1900)+"-"+Log.fstr(d.getMonth())+"-"+Log.fstr(d.getDay())+"T"+
			Log.fstr(d.getHours())+":"+Log.fstr(d.getMinutes())+":"+Log.fstr(d.getHours())+".001"
			+"Z";
		*/
		return (String) android.text.format.DateFormat.format("yyyy-MM-ddTHH:mm:ss.001Z", new java.util.Date(dt));
	}
	
	public static String toCSV(Location l){ // for google track
		counter = counter + 1;
		String result = "\"1\",\""+counter+"\","
			+"\""+l.getLatitude()+"\","
			+"\""+l.getLongitude()+"\","
			+"\""+l.getAltitude()+"\","
			+"\""+l.getBearing()+"\","
			+"\""+Math.round(l.getAccuracy())+"\","
			+"\""+l.getSpeed()+"\","
			+"\""+DateTimetoStr(l.getTime())+"\"" // 2013-09-04T06:01:47.718Z
			+"\"\",\"\",\"\"";
		return result;
	}
	
	public static void start(Context mContext) {
		if (loc == null)
			loc = new Loc(LocationManager.GPS_PROVIDER);
		if (dbHelper == null)
			dbHelper = new Db_Helper(mContext);

		LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 1, loc);
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		if (loc != null){
			// ������ � ��� ���� �� �����
			Log.wrLogFile(arg0.toString()+"\n"
				, Log.getDir()+"/"+Log.getDateDir()+"loc_"+provider+"_"+Log.getDay() + ".txt"); // ������ � ����
			// ���� ������� csv ��� ����-�������
			Log.wrLogFile(toCSV(arg0)+"\n"
				, Log.getDir()+"/"+Log.getDateDir()+"loc_"+provider+"_"+Log.getDay() + ".csv"); // ������ � ����
		}
		if (dbHelper != null)
			dbHelper.insert_loc(arg0.toString(), arg0); // ������ � ��
	}


    @Override
    public void onProviderDisabled(String provider) {
		Log.d("LOC", "onProviderDisabled "+provider); 
    }

    @Override
    public void onProviderEnabled(String provider) {
		Log.d("LOC", "onProviderEnabled "+provider); 
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("LOC", "onStatusChanged  "+provider+" status "+status); 
    }
}
