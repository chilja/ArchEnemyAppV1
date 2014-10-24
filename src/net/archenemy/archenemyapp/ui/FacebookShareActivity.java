package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import android.os.Bundle;

public class FacebookShareActivity extends FacebookActivity {
	
	public static final String SHARE_ELEMENT = "FacebookShareElement";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.facebook_activity);       
//	    showFragment();
	} 
	
//	private void showFragment(){
//		FacebookShareFragment fragment = new FacebookShareFragment();
//		 // Add the fragment to the 'fragment_container' FrameLayout
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, fragment).commit();
//	}
}
	
