package net.archenemy.archenemyapp.ui;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.FacebookActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class SettingsActivity extends FacebookActivity {
	//Intent constants
	public final static String PAGE = "SettingActivity.Page";
	public static final int PREFERENCES = 0;
	public static final int ACCOUNTS = 1;
	
	private int mPage;
	private PreferenceFragment mPreferenceFragment;
	private FacebookAccount mFacebookAccount;
	private TwitterAccount mTwitterAccount;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings_activity);
	    Intent args = getIntent();
	    mPage = args.getIntExtra(SettingsActivity.PAGE, PREFERENCES);
	    setTitle(R.string.title_activity_settings);
	    
	    //Find the fragments
	    mPreferenceFragment = (PreferenceFragment) getFragmentManager().findFragmentById(R.id.SettingsFragment);
	    mTwitterAccount = (TwitterAccount) getSupportFragmentManager().findFragmentById(R.id.TwitterAccountFragment);
	    mFacebookAccount = (FacebookAccount) getSupportFragmentManager().findFragmentById(R.id.FacebookAccountFragment);
	    showPage(mPage);
	}
	
	private void showPage(int page) {
		switch (page){
	    case PREFERENCES:
	    	showPreferences();
	    	break;
	    case ACCOUNTS: 
	    	showAccounts();
	    }
	}
	
	private void showAccounts(){
		hidePreferences();
	    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    transaction.show(mTwitterAccount);
	    transaction.show(mFacebookAccount);
		transaction.commit();
		setTitle(R.string.action_accounts);
	}
	
	private void hideAccounts(){
	    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    transaction.hide(mTwitterAccount);
	    transaction.hide(mFacebookAccount);
		transaction.commit();
	}
	
	private void hidePreferences(){
		android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
	    transaction.hide(mPreferenceFragment);
	    transaction.commit();		    
	}
	
	private void showPreferences(){
		hideAccounts();
		android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
	    transaction.show(mPreferenceFragment);
	    transaction.commit();
	    setTitle(R.string.action_settings);
	}
}
