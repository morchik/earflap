package ru.vmordo.earflap;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
//import android.preference.PreferenceCategory;
import ru.vmordo.earflap.R;

public class PrefActivity extends PreferenceActivity {
  
  //public static boolean allowDel = false;
  CheckBoxPreference chb_del_q;
  CheckBoxPreference chb_autostart;
  
  //CheckBoxPreference chb3;
  //PreferenceCategory categ2;
  
  SharedPreferences prefs;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.pref);

    chb_autostart = (CheckBoxPreference) findPreference("chb_autostart");
	prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
	
	// нужно для автозогрузки приложения 
	chb_autostart.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	      public boolean onPreferenceClick(Preference preference) {
	    		Editor editor = prefs.edit();
	    		editor.putBoolean("chb_autostart", chb_autostart.isChecked() );
	    		editor.commit();	        
	    		return false;
	      }
	    });

	  
    chb_del_q = (CheckBoxPreference) findPreference("chb_del_q");
    //allowDel = chb_del_q.isChecked();
    
    //chb3 = (CheckBoxPreference) findPreference("chb3");
    //categ2  = (PreferenceCategory) findPreference("categ2");
    //categ2.setEnabled(chb3.isChecked());
    /*
    chb3.setOnPreferenceClickListener(new OnPreferenceClickListener() {
      public boolean onPreferenceClick(Preference preference) {
        categ2.setEnabled(chb3.isChecked());
        return false;
      }
    });
    */
  }
}