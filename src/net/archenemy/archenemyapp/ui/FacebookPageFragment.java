package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.archenemy.archenemyapp.data.SocialMediaUser;
import net.archenemy.archenemyapp.data.DataAdapter;

public class FacebookPageFragment extends PageFragment 
	implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TAG = "FacebookPageFragment";
		
	@Override
	public String getTAG() {
		return TAG;
	}

	@Override
	protected List<FeedElement> getListElements() {
		SocialMediaUser member = DataAdapter.getSocialMediaUser(mUserId);
		FacebookUser user = new FacebookUser(member.getFacebookUserName(), member.getFacebookUserId());
		List<FeedElement> list = new ArrayList<FeedElement>();
		list.add(user);
		list.addAll(member.getPosts());		
		return list;
	}	
}
