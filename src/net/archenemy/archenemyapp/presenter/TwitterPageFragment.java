package net.archenemy.archenemyapp.presenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import twitter4j.User;

import net.archenemy.archenemyapp.model.DataAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;

public class TwitterPageFragment extends PageFragment 
	implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TAG = "TwitterPageFragment";
	
	@Override
	public String getTAG() {
		return TAG;
	}

	@Override
	protected List<FeedElement> getListElements() {		
		List<FeedElement> list = new ArrayList<FeedElement>();		
		SocialMediaUser member = DataAdapter.getSocialMediaUser(mUserId);
		User user = member.getTwitterUser();
		if ( user != null ) {	
			TwitterUser twitterUser = new TwitterUser(user.getName(), user.getScreenName(), user.getOriginalProfileImageURL());	
			list.add(twitterUser);
		}
		list.addAll(member.getTweets());		
		return list;
	}
}
