package com.noolite;

import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//Fragment, отображающий подробную информацию по взаимодействию pebble и приложения
public class PageFragment extends Fragment {

	static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

	int pageNumber;

	static PageFragment newInstance(int page) {
	    PageFragment pageFragment = new PageFragment();
	    Bundle arguments = new Bundle();
	    arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
	    pageFragment.setArguments(arguments);
	    return pageFragment;
	  }
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
	  }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment, null);

		ImageView page = (ImageView) view.findViewById(R.id.page);
		TextView info = (TextView) view.findViewById(R.id.information);
		TextView info1 = (TextView) view.findViewById(R.id.information1);
		TextView info2 = (TextView) view.findViewById(R.id.information2);
		info1.setVisibility(View.GONE);
		info2.setVisibility(View.GONE);

		//отображение необходимой страницы информации
		switch (pageNumber) {
		case 0:
			page.setImageResource(R.drawable.page1);
			info.setText(R.string.page0);
			info1.setVisibility(View.VISIBLE);
			info2.setVisibility(View.VISIBLE);
			break;
		case 1:
			page.setImageResource(R.drawable.page2);
			info.setText(R.string.page1);
			break;
		case 2:
			page.setImageResource(R.drawable.page3);
			info.setText(R.string.page2);
			break;
		case 3:
			page.setImageResource(R.drawable.page4);
			info.setText(R.string.page3);
			break;
		}

		return view;
	}
}