package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.archenemy.archenemyapp.data.BandMember;
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
	protected List<ListElement> getListElements() {
		BandMember member = DataAdapter.getBandMember(mUserId);
		member.getTwitterUserName();
		FacebookUser user = new FacebookUser(member.getFacebookUserName(), member.getFacebookUserId());
		List<ListElement> list = new ArrayList<ListElement>();
		list.add(user);
		list.addAll(member.getPosts());		
		return list;
	}	
}
