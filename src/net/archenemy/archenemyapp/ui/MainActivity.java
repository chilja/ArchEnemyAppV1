package net.archenemy.archenemyapp.ui;

import java.util.ArrayList;

import twitter4j.User;

import com.facebook.Session;
import com.facebook.SessionState;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.BandMember;
import net.archenemy.archenemyapp.data.Constants;
import net.archenemy.archenemyapp.data.DataAdapter;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import net.archenemy.archenemyapp.data.TwitterAdapter;
import net.archenemy.archenemyapp.data.TwitterAdapter.TokenCallback;
import net.archenemy.archenemyapp.data.TwitterAdapter.UserCallback;
import net.archenemy.archenemyapp.data.Utility;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
	private static int mSelectedMenuItem = FACEBOOK;// initial selection
	
	//fragments
	private FacebookFragment mFacebookFragment;
	private FacebookLoginFragment mFacebookLoginFragment;	
	private TwitterFragment mTwitterFragment;	
	private TwitterLoginFragment mTwitterLoginFragment;
//	private TourFragment mTourFragment;

	private MenuDrawer mMenuDrawer;
	private ActionBar mActionBar;
	private FragmentManager mFragmentManager;
	private DataAdapter mDataAdapter;
	
	//Twitter
	private TwitterAdapter mTwitterAdapter;
	//flag to prevent repeated automatic refresh
	private static boolean mTwitterIsRefreshed = false;
	private int mTwitterCallbackCount;
	
	//Facebook
	private FacebookAdapter mFacebookAdapter;	
	//flag to prevent repeated automatic refresh
	private static boolean mFacebookIsRefreshed = false;
	private int mFacebookCallbackCount;
	
	private boolean mIsResumed = false;
	
	private LoadingProgressDialog mFacebookProgressDialog;
	private LoadingProgressDialog mTwitterProgressDialog;
	
	private boolean mAccountActivityVisible = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);
		
		mDataAdapter = new DataAdapter(this);
		                     
		mFragmentManager = getSupportFragmentManager();
		mMenuDrawer = new MenuDrawer();
		    
	    mFacebookAdapter = new FacebookAdapter(this);
	    mTwitterAdapter = new TwitterAdapter(this);
	    
	    mFacebookProgressDialog = new LoadingProgressDialog(getResources().getString(R.string.facebook_loading));
	    mTwitterProgressDialog = new LoadingProgressDialog(getResources().getString(R.string.twitter_loading));
	    
	    //redirection from twitter authorization web page?
	    Uri uri = getIntent().getData();
	    if (uri != null && uri.toString().startsWith(TwitterAdapter.TWITTER_CALLBACK_URL)) {
	    	//request access token from Twitter using provided url
	    	mTwitterAdapter.makeTokenRequest(uri, this); 
	    } 
	    	    
	    //try to retrieve fragments
	    if (savedInstanceState != null) {
	    	mTwitterFragment = (TwitterFragment) getSupportFragmentManager().findFragmentByTag(TwitterFragment.TAG);
	    	mFacebookFragment = (FacebookFragment) getSupportFragmentManager().findFragmentByTag(FacebookFragment.TAG);
	    	mTwitterLoginFragment = (TwitterLoginFragment) getSupportFragmentManager().findFragmentByTag(TwitterLoginFragment.TAG);
		    mFacebookLoginFragment = (FacebookLoginFragment) getSupportFragmentManager().findFragmentByTag(FacebookLoginFragment.TAG);
//	    	mTourFragment = (TourFragment) getSupportFragmentManager().findFragmentByTag(TourFragment.TAG);
	    	
	    	mTwitterIsRefreshed = savedInstanceState.getBoolean(Constants.TWITTER_IS_REFRESHED, false);
	    	mFacebookIsRefreshed = savedInstanceState.getBoolean(Constants.FACEBOOK_IS_REFRESHED, false);
	    } 
	    
	    //create fragments which could not have been retrieved
	    if (mTwitterFragment == null)
	    	mTwitterFragment = new TwitterFragment();
	    if (mFacebookFragment == null)
	    	mFacebookFragment = new FacebookFragment();
	    if (mTwitterLoginFragment == null)
		    mTwitterLoginFragment = new TwitterLoginFragment();
		if (mFacebookLoginFragment == null)
		    mFacebookLoginFragment = new FacebookLoginFragment();
