package net.archenemy.archenemyapp.ui;

import java.io.Serializable;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.R.id;
import net.archenemy.archenemyapp.R.layout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.MenuInflater;

import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.FacebookDialog;

public class FacebookShareActivity extends FacebookActivity {
	
	public static final String SHARE_ELEMENT = "FacebookShareElement";
	//fragment
	private FacebookShareFragment mShareFragment;
	
	private int mSelectedShowIndex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.facebook_activity);    
	    //Find the fragments
	    mShareFragment = new FacebookShareFragment();
	    
	    //Get the content to be shared
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	mSelectedShowIndex = (extras.getInt(TourFragment.SELECTED_SHOW_INDEX, -1));
        }
         	   
	    showFragment();
	} 
	
	private void showFragment(){	
		Bundle args = new Bundle();
		args.putSerializable(SHARE_ELEMENT,(Serializable) new FacebookShareElement.Show(0, this, mSelectedShowIndex));
        mShareFragment.setArguments(args);
		 // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mShareFragment).commit();
	}
}
	
