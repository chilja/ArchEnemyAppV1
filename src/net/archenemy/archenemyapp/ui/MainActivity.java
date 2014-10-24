package net.archenemy.archenemyapp.ui;

import com.facebook.Session;
import com.facebook.SessionState;
import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.BandMember;
import net.archenemy.archenemyapp.data.DataAdapter;
import net.archenemy.archenemyapp.data.FacebookAdapter;
import net.archenemy.archenemyapp.data.TwitterAdapter;
import net.archenemy.archenemyapp.data.Utility;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity 
	extends 
		FacebookActivity 
	implements 
		TwitterAdapter.FeedCallback,
		FacebookAdapter.FeedCallback {
	
	private static final String TAG = "MainActivity";
	
	//Keys for saving instance state
	public static final String TWITTER_IS_REFRESHED = "mTwitterIsRefreshed";
	public static final String FACEBOOK_IS_REFRESHED = "mFacebookIsRefreshed";
	public static final String POPUP_VISIBLE = "net.archenemy.archenemyapp.popupVisible";
	public static final String FRAGMENT_KEY = "net.archenemy.archenemyapp.fragment";
	
	//menu positions = main fragment index
	private static final int FACEBOOK = 0;
	private static final int TWITTER = 1;
	private static final int TOUR = 2;
	private static int mSelectedMenuItem = TOUR;// initial selection
	
	//fragments
	private FacebookFragment mFacebookFragment;
	private TwitterFragment mTwitterFragment;
	private TourFragment mTourFragment;
	private TwitterLoginFragment mTwitterLoginFragment;
    private FacebookLoginFragment mFacebookLoginFragment;

	private MenuDrawer mMenuDrawer;
	private boolean mIsResumed = false;
	private ActionBar mActionBar;
	private FragmentManager mFragmentManager;
	private DataAdapter mDataAdapter;
	
	//Twitter
	private TwitterAdapter mTwitterAdapter;
	private static boolean mTwitterIsRefreshed = false;
	
	//Facebook
	private FacebookAdapter mFacebookAdapter;		
	private static boolean mFacebookIsRefreshed = false;
	
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
			mFacebookAdapter.makeFeedRequest(this, member);	
		}		
	}
	
	//Facebook Callback
	@Override
	public void onLogout() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		mDataAdapter = new DataAdapter(this);
		                     
		mFragmentManager = getSupportFragmentManager();
		mMenuDrawer = new MenuDrawer();
		    
	    mTwitterAdapter = new TwitterAdapter(this);
	    mFacebookAdapter = new FacebookAdapter(this);
	    	    
	    //try to retrieve fragments
	    if (savedInstanceState != null) {
	    	mTwitterFragment = (TwitterFragment) getSupportFragmentManager().findFragmentByTag(TwitterFragment.TAG);
	    	mFacebookFragment = (FacebookFragment) getSupportFragmentManager().findFragmentByTag(FacebookFragment.TAG);
	    	mTwitterLoginFragment = (TwitterLoginFragment) getSupportFragmentManager().findFragmentByTag(TwitterLoginFragment.TAG);
		    mFacebookLoginFragment = (FacebookLoginFragment) getSupportFragmentManager().findFragmentByTag(FacebookLoginFragment.TAG);
	    	mTourFragment = (TourFragment) getSupportFragmentManager().findFragmentByTag(TourFragment.TAG);
	    	
	    	mFacebookIsRefreshed = savedInstanceState.getBoolean(FACEBOOK_IS_REFRESHED, false);
	    } 
	    
	    if (mTwitterFragment == null)
	    	mTwitterFragment = new TwitterFragment();
	    if (mFacebookFragment == null)
	    	mFacebookFragment = new FacebookFragment();
	    if (mTwitterLoginFragment == null)
		    mTwitterLoginFragment = new TwitterLoginFragment();
		if (mFacebookLoginFragment == null)
		    mFacebookLoginFragment = new FacebookLoginFragment();
		if (mTourFragment == null)
	    	mTourFragment = new TourFragment();
	    if (savedInstanceState == null) {	
	    	FragmentTransaction transaction = mFragmentManager.beginTransaction();
			transaction.add(R.id.fragmentContainer, mTourFragment, TourFragment.TAG );
			transaction.commit();
	    }
		
	    restoreActionBar();
	    
	  //redirection from twitter authorization web page?
	    Uri uri = getIntent().getData();
	    if (uri != null && uri.toString().startsWith(mTwitterAdapter.TWITTER_CALLBACK_URL)) {
	    	mTwitterAdapter.authorize(uri, this); 
	    	showFragment(TWITTER, false);
	    } else {
	    	if (savedInstanceState != null) {	
		    	mTwitterIsRefreshed = savedInstanceState.getBoolean(TWITTER_IS_REFRESHED, false);
		    	restoreFragment(savedInstanceState);					
		    } else {
		    	showFragment(mSelectedMenuItem, false);
		    }
	    }
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
		bundle.putInt(FRAGMENT_KEY, getVisibleFragmentIndex() );
		bundle.putBoolean(TWITTER_IS_REFRESHED, mTwitterIsRefreshed);
		bundle.putBoolean(FACEBOOK_IS_REFRESHED, mFacebookIsRefreshed);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mMenuDrawer.onConfigurationChanged(newConfig);
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
		if (getVisibleFragment()==mFacebookLoginFragment ||getVisibleFragment()==mTwitterLoginFragment) {
		    inflater.inflate(R.menu.settings, menu);
		    return true;
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
	    //Action bar events
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
			intent.putExtra(SettingsActivity.PAGE, SettingsActivity.PREFERENCES);
			startActivity(intent);
			return true;
	    }
	    if (item.getItemId() == R.id.actionAccounts) {
			Intent intent = new Intent(this, SettingsActivity.class);
			intent.putExtra(SettingsActivity.PAGE, SettingsActivity.ACCOUNTS);
			startActivity(intent);
			return true;
	    }
	    return super.onOptionsItemSelected(item);
	}   
    
    private void refreshFacebook() {
    	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  				
			if (!mFacebookIsRefreshed && mFacebookAdapter.isLoggedIn()) { 
				Toast.makeText(this,getResources().getString(R.string.facebook_loading), Toast.LENGTH_LONG).show();
				for (BandMember member: mDataAdapter.getEnabledBandMembers())
					mFacebookAdapter.makeFeedRequest(this, member);	
				//set flag
				mFacebookIsRefreshed = true;	
			}
        }
        mFacebookFragment.refresh();
    }
    
    private void refreshTwitter() {
    	//check network connection
        if (Utility.isConnectedToNetwork(this, true)) { 	  	
			if (!mTwitterIsRefreshed && mTwitterAdapter.isLoggedIn()) {
				Toast.makeText(this,getResources().getString(R.string.twitter_loading), Toast.LENGTH_LONG).show();
				mTwitterAdapter.getFeed(this);
				//set flag
				mTwitterIsRefreshed = true;
			}
        }
        mTwitterFragment.refresh();
    }
   
 
    @Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    // Sync the toggle state after onRestoreInstanceState has occurred.
	    mMenuDrawer.syncState();
	}

	//Twitter Callback
    public void onFeedRequestCompleted() { 
		mTwitterFragment.refresh();
	    Log.i(TAG, "Received twitter feed");	
	}
	
	//Facebook Callback
	@Override
	public void onFeedRequestCompleted(BandMember member) {
		mFacebookFragment.refresh();
		Log.i(TAG, "Received facebook feed");
	}

	//Twitter Callback
	@Override
	public void onAuthorizationCompleted(Boolean isAuthorized) {
		showFragment(TWITTER, false);
		if(isAuthorized) mTwitterAdapter.getFeed(this);
	}

	@Override
	public void onLogoutCompleted(boolean isLoggedIn) {
		// TODO Auto-generated method stub		
	}
	
	protected void restoreFragment(Bundle savedInstanceState){
		int fragmentIndex = savedInstanceState.getInt(FRAGMENT_KEY);
		showFragment(fragmentIndex, false);
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
			case TOUR:
				showFragment(mTourFragment, addToBackStack); break;
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
		transaction.hide(mTourFragment);
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
		if (mTourFragment.isVisible()) return mTourFragment;   
		if (mTwitterFragment.isVisible()) return mTwitterFragment;
		if (mTwitterLoginFragment.isVisible()) return mTwitterLoginFragment;
		if (mFacebookFragment.isVisible()) return mFacebookFragment;
		if (mFacebookLoginFragment.isVisible()) return mFacebookLoginFragment;
		return null;
	}
	
	protected int getVisibleFragmentIndex() {
		if (mTourFragment.isVisible()) return TOUR;   
		if (mTwitterFragment.isVisible() || mTwitterLoginFragment.isVisible()) return TWITTER;
		if (mFacebookFragment.isVisible()|| mFacebookLoginFragment.isVisible()) return FACEBOOK;
		return -1;
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
		    mDrawerListView.setOnItemClickListener(new DrawerItemClickListener(mActivity));   
		    
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
			private ActionBarActivity mActivity;
			
			DrawerItemClickListener(ActionBarActivity activity) {
				this.mActivity = activity;
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
				
				if (mSelectedMenuItem == position) {
					textView.setTextColor(getResources().getColor(android.R.color.white));
					indicator.setVisibility(View.VISIBLE);
				} else {
					textView.setTextColor(getResources().getColor(R.color.lightgrey));
					indicator.setVisibility(View.INVISIBLE);
				}
				
				return view;				
			}		   
		}
	}
}

