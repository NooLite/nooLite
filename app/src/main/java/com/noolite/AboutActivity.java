package com.noolite;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

//Activity для отображения информации о работе приложения с часами pebble
public class AboutActivity extends FragmentActivity{

	static final int PAGE_COUNT = 4;

	ViewPager pager;
	PagerAdapter pagerAdapter;
	ActionBar actionBar;
	View view;
	Button goToStart;

	//Переход на Activity настроек по нажатию на кнопку "назад"
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, PebbleSettings.class);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		
		LayoutInflater vi = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = vi.inflate(R.layout.action_bar_settings_activity, null);
		actionBar = getActionBar();

		actionBar.setCustomView(view, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		
		ImageButton back = (ImageButton) view.findViewById(R.id.backBtnSettings);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), PebbleSettings.class);
				startActivity(intent);
				finish();
			}
		});
		

		pager = (ViewPager) findViewById(R.id.pager);
		pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);

		//обработка событий перелистывания страниц с информацией
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				ImageView pageState = (ImageView) findViewById(R.id.pageState);
				
				switch (position) {
				case 0:
					pageState.setImageResource(R.drawable.dots1);
					
					break;
				case 1:
					pageState.setImageResource(R.drawable.dots2);
					
					break;
				case 2:
					pageState.setImageResource(R.drawable.dots3);
					
					break;
				case 3:
					pageState.setImageResource(R.drawable.dots4);
					
					break;
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		//кнопка для перехода на начальную страницу
		goToStart = (Button) findViewById(R.id.goToStart);
		goToStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(0);
				ImageView pageState = (ImageView) findViewById(R.id.pageState);
				pageState.setImageResource(R.drawable.dots1);
			}
		});
		
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return PageFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

	}

}
