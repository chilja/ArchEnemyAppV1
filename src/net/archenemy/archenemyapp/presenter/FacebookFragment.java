package net.archenemy.archenemyapp.presenter;

import java.util.ArrayList;

import net.archenemy.archenemyapp.R;
import net.archenemy.archenemyapp.model.DataAdapter;
import net.archenemy.archenemyapp.model.SocialMediaUser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FacebookFragment extends BaseFragment {
	
	public static final int TITLE = R.string.title_facebook;	
	public static final String TAG = "FacebookFragment";
    
    //view pager
	protected ViewPager mViewPager;
	
	protected FacebookPagerAdapter mPagerAdapter;

	//one page for each band member
	protected static ArrayList<SocialMediaUser> mSocialMediaUsers;
	
	private View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    mView = inflater.inflate(R.layout.pager_fragment, container, false);

	    mSocialMediaUsers = DataAdapter.getEnabledSocialMediaUsers(getActivity());
	 	
        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        refresh();

	    return mView;
	}
	
	public void refresh(){
		if (getFragmentManager() != null && mViewPager != null) {				
			mPagerAdapter = new FacebookPagerAdapter(getFragmentManager());
			if (mPagerAdapter != null) {
				mViewPager.setOffscreenPageLimit(6);
				mViewPager.setAdapter(mPagerAdapter);
				mPagerAdapter.notifyDataSetChanged();
			}
		}
	}
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}

	class FacebookPagerAdapter extends FragmentPagerAdapter {
		
		public FacebookPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
		
		// getCount() is used as an offset to make sure that instantiateItem() returns the correct item
		// as both TwitterFragment and FacebookFragment use a PagerAdapter
		// Pager Adapter uses the position as a tag to retrieve the fragment

	    @Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
	    	Object o = super.instantiateItem(container, position+getCount());
			return o;
		}

	    @Override
	    public Fragment getItem(int i) {
	    	//create page with feeds of the corresponding band member
	    	if (i > getCount()-1) {
	    	return	getNewFragment(i-getCount());
	    	}else {
	    		return	getNewFragment(i);
	    	}
	    }
	
	    @Override
	    public int getCount() {
	        return mSocialMediaUsers.size();
	    }
	
	    @Override
	    public CharSequence getPageTitle(int position) {
	    	if (position > getCount()-1) {
	    		return mSocialMediaUsers.get(position-getCount()).getName();
	    	} else {
	    		return mSocialMediaUsers.get(position).getName();
	    	}
	    }  
	    
	    protected FacebookPageFragment getNewFragment(int i) {
			FacebookPageFragment fragment = new FacebookPageFragment();
		    Bundle args = new Bundle();
	        args.putInt(FacebookPageFragment.USER_ID, mSocialMediaUsers.get(i).getUserId());
	        fragment.setArguments(args);
	        return fragment;	
		}
	}
}
