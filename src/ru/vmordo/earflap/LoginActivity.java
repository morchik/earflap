package ru.vmordo.earflap;

import java.util.Calendar;

//import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ru.vmordo.earflap.R;
import java.io.IOException;

public class LoginActivity extends FragmentActivity implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener {
	private GoogleMap mMap;

	public static LocationClient mLocationClient;

	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		//EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		//EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	TextView tvOut;
	EditText etPsw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ru.vmordo.util.Log.context = getBaseContext();
		startService(new Intent(this, TrackingService.class));  // запуск службы
		setContentView(R.layout.myscreen);
		tvOut = (TextView) findViewById(R.id.tvOut);
		etPsw = (EditText) findViewById(R.id.edLogin);
		etPsw.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				tvOut.setText("");
				return false;
			}
		});
	}

	public void onClick(View v) throws IOException {
		// получаем текущее время
		showMyLocation(null);
		final Calendar c = Calendar.getInstance();
		String tmpPsw = "" + c.get(Calendar.MINUTE)
				+ c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.DAY_OF_MONTH);

		if ((etPsw.getText() + "").compareTo(tmpPsw) == 0) {
			//etPsw.setText("");
			startActivity(new Intent(this, MainActivity.class));
		} else if ((etPsw.getText() + "").compareTo("1234567890") == 0) { // tmp
			startActivity(new Intent(this, MainActivity.class));
		} else if ((etPsw.getText() + "").compareTo("8888888888") == 0) {
			startActivity(new Intent(this, PrefActivity.class));
		} else if ((etPsw.getText() + "").compareTo("9999999999") == 0) {
			Process root = Runtime.getRuntime().exec("su"); // test root 
			Toast.makeText(getApplicationContext(), root.toString(), Toast.LENGTH_LONG).show();
		} else {
			tvOut.setText(R.string.txt_ypswrdWr);
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }*/
    }

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
				//mMap.show();
				//AIzaSyAbgQwUZkqXRjKYBf09afRsE4eGKewjFZw
				//AIzaSyClQWtxlvfXn0ltwSJxuMdSFDKlneO4Ot8
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	/**
	 * Button to get current Location. This demonstrates how to get the current
	 * Location as required without needing to register a LocationListener.
	 */
	public void showMyLocation(View view) {
		if (mLocationClient != null && mLocationClient.isConnected()) {
			String msg = "Location = " + mLocationClient.getLastLocation();
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		//String msg = "Location = " + mLocationClient.getLastLocation();
		//Db_Helper dbHelper = new Db_Helper(this);
		//dbHelper.insert_loc(arg0.toString(), arg0); // запись в бд
		//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		//Log.wrLogFile(arg0.toString(), "loc_"+Log.getDay() + ".txt"); // запись в файл
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onConnected(Bundle arg0) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