//		if (mTourFragment == null)
//	    	mTourFragment = new TourFragment();
		
		//add first fragment that will be replaced in showFragment()
	    if (savedInstanceState == null) {	
	    	FragmentTransaction transaction = mFragmentManager.beginTransaction();
			transaction.add(R.id.fragmentContainer, mFacebookFragment, FacebookFragment.TAG );
			transaction.commit();
	    }
	    		
	    restoreActionBar();
	    
    	if (savedInstanceState != null) {	

	    	restoreFragment(savedInstanceState);					
	    } else {
	    	showFragment(mSelectedMenuItem, false);
	    }

	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    //save current state
		bundle.putInt(Constants.FRAGMENT, getVisibleFragmentIndex() );
		bundle.putBoolean(Constants.TWITTER_IS_REFRESHED, mTwitterIsRefreshed);
		bundle.putBoolean(Constants.FACEBOOK_IS_REFRESHED, mFacebookIsRefreshed);
		bundle.putBoolean(Constants.DRAWER_OPEN, mMenuDrawer.isOpen());
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

	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mMenuDrawer.onConfigurationChanged(newConfig);
    }  
	
	public boolean onPrepareOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater();
		menu.clear();
		if (mMenuDrawer.isOpen()) {
			inflater.inflate(R.menu.settings, menu);
		    return true;
		} else {
			if (getVisibleFragment()==mTwitterFragment) {
			    inflater.inflate(R.menu.twitter, menu);
			    return true;
			}
			if (getVisibleFragment()==mFacebookFragment) {
			    inflater.inflate(R.menu.facebook, menu);
			    return true;
			}
		}
		return false;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Pass the event to ActionBarDrawerToggle, if it returns
	    // true, then it has handled the app icon touch event
	    if (mMenuDrawer.onOptionsItemSelected(item)) {
	      return true;
	    }
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
 
    @Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    // Sync the toggle state after onRestoreInstanceState has occurred.
	    if (mMenuDrawer != null)
	    	mMenuDrawer.syncState();
	}
    
  //Facebook Callback
  	protected void onSessionStateChange(final Session session, SessionState state, Exception exception) {
  	    if (session != null && session.isOpened()) {
  	    	Log.i(TAG, "Facebook session opened");
  	    	if (mFacebookAdapter.isEnabled())
  	    		if (getVisibleFragmentIndex() == FACEBOOK) {
  	    			showFragment(FACEBOOK,false);
  	    	}
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
  		for (BandMember member: mDataAdapter.getEnabledBandMembers())
  			mFacebookAdapter.makeFeedRequest(this, member.getFacebookUserId());	
  		}		
  	}
	
	//Facebook Callback
	@Override
	public void onFeedRequestCompleted(ArrayList<ListElement> elements, String id) {
		if (elements != null && id != null && elements.size() >0) {
			for (BandMember member: mDataAdapter.getEnabledBandMembers()) {
				if (id.equals(member.getFacebookUserId())) {
					member.setPosts(elements);
					break;
				}					
			}
		}
		
		mFacebookCallbackCount -= 1;
		mFacebookProgressDialog.setProgress(50);
		
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
		refreshTwitter();
		// show Twitter fragment
		if (mIsResumed && getVisibleFragmentIndex()== TWITTER)
			showFragment(TWITTER, false);
	}
	
	//Twitter Callback
    public void onFeedRequestCompleted(ArrayList<ListElement> elements, Long id) { 
    	if (elements != null && id != null && elements.size() >0) {
			for (BandMember member: mDataAdapter.getEnabledBandMembers()) {
				if (id.equals(member.getTwitterUserId())) {
					member.setTweets(elements);
					break;
				}					
			}
		}
		mTwitterCallbackCount -= 1;
		mTwitterProgressDialog.setProgress(50);
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
		for (BandMember member : mDataAdapter.getEnabledBandMembers()) {
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
				for (BandMember member: mDataAdapter.getEnabledBandMembers()) {
					mFacebookAdapter.makeFeedRequest(this, member.getFacebookUserId());	
					mFacebookCallbackCount += 1;
				}
			}
        }
		//set flag
		mFacebookIsRefreshed = true;
        mFacebookFragment.refresh();
    }
    
    private void refreshTwitter() {
    	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  	
			if (!mTwitterIsRefreshed && mTwitterAdapter.isLoggedIn()) {
				if (mTwitterProgressDialog != null) mTwitterProgressDialog.dismiss();
				
				mTwitterProgressDialog.show();
				mTwitterCallbackCount = 0;
				for (BandMember member: mDataAdapter.getEnabledBandMembers()) {
					mTwitterAdapter.makeFeedRequest(member.getTwitterUserId(), this);
					mTwitterCallbackCount += 1;
				}
				
		        for (BandMember member: mDataAdapter.getEnabledBandMembers()) {
		        	if (member.getTwitterUser() == null) {
		        		mTwitterAdapter.makeUserRequest(member.getTwitterUserId(), this);
		        	}
		        }

			}
        }
		//set flag
		mTwitterIsRefreshed = true;
        mTwitterFragment.refresh();
    }
	
	protected void restoreFragment(Bundle savedInstanceState){
		int fragmentIndex = savedInstanceState.getInt(Constants.FRAGMENT);
		showFragment(fragmentIndex, false);
	    if (savedInstanceState.getBoolean(Constants.DRAWER_OPEN)) mMenuDrawer.openDrawer();
	}
	
	protected void restoreActionBar() {
		mActionBar = getActionBar();
	    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	    mActionBar.setDisplayShowTitleEnabled(true);
	    mActionBar.setDisplayHomeAsUpEnabled(true);
	}

	protected void showFragment(int menuIndex, boolean addToBackStack) {
		mSelectedMenuItem = menuIndex;
		switch (menuIndex) {
			case FACEBOOK:
				if (mFacebookAdapter.isLoggedIn()) {
					showFragment(mFacebookFragment, addToBackStack); 
					refreshFacebook();
				} else {
					showFragment(mFacebookLoginFragment, addToBackStack);
					mPendingLogin = true;
				}
				break;
			case TWITTER:
				if (mTwitterAdapter.isLoggedIn()) {
					showFragment(mTwitterFragment, addToBackStack); 
					refreshTwitter();
				} else {
					showFragment(mTwitterLoginFragment, addToBackStack);
				}				
				break;
//			case TOUR:
//				showFragment(mTourFragment, addToBackStack); break;
		}
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
    	
    	//set action bar title
    	if (mMenuDrawer.isOpen()) { 
    		mActionBar.setTitle(R.string.app_name);
    	} else {
    		mActionBar.setTitle(fragment.getTitle());
    	}
    	invalidateOptionsMenu();

	}

	protected void hideFragments() {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
//		transaction.hide(mTourFragment);
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
//		if (mTourFragment.isVisible()) return mTourFragment;   
		if (mTwitterFragment.isVisible()) return mTwitterFragment;
		if (mTwitterLoginFragment.isVisible()) return mTwitterLoginFragment;
		if (mFacebookFragment.isVisible()) return mFacebookFragment;
		if (mFacebookLoginFragment.isVisible()) return mFacebookLoginFragment;
		return null;
	}
	
	protected int getVisibleFragmentIndex() {
//		if (mTourFragment.isVisible()) return TOUR;   
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

	class MenuDrawer {
			
		private MainActivity mActivity;
		private DrawerLayout mDrawerLayout;
		private ListView mDrawerListView;
		private ActionBarDrawerToggle mDrawerToggle;
		
		void syncState() {
			mDrawerToggle.syncState();
		}
		
		boolean onOptionsItemSelected(MenuItem item) {
			return mDrawerToggle.onOptionsItemSelected(item);
		}
		
		void onConfigurationChanged(Configuration newConfig){
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
		
		public MenuDrawer(){
			mActivity = MainActivity.this;
			initDrawer();
		}
		
		public ActionBarDrawerToggle getDrawerToggle() {
			return mDrawerToggle;
		}
		
		void openDrawer() {
			mDrawerLayout.openDrawer(mDrawerListView);
		}
		
		boolean isOpen() {
			return mDrawerLayout.isDrawerOpen(Gravity.START);
		}
		
		private void initDrawer() {
	
		    String [] mainMenu = mActivity.getResources().getStringArray(R.array.menu);
		    
		    mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
		   
		    mDrawerListView = (ListView) mActivity.findViewById(R.id.drawerListView);
		    // Set the adapter for the list view
		    mDrawerListView.setAdapter(new DrawerArrayAdapter(mActivity,
		            R.layout.drawer_list_element, mainMenu));
		    // Set the list's click listener
		    mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());   
		    
		    mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout,
		            R.drawable.ic_drawer, R.string.navigation_drawer_open, 
		            R.string.navigation_drawer_close) {
		    	
		        /** Called when a drawer has settled in a completely closed state. */
		        public void onDrawerClosed(View view) {
		            super.onDrawerClosed(view);
		            BaseFragment fragment = getVisibleFragment();
		            if ( fragment != null)
		            	mActivity.getActionBar().setTitle
							 (fragment.getTitle());
		            mActivity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
		        }
		        
		        /** Called when a drawer has settled in a completely open state. */
		        public void onDrawerOpened(View drawerView) {
		            super.onDrawerOpened(drawerView);
		            mActivity.getActionBar().setTitle(R.string.title_activity_main);
		            mActivity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
		        }
		        
		    };
	
		    // Set the drawer toggle as the DrawerListener
		    mDrawerLayout.setDrawerListener(mDrawerToggle);    		    
		}
		
		public class DrawerItemClickListener implements ListView.OnItemClickListener {
			
			DrawerItemClickListener() {
			}
			
	        @Override
	        public void onItemClick(AdapterView parent, View view, int position, long id) {	        	
	            mDrawerListView.setItemChecked(position, true);
	            mSelectedMenuItem = position;
	            showFragment(position, false);
	            mDrawerLayout.closeDrawer(mDrawerListView);
	        }
		}	
		
		class DrawerArrayAdapter extends ArrayAdapter<String> {
			
			String[] mMenuItems;
			
			DrawerArrayAdapter (ActionBarActivity activity, int resource, String[] objects) {
				super(activity, resource, objects);
				mMenuItems = objects;
			}
	
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				View view = convertView;
		        if (view == null) {
		            LayoutInflater inflater =
		                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            view = inflater.inflate(R.layout.drawer_list_element, null);
		        }
		        
				TextView textView = (TextView) view.findViewById(R.id.textView);
				textView.setText(mMenuItems[position]);
				
				View indicator = (View) view.findViewById(R.id.indicator);
				ImageView providerIcon = (ImageView) view.findViewById(R.id.providerIconView);
				switch (position) {
					case TWITTER: 
						mDataAdapter.loadBitmap(R.drawable.twitter, providerIcon);
						break;
					case FACEBOOK: 
						mDataAdapter.loadBitmap(R.drawable.facebook_medium, providerIcon); 
						break;
				}
				
				
				if (mSelectedMenuItem == position) {
					//highlight selected menu item
					textView.setTextColor(getResources().getColor(Constants.WHITE));
					providerIcon.clearColorFilter();
					indicator.setVisibility(View.VISIBLE);
				} else {
					textView.setTextColor(getResources().getColor(Constants.GREY));
					indicator.setVisibility(View.INVISIBLE);
					providerIcon.setColorFilter(Constants.GREY);
				}
				
				return view;				
			}		   
		}
	}
}

