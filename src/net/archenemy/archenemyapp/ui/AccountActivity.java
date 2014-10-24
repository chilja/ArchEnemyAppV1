package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentTransaction;

public class AccountActivity extends FacebookActivity {
	
	private FacebookAccount mFacebookAccount;
	private TwitterAccount mTwitterAccount;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.account_activity);
	    
	    //Find the fragments
	    mTwitterAccount = (TwitterAccount) getSupportFragmentManager().findFragmentById(R.id.TwitterAccountFragment);
	    mFacebookAccount = (FacebookAccount) getSupportFragmentManager().findFragmentById(R.id.FacebookAccountFragment);
	    showAccounts();
	}
	
	private void showAccounts(){
	    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    transaction.show(mTwitterAccount);
	    transaction.show(mFacebookAccount);
		transaction.commit();
		setTitle(R.string.action_accounts);
	}
}
