package net.archenemy.archenemyapp.data;

import net.archenemy.archenemyapp.R;

public final class Constants {
	
	//colors
	public static final int WHITE = android.R.color.white;
	public static final int GREY = R.color.lightgrey;
	public static final int RED = R.color.red;
	
	//Keys for saving instance state
	public static final String TWITTER_USER_NAME = "net.archenemy.archenemyapp.TWITTER_USER_NAME";
	public static final String FACEBOOK_USER_NAME = "net.archenemy.archenemyapp.FACEBOOK_USER_NAME";
	public static final String TWITTER_IS_REFRESHED = "net.archenemy.archenemyapp.mTwitterIsRefreshed";
	public static final String FACEBOOK_IS_REFRESHED = "net.archenemy.archenemyapp.mFacebookIsRefreshed";
	public static final String POPUP_VISIBLE = "net.archenemy.archenemyapp.popupVisible";
	public static final String FRAGMENT = "net.archenemy.archenemyapp.fragment";
	public static final String DRAWER_OPEN = "net.archenemy.archenemyapp.drawerOpen";

	//Preference keys
	public static final String PREF_KEY_MICHAEL = "pref_key_michael_amott";
	public static final String PREF_KEY_ALYSSA = "pref_key_alyssa_white_gluz";
	

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION =
        "net.archenemy.archenemyapp.BROADCAST";
    
    public static final String FEED_ACTION =
            "net.archenemy.archenemyapp.BROADCAST";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS =
        "net.archenemy.archenemyapp.STATUS";

}
