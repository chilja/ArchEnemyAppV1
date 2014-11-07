package net.archenemy.archenemyapp.presenter;

import net.archenemy.archenemyapp.R;
import android.os.Bundle;

public class FacebookShareActivity extends FacebookActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.facebook_share_activity);
	}

	@Override
	public void onFacebookLogin() {
		
	} 
}
	
