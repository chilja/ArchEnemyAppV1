package net.archenemy.archenemyapp.ui;

import java.util.ArrayList;

import twitter4j.User;

import com.facebook.Session;
import com.facebook.SessionState;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.SocialMediaUser;
import net.archenemy.archenemyapp.data.Constants;
import net.archenemy.archenemyapp.data.DataAdapter;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import net.archenemy.archenemyapp.data.TwitterAdapter;
import net.archenemy.archenemyapp.data.TwitterAdapter.TokenCallback;
import net.archenemy.archenemyapp.data.TwitterAdapter.UserCallback;
import net.archenemy.archenemyapp.data.Utility;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

public class MainActivity 
	extends 
		FacebookActivity 
	implements 
		TwitterAdapter.FeedCallback,
		FacebookAdapter.FeedCallback, UserCallback, TokenCallback {
	
	private static final String TAG = "MainActivity";
	
	//menu positions = main fragment index
	private static final int FACEBOOK = 0;
	private static final int TWITTER = 1;
	private static int mSelectedMenuItem;// initial selection
	
	//fragments
	private FacebookFragment mFacebookFragment;
	private FacebookLoginFragment mFacebookLoginFragment;	
	private TwitterFragment mTwitterFragment;	
	private TwitterLoginFragment mTwitterLoginFragment;
//	private TourFragment mTourFragment;

//	private MenuDrawer mMenuDrawer;
	private ActionBar mActionBar;
	private FragmentManager mFragmentManager;
	private DataAdapter mDataAdapter;
	
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
	
	private boolean mIsResumed = false;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tab_activity);
		
		mTwitterButton = (ImageView) findViewById(R.id.twitterButton);
		mTwitterButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				showFragment(TWITTER, false);				
			}
		});
		
		mFacebookButton = (ImageView) findViewById(R.id.facebookButton);
		mFacebookButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				showFragment(FACEBOOK, false);				
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
	    
	    //show fragment
	    
	    //redirection from twitter authorization web page?
	    Uri uri = getIntent().getData();
	    if (uri != null && uri.toString().startsWith(TwitterAdapter.TWITTER_CALLBACK_URL)) {
	    	//request access token from Twitter using provided url
	    	mTwitterAdapter.makeTokenRequest(uri, this); 
	    } 
	    
	    //case restore activity?
    	if (savedInstanceState != null) {	
	    	restoreFragment(savedInstanceState);
	    	return;
	    } 
    	
    	//default
    	SharedPreferences sharedPreferences = 
		        PreferenceManager.getDefaultSharedPreferences(this);
        String start = sharedPreferences.getString(Constants.PREF_KEY_START, Constants.FACEBOOK);
        mSelectedMenuItem = (Constants.FACEBOOK.equals(start))? FACEBOOK : TWITTER;
    	showFragment(mSelectedMenuItem, false);
	    
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
		mIsResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		mIsResumed = false;
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
		return false;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    //handle action bar events
	    if (item.getItemId() == R.id.actionRefreshTwitter) {
	    	mTwitterIsRefreshed = false;
	    	refreshTwitter();
	    	return true;
	    }
	    if (item.getItemId() == R.id.actionRefreshFacebook) {
	    	mFacebookIsRefreshed = false;
	    	refreshFacebook();
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
    
  //Facebook Callback
  	protected void onSessionStateChange(final Session session, SessionState state, Exception exception) {
    	Log.i(TAG, "Facebook session opened");
    	if (mFacebookAdapter.isLoggedIn())
    		if (getVisibleFragmentIndex() == FACEBOOK) {
    			mFacebookIsRefreshed = false;
    			showFragment(FACEBOOK,false);
    			refreshFacebook();
    	} 
  	}
  	
  	//Facebook Callback
  	@Override
  	public void onLogin() {
  		//redirection from facebook
  		if (getVisibleFragmentIndex() == FACEBOOK) {
  			if (mPendingLogin) {
  				mPendingLogin = false;
  				showFragment(FACEBOOK, false);    				
  			}
  		for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers())
  			mFacebookAdapter.makeFeedRequest(this, member.getFacebookUserId());	
  		}		
  	}
	
	//Facebook Callback
	@Override
	public void onFeedRequestCompleted(ArrayList<FeedElement> elements, String id) {
		if (elements != null && id != null && elements.size() >0) {
			for (SocialMediaUser member: mDataAdapter.getEnabledSocialMediaUsers()) {
				if (id.equals(member.getFacebookUserId())) {
					member.setPosts(elements);
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
	@Override
	public void onTokenRequestCompleted() {
		// user is now logged in with Twitter
		// show Twitter fragment and refresh
//		if (getVisibleFragmentIndex()== TWITTER) {
			showFragment(TWITTER, false);
			refreshTwitter();
//		}
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
		refreshTwitter();
	}

	private void refreshFacebook() {
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
    
    private void refreshTwitter() {
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
        //refesh screen
        mTwitterFragment.refresh();
    }
	
	protected void restoreFragment(Bundle savedInstanceState){
		int fragmentIndex = savedInstanceState.getInt(Constants.FRAGMENT);
		showFragment(fragmentIndex, false);
	}
	
	protected void restoreActionBar() {
		mActionBar = getActionBar();
	    mActionBar.setDisplayShowTitleEnabled(true);
	}

	protected void showFragment(int menuIndex, boolean addToBackStack) {
		mSelectedMenuItem = menuIndex;
		switch (menuIndex) {
			case FACEBOOK:
				if (mFacebookAdapter.hasValidToken()) {
					showFragment(mFacebookFragment, addToBackStack); 
					refreshFacebook();
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
					refreshTwitter();
				} else {
					showFragment(mTwitterLoginFragment, addToBackStack);
				}
				setButtonViewUnselected(mFacebookButton);
				setButtonViewSelected(mTwitterButton);
				break;
		}
	}
	
	protected void setButtonViewSelected(ImageView button) {		
		button.setBackgroundResource(R.color.red_semi_transparent);
		button.clearColorFilter();
	}
	
	protected void setButtonViewUnselected(ImageView button) {		
		button.setBackgroundResource(R.color.red_transparent);
		button.setColorFilter(Constants.BLACK_TRANSP);
	}
	
	protected void showFragment (BaseFragment fragment, boolean addToBackStack) {
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

	protected void hideFragments() {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.hide(mTwitterFragment);
		transaction.hide(mFacebookFragment);
		transaction.hide(mTwitterLoginFragment);
		transaction.hide(mFacebookLoginFragment);
	    transaction.commit();
	}
	
	protected void clearBackStack() {
		// Get the number of entries in the back stack
		int backStackSize = mFragmentManager.getBackStackEntryCount();
		// Clear the back stack
		if (backStackSize > 0) {
			for (int i = 0; i < backStackSize; i++) {
			    mFragmentManager.popBackStack();
			}
		}
	}
	
	protected BaseFragment getVisibleFragment() {  
		if (mTwitterFragment.isVisible()) return mTwitterFragment;
		if (mTwitterLoginFragment.isVisible()) return mTwitterLoginFragment;
		if (mFacebookFragment.isVisible()) return mFacebookFragment;
		if (mFacebookLoginFragment.isVisible()) return mFacebookLoginFragment;
		return null;
	}
	
	protected int getVisibleFragmentIndex() {  
		if (mTwitterFragment.isVisible() || mTwitterLoginFragment.isVisible()) return TWITTER;
		if (mFacebookFragment.isVisible()|| mFacebookLoginFragment.isVisible()) return FACEBOOK;
		return -1;
	}
	
	private class LoadingProgressDialog extends ProgressDialog {
		LoadingProgressDialog (String message){
			super(MainActivity.this);
			setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			setProgressDrawable(getResources().getDrawable(R.drawable.progress_indicator));
			setMessage(message);
		}		
	}	
}

