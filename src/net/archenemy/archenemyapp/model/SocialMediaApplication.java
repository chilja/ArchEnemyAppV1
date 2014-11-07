package net.archenemy.archenemyapp.model;

import io.fabric.sdk.android.Fabric;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import android.app.Application;

public class SocialMediaApplication extends Application {
	
	public void onCreate() {
        super.onCreate();
 
	 TwitterAuthConfig authConfig = 
			 new TwitterAuthConfig("DqhZFiXkeerd1gaqeD1reFmVX",
	                                   "pcpFXsv1YFqQ1BncuQw5qzBMvl9Ow3TUPKG2oFHnuR5RG9e2Ab");
	 Fabric.with(this, new Twitter(authConfig));
	 Fabric.with(this, new TwitterCore(authConfig));
 
    }

}
