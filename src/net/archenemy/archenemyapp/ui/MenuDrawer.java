package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.data.Constants;
import net.archenemy.archenemyapp.data.DataAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuDrawer {

	//menu positions = main fragment index
		private static final int FACEBOOK = 0;
		private static final int TWITTER = 1;
		private static int mSelectedMenuItem;// initial selection		
		private MainActivity mActivity;
		private DrawerLayout mDrawerLayout;
		private ListView mDrawerListView;
		private ActionBarDrawerToggle mDrawerToggle;
		private DataAdapter mDataAdapter;
		

		void onPostCreate(Bundle savedInstanceState) {			    
		   syncState();
		}
		   
		void syncState() {
			mDrawerToggle.syncState();
		}
		
		boolean onOptionsItemSelected(MenuItem item) {
			return mDrawerToggle.onOptionsItemSelected(item);
		}
		
		void onConfigurationChanged(Configuration newConfig){
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
		
		public MenuDrawer(MainActivity activity){
			mActivity = activity;
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
		            BaseFragment fragment = mActivity.getVisibleFragment();
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
	            mActivity.showFragment(position, false);
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
		                    (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            view = inflater.inflate(R.layout.drawer_list_element, null);
		        }
		        
				TextView textView = (TextView) view.findViewById(R.id.textView);
				textView.setText(mMenuItems[position]);
				
				View indicator = (View) view.findViewById(R.id.indicator);
				ImageView icon = (ImageView) view.findViewById(R.id.providerIconView);
				switch (position) {
					case TWITTER: 
						mDataAdapter.loadBitmap(R.drawable.twitter, icon, 60, 60);
						break;
					case FACEBOOK: 
						mDataAdapter.loadBitmap(R.drawable.facebook_medium, icon, 60, 60); 
						break;
				}
				
				
				if (mSelectedMenuItem == position) {
					//highlight selected menu item
					textView.setTextColor(mActivity.getResources().getColor(Constants.WHITE));
					icon.clearColorFilter();
					indicator.setVisibility(View.VISIBLE);
				} else {
					textView.setTextColor(mActivity.getResources().getColor(Constants.LIGHTGREY));
					indicator.setVisibility(View.INVISIBLE);
					icon.setColorFilter(Constants.LIGHTGREY);
				}
				
				return view;				
			}		   
		}
	}

