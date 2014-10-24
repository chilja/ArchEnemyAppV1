package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.List;

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
	protected List<FeedListElement> getListElements() {
		return DataAdapter.getBandMember(mUserId).getFacebookFeedElements();
	}	
}
