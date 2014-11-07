package net.archenemy.archenemyapp.presenter;

import java.util.ArrayList;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.BitmapUtility;
import net.archenemy.archenemyapp.model.Constants;
import net.archenemy.archenemyapp.model.DataAdapter;
import net.archenemy.archenemyapp.model.FacebookAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import net.archenemy.archenemyapp.model.TwitterAdapter;
import net.archenemy.archenemyapp.model.Utility;
import twitter4j.User;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity 
	extends 
		FacebookActivity 
	implements 
		TwitterAdapter.FeedCallback,		 
		TwitterAdapter.UserCallback, 
		TwitterLoginFragment.OnLTwitteroginListener,
		FacebookAdapter.FeedCallback,
		FacebookLoginFragment.OnFacebookLoginListener{
	
	private static final String TAG = "MainActivity";
	
	//menu positions = main fragment index
	private static final int FACEBOOK = 0;
	private static final int TWITTER = 1;
	private static int mSelectedMenuItem;// initial selection
	
	private ActionBar mActionBar;
	private FragmentManager mFragmentManager;
	private DataAdapter mDataAdapter;
	
	//fragments
	private FacebookFragment mFacebookFragment;
	private FacebookLoginFragment mFacebookLoginFragment;	
	private TwitterFragment mTwitterFragment;	
	private TwitterLoginFragment mTwitterLoginFragment;
	
	//Twitter
	private TwitterAdapter mTwitterAdapter;
	//flag to prevent repeated automatic refresh
	private static boolean mTwitterIsRefreshed = false;
	private Integer mTwitterCallbackCount;
	private Integer mTwitterCallbackTotal;
	private LoadingProgressDialog mTwitterProgressDialog;
	private ImageView mTwitterButton;
	
	
	//Facebook
	private FacebookAdapter mFacebookAdapter;	
	//flag to prevent repeated automatic refresh
	private static boolean mFacebookIsRefreshed = false;
	private Integer mFacebookCallbackCount;
	private Integer mFacebookCallbackTotal;
	private LoadingProgressDialog mFacebookProgressDialog;
	private ImageView mFacebookButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tab_activity);
		
		mTwitterButton = (ImageView) findViewById(R.id.twitterButton);
		mTwitterButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				showMenuItem(TWITTER, false);				
			}
		});
		
		mFacebookButton = (ImageView) findViewById(R.id.facebookButton);
		mFacebookButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				showMenuItem(FACEBOOK, false);				
			}
		});
				
		mDataAdapter = new DataAdapter(this);
		                     
		mFragmentManager = getSupportFragmentManager();
		    
	    mFacebookAdapter = new FacebookAdapter(this);
	    mTwitterAdapter = new TwitterAdapter(this);
	    
	    mFacebookProgressDialog = new LoadingProgressDialog(getResources().getString(R.string.facebook_loading));
	    mTwitterProgressDialog = new LoadingProgressDialog(getResources().getString(R.string.twitter_loading));
	    	    
	    //try to retrieve fragments
	    if (savedInstanceState != null) {
	    	mTwitterFragment = (TwitterFragment) getSupportFragmentManager().findFragmentByTag(TwitterFragment.TAG);
	    	mFacebookFragment = (FacebookFragment) getSupportFragmentManager().findFragmentByTag(FacebookFragment.TAG);
	    	mTwitterLoginFragment = (TwitterLoginFragment) getSupportFragmentManager().findFragmentByTag(TwitterLoginFragment.TAG);
		    mFacebookLoginFragment = (FacebookLoginFragment) getSupportFragmentManager().findFragmentByTag(FacebookLoginFragment.TAG);
	    	
		    mTwitterIsRefreshed = savedInstanceState.getBoolean(Constants.TWITTER_IS_REFRESHED, false);
	    	mFacebookIsRefreshed = savedInstanceState.getBoolean(Constants.FACEBOOK_IS_REFRESHED, false);
	    } 
	    
	    //create fragments
	    if (mTwitterFragment == null)
	    	mTwitterFragment = new TwitterFragment();
	    if (mFacebookFragment == null)
	    	mFacebookFragment = new FacebookFragment();
	    if (mTwitterLoginFragment == null)
		    mTwitterLoginFragment = new TwitterLoginFragment();
		if (mFacebookLoginFragment == null)
		    mFacebookLoginFragment = new FacebookLoginFragment();
		
		//add first fragment that will be replaced in showFragment()
	    if (savedInstanceState == null) {	
	    	FragmentTransaction transaction = mFragmentManager.beginTransaction();
			transaction.add(R.id.fragmentContainer, mFacebookFragment, FacebookFragment.TAG );
			transaction.commit();
	    }
	    		
	    restoreActionBar();  
	    
    	if (savedInstanceState != null) {	
	    	restoreFragment(savedInstanceState);
	    	return;
	    } 
    	
    	SharedPreferences sharedPreferences = 
		        PreferenceManager.getDefaultSharedPreferences(this);
        String start = sharedPreferences.getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
        mSelectedMenuItem = (Constants.FACEBOOK.equals(start))? FACEBOOK : TWITTER;
    	showMenuItem(mSelectedMenuItem, false);	    
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    //save current state
		bundle.putInt(Constants.FRAGMENT, getVisibleFragmentIndex() );
		bundle.putBoolean(Constants.TWITTER_IS_REFRESHED, mTwitterIsRefreshed);
		bundle.putBoolean(Constants.FACEBOOK_IS_REFRESHED, mFacebookIsRefreshed);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	} 
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    // Pass the activity result to the fragment, which will
	    // then pass the result to the login button.
	    if (mTwitterLoginFragment != null) {
	    	mTwitterLoginFragment.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//cancel background threads
		BitmapUtility.onDestroy();
	}

	public boolean onPrepareOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater();
		menu.clear();
		if (getVisibleFragment()==mTwitterFragment) {
		    inflater.inflate(R.menu.twitter, menu);
		    return true;
		}
		if (getVisibleFragment()==mFacebookFragment) {
		    inflater.inflate(R.menu.facebook, menu);
		    return true;
		}
		//default
		inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    //handle action bar events
	    if (item.getItemId() == R.id.actionRefreshTwitter) {
	    	//set flag to false to allow manual refresh
	    	mTwitterIsRefreshed = false;
	    	refreshTwitterFeed();
	    	return true;
	    }
	    if (item.getItemId() == R.id.actionRefreshFacebook) {
	    	//set flag to false to allow manual refresh
	    	mFacebookIsRefreshed = false;
	    	refreshFacebookFeed();
	    	return true;
	    }
	    if (item.getItemId() == R.id.actionSettings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
	    }
	    if (item.getItemId() == R.id.actionAccounts) {
			Intent intent = new Intent(this, AccountActivity.class);
			startActivity(intent);
			return true;
	    }
	    //if event has not been handled, then pass it on
	    return super.onOptionsItemSelected(item);
	}    
    
  	@Override
  	public void onFacebookLogin() {
  		//redirection from facebook 		
		Log.i(TAG, "Facebook session opened");
		if (mFacebookAdapter.isLoggedIn())
			if (getVisibleFragmentIndex() == FACEBOOK) {
				mFacebookIsRefreshed = false;
				showMenuItem(FACEBOOK,false);
				refreshFacebookFeed();
		} 
  	}
	
	@Override
	public void onTwitterLogin() {
  		//redirection from twitter
		Log.i(TAG, "Twitter session opened");
		if (mTwitterAdapter.isLoggedIn())
  		if (getVisibleFragmentIndex() == TWITTER) {
  			mTwitterIsRefreshed = false;
			showMenuItem(TWITTER, false);   
			refreshTwitterFeed();
		}
	}

	//Facebook Callback
	@Override
	public void onFeedRequestCompleted(ArrayList<FeedElement> elements, String id) {
		if (elements != null && id != null && elements.size() >0) {
			for (SocialMediaUser user: mDataAdapter.getEnabledSocialMediaUsers()) {
				if (id.equals(user.getFacebookUserId())) {
					user.setPosts(elements);
					break;
				}					
			}
		}
		
		synchronized (mFacebookCallbackCount) {
			mFacebookCallbackCount -= 1;
			int progress = (mFacebookCallbackTotal == 0) ? 100 :
				(1 - (mFacebookCallbackCount / mFacebookCallbackTotal)) * 100;
			mFacebookProgressDialog.setProgress(progress);
		}
		//all requests completed? refresh fragment
		if (mFacebookCallbackCount < 1) {
			mFacebookProgressDialog.dismiss();
			mFacebookFragment.refresh();
		}
		Log.i(TAG, "Received facebook feed");
	}
	
	//Twitter Callback
    public void onFeedRequestCompleted(ArrayList<FeedElement> elements, Long id) { 
    	if (elements != null && id != null && elements.size() >0) {
			for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers()) {
				if (id.equals(member.getTwitterUserId())) {
					member.setTweets(elements);
					break;
				}					
			}
		}
    	synchronized (mTwitterCallbackCount) {
			mTwitterCallbackCount -= 1;
			int progress = (mTwitterCallbackTotal == 0) ? 100 : 
				(1 - (mTwitterCallbackCount / mTwitterCallbackTotal)) * 100;
			mTwitterProgressDialog.setProgress(progress);
    	}
		//all requests completed? refresh fragment
		if (mTwitterCallbackCount < 1) {
			mTwitterProgressDialog.dismiss();
			mTwitterFragment.refresh();
		}
			
	    Log.i(TAG, "Received twitter feed");	
	}
    
    @Override
	public void onUserRequestCompleted(User user) {
		Long userId = user.getId();
		for (SocialMediaUser member : mDataAdapter.getEnabledSocialMediaUsers()) {
			if (member.getTwitterUserId().equals(userId)) {
				member.setTwitterUser(user);
				break;
			}
		}
		refreshTwitterFeed();
	}

	private void refreshFacebookFeed() {
    	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  				
			if (!mFacebookIsRefreshed && mFacebookAdapter.isLoggedIn()) { 
				
				if (mFacebookProgressDialog != null) 
					mFacebookProgressDialog.dismiss();
				
				mFacebookProgressDialog.show();
				
				mFacebookCallbackCount = 0;				
				for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers()) {
					mFacebookAdapter.makeFeedRequest(this, member.getFacebookUserId());	
					mFacebookCallbackCount += 1;
				}
				mFacebookCallbackTotal = mFacebookCallbackCount;
				//set flag
				mFacebookIsRefreshed = true;
			}
        }
		//refresh screen
        mFacebookFragment.refresh();
    }
    
    private void refreshTwitterFeed() {
    	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  	
			if (!mTwitterIsRefreshed && mTwitterAdapter.isLoggedIn()) {
				if (mTwitterProgressDialog != null) mTwitterProgressDialog.dismiss();
				
				mTwitterProgressDialog.show();
				mTwitterCallbackCount = 0;
				for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers()) {
					mTwitterAdapter.makeFeedRequest(member.getTwitterUserId(), this);
					mTwitterCallbackCount += 1;
				}
				
		        for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers()) {
		        	if (member.getTwitterUser() == null) {
		        		mTwitterAdapter.makeUserRequest(member.getTwitterUserId(), this);
		        	}
		        }
		        mTwitterCallbackTotal = mTwitterCallbackCount;
				//set flag
				mTwitterIsRefreshed = true;
			}
        }
        //refresh UI
        mTwitterFragment.refresh();
    }
	
    private void setButtonViewSelected(ImageView button) {		
		button.setBackgroundResource(R.color.red_semi_transparent);
		button.clearColorFilter();
	}
	
	private void setButtonViewUnselected(ImageView button) {		
		button.setColorFilter(Constants.BLACK_TRANSP);
		button.setBackgroundResource(R.color.red_transparent);
	}
	
	private void restoreActionBar() {
		mActionBar = getActionBar();
	    mActionBar.setDisplayShowTitleEnabled(true);
	}

	void showMenuItem(int menuIndex, boolean addToBackStack) {
		mSelectedMenuItem = menuIndex;
		switch (menuIndex) {
			case FACEBOOK:
				if (mFacebookAdapter.hasValidToken()) {
					showFragment(mFacebookFragment, addToBackStack); 
					refreshFacebookFeed();
				} else {
					showFragment(mFacebookLoginFragment, addToBackStack);
					mPendingLogin = true;
				}
				setButtonViewSelected(mFacebookButton);
				setButtonViewUnselected(mTwitterButton);
				break;
			case TWITTER:
				if (mTwitterAdapter.isLoggedIn()) {
					showFragment(mTwitterFragment, addToBackStack); 
					refreshTwitterFeed();
				} else {
					showFragment(mTwitterLoginFragment, addToBackStack);
				}
				setButtonViewUnselected(mFacebookButton);
				setButtonViewSelected(mTwitterButton);
				break;
		}
	}

	private void showFragment (BaseFragment fragment, boolean addToBackStack) {
		hideFragments();
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragment, fragment.getTAG());
		transaction.show(fragment);
		
		clearBackStack();

      //back navigation
    	if (addToBackStack) {
    		transaction.addToBackStack(null);
    	} 
    	
    	transaction.commit();

    	invalidateOptionsMenu();
	}

	private void restoreFragment(Bundle savedInstanceState){
		int fragmentIndex = savedInstanceState.getInt(Constants.FRAGMENT);
		showMenuItem(fragmentIndex, false);
	}

	private void hideFragments() {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.hide(mTwitterFragment);
		transaction.hide(mFacebookFragment);
		transaction.hide(mTwitterLoginFragment);
		transaction.hide(mFacebookLoginFragment);
	    transaction.commit();
	}
	
	private void clearBackStack() {
		// Get the number of entries in the back stack
		int backStackSize = mFragmentManager.getBackStackEntryCount();
		// Clear the back stack
		if (backStackSize > 0) {
			for (int i = 0; i < backStackSize; i++) {
			    mFragmentManager.popBackStack();
			}
		}
	}
	
	BaseFragment getVisibleFragment() {  
		if (mTwitterFragment.isVisible()) return mTwitterFragment;
		if (mTwitterLoginFragment.isVisible()) return mTwitterLoginFragment;
		if (mFacebookFragment.isVisible()) return mFacebookFragment;
		if (mFacebookLoginFragment.isVisible()) return mFacebookLoginFragment;
		return null;
	}
	
	private int getVisibleFragmentIndex() {  
		if (mTwitterFragment.isVisible() || mTwitterLoginFragment.isVisible()) return TWITTER;
		if (mFacebookFragment.isVisible()|| mFacebookLoginFragment.isVisible()) return FACEBOOK;
		return -1;
	}
	
	private class LoadingProgressDialog extends ProgressDialog {
		LoadingProgressDialog (String message){
			super(MainActivity.this);
			setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			setProgressDrawable(getResources().getDrawable(R.drawable.loader));
			setIndeterminateDrawable(getResources().getDrawable(R.drawable.loader));
			setMessage(message);
		}		
	}	
}

