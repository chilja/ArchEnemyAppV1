package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.Constants;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment 
	implements OnSharedPreferenceChangeListener{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        setStartMenuSummary();
        
    }
	
	private void setStartMenuSummary() {
		SharedPreferences sharedPreferences = 
		        PreferenceManager.getDefaultSharedPreferences(getActivity());
        String start = sharedPreferences.getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
        String summary = (Constants.FACEBOOK.equals(start))?
        		getResources().getString(R.string.title_facebook) : getResources().getString(R.string.title_twitter);
        Preference startMenuPreference = getPreferenceManager().findPreference(Constants.PREF_KEY_START);
        startMenuPreference.setSummary(summary);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	        String key) {
        if (key.equals(Constants.PREF_KEY_START)) {
            // Set summary to be the user-description for the selected value
            setStartMenuSummary();
        }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    PreferenceManager.getDefaultSharedPreferences(getActivity())
	    		.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    PreferenceManager.getDefaultSharedPreferences(getActivity())
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
}
