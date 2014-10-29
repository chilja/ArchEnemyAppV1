package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import twitter4j.User;

import net.archenemy.archenemyapp.data.BandMember;
import net.archenemy.archenemyapp.data.DataAdapter;

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
	protected List<ListElement> getListElements() {		
		List<ListElement> list = new ArrayList<ListElement>();		
		BandMember member = DataAdapter.getBandMember(mUserId);
		User user = member.getTwitterUser();
		if ( user != null ) {	
			TwitterUser twitterUser = new TwitterUser(user.getName(), user.getScreenName(), user.getOriginalProfileImageURL());	
			list.add(twitterUser);
		}
		list.addAll(member.getTweets());		
		return list;
	}
}
