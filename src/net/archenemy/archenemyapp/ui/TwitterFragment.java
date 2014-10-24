package net.archenemy.archenemyapp.ui;

import net.archenemy.archenemyapp.R;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import net.archenemy.archenemyapp.data.BandMember;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TwitterFragment extends BaseFragment {
	
	protected static final int TITLE = R.string.title_twitter;
    public static final String TAG = "TwitterFragment";
    
    //view pager
	protected ViewPager mViewPager;
	protected TwitterPagerAdapter mPagerAdapter;

	//one page for each band member
	protected static ArrayList<BandMember> mBandMembers;
	
	private View mView;
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    
		super.onCreateView(inflater, container, savedInstanceState);
	    mView = inflater.inflate(R.layout.news_fragment, container, false);
	    init();
	 	mBandMembers = mDataAdapter.getEnabledBandMembers();

        mViewPager = (ViewPager) mView.findViewById(R.id.pager);
        
        refresh();
	    	
	    return mView;
	}
	
	public int getTitle() {
		return TITLE;
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	public void refresh(){
		if (mFragmentManager != null && mViewPager != null) {
			synchronized (mFragmentManager) {

		 	mPagerAdapter = new TwitterPagerAdapter(mFragmentManager);	        
				if (mPagerAdapter != null) {
					mViewPager.setOffscreenPageLimit(6);
					mViewPager.setAdapter(mPagerAdapter);
		//			mPagerAdapter.notifyDataSetChanged();
				}
			}
		}
	}
	

	class TwitterPagerAdapter extends FragmentPagerAdapter {
		
	    @Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
	    	Object o = super.instantiateItem(container, position);
			return o;
		}

	    public TwitterPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
	
	    @Override
	    public Fragment getItem(int i) {
	    	//create page with feeds of the corresponding band member
	    	return	getNewFragment(i);
	    }
	
	    @Override
	    public int getCount() {
	        return mBandMembers.size();
	    }
	
	    @Override
	    public CharSequence getPageTitle(int position) {
	        return mBandMembers.get(position).getName();
	    }  
	    
	    protected TwitterPageFragment getNewFragment(int i) {
			TwitterPageFragment fragment = new TwitterPageFragment();
		    Bundle args = new Bundle();
	        args.putInt(TwitterPageFragment.USER_ID, i+1);
	        fragment.setArguments(args);
	        return fragment;	
		}
	}
}
