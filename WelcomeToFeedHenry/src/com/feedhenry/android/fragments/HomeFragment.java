package com.feedhenry.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.feedhenry.android.R;

public class HomeFragment extends Fragment implements OnClickListener {

	// TODO Set state selector for buttons
	
	OnOptionSelectedListener mCallback;
	private View rootView;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		initUI();
		return rootView;
	}

	
	private void initUI() {
		
		// TODO Possible Memory Issues Here creating new instances of font
		Typeface font = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/fontawesome-webfont.ttf");
		
		Button cloudButton = (Button) rootView.findViewById(R.id.cloud_btn);
		cloudButton.setTypeface(font);
		cloudButton.setOnClickListener(this);
		Button exampleButton = (Button) rootView.findViewById(R.id.example_btn);
		exampleButton.setTypeface(font);
		exampleButton.setOnClickListener(this);
		Button dataBrowserButton = (Button) rootView.findViewById(R.id.data_browser_btn);
		dataBrowserButton.setTypeface(font);
		dataBrowserButton.setOnClickListener(this);
		Button pushButton = (Button) rootView.findViewById(R.id.push_btn);
		pushButton.setTypeface(font);
		pushButton.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View view) {
		int selected = 0;
		switch(view.getId()) {
		case R.id.cloud_btn:  
			selected = 0;
            break;
		case R.id.push_btn:  
			selected = 1;
            break;
		case R.id.example_btn:  
			selected = 2;
			break;
		case R.id.data_browser_btn:  
			selected = 3;
			break;
		}
		mCallback.onOptionSelected(selected);
	}
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnOptionSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOptionSelectedListener");
        }
    }
	
	
	public interface OnOptionSelectedListener {
        public void onOptionSelected(int selection);
    }
}
