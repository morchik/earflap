package ru.vmordo.earflap;

import java.util.Calendar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import ru.vmordo.earflap.R;
import java.io.IOException;

public class LoginActivity extends FragmentActivity implements SeekBar.OnSeekBarChangeListener{
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
	SeekBar sb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ru.vmordo.util.Log.context = getBaseContext();
		startService(new Intent(this, TrackingService.class));  // запуск службы
		setContentView(R.layout.myscreen);
		tvOut = (TextView) findViewById(R.id.tvOut);
		etPsw = (EditText) findViewById(R.id.edLogin);
		sb = (SeekBar)findViewById(R.id.seekBar1);
		sb.setOnSeekBarChangeListener(this);
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
    }

    @Override
    public void onPause() {
        super.onPause();
     }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int  p = sb.getProgress();
		tvOut.setText(progress+" sb= "+p);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
