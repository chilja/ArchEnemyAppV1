package net.archenemy.archenemyapp.ui;

import java.io.Serializable;
import java.util.List;
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
		return DataAdapter.getBandMember(mUserId).getTweets();
	}
}
