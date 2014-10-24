package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentTransaction;

public class SettingsActivity extends FacebookActivity {

	private PreferenceFragment mPreferenceFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings_activity);
	    setTitle(R.string.title_activity_settings);
	    
	    //Find the fragments
	    mPreferenceFragment = (PreferenceFragment) getFragmentManager().findFragmentById(R.id.SettingsFragment);
		android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
	    transaction.show(mPreferenceFragment);
	    transaction.commit();
	    setTitle(R.string.action_settings);
	}
}
