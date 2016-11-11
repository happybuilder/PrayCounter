package edu.wisdom.praycounter2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import static edu.wisdom.praycounter2.R.id.tabLayout;

// 用 AppCompatActivity 比單用 Activity 多了 Tool Bar (App Bar) 的功能.
//   Ref: https://developer.android.com/training/appbar/setting-up.html
public class PrayReportActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

	//This is our tablayout
	private TabLayout tabLayout;

	//This is our viewPager
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pray_report);

		//Adding toolbar to the activity
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//Initializing the tablayout
		tabLayout = (TabLayout) findViewById(R.id.tabLayout);

		//Adding the tabs using addTab() method
		tabLayout.addTab(tabLayout.newTab().setText("合計"));
		tabLayout.addTab(tabLayout.newTab().setText("記錄"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

		//Initializing viewPager
		viewPager = (ViewPager) findViewById(R.id.pager);

		//Creating our pager adapter
		PrayReportFragmentStatePagerAdapter adapter = new PrayReportFragmentStatePagerAdapter(
				getSupportFragmentManager(), tabLayout.getTabCount());

		//Adding adapter to pager
		viewPager.setAdapter(adapter);

		//Adding onTabSelectedListener to swipe views
		tabLayout.setOnTabSelectedListener(this);
	}

	@Override
	public void onTabSelected(TabLayout.Tab tab) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {

	}

	@Override
	public void onTabReselected(TabLayout.Tab tab) {

	}
}
