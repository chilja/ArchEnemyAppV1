package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class AccountActivity extends FacebookActivity {
	
	private FacebookAccountFragment mFacebookAccount;
	private TwitterAccountFragment mTwitterAccount;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.account_activity);
	    
	    //Find the fragments
	    mTwitterAccount = (TwitterAccountFragment) getSupportFragmentManager().findFragmentById(R.id.TwitterAccountFragment);
	    mFacebookAccount = (FacebookAccountFragment) getSupportFragmentManager().findFragmentById(R.id.FacebookAccountFragment);
	    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    transaction.show(mTwitterAccount);
	    transaction.show(mFacebookAccount);
		transaction.commit();
		setTitle(R.string.action_accounts);
	}
}
