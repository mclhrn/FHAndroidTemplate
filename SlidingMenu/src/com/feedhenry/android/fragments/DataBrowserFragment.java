package com.feedhenry.android.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feedhenry.android.R;
import com.feedhenry.android.R.id;
import com.feedhenry.android.R.layout;
import com.feedhenry.android.server.FHAgent;
import com.feedhenry.android.utilities.KeyboardToggle;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;

public class DataBrowserFragment extends Fragment implements OnClickListener {

	
	private View rootView;
	private EditText et;
	private TextView success, fail;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_data_browser, container,
				false);
		initUI();
		return rootView;
	}

	
	public void initUI() {
		// TODO Possible Memory Issues Here creating new instances of font
		Typeface font = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/fontawesome-webfont.ttf");

		Button dataBrowserButton = (Button) rootView
				.findViewById(R.id.data_browser_btn);
		dataBrowserButton.setTypeface(font);
		dataBrowserButton.setOnClickListener(this);

		et = (EditText) rootView.findViewById(R.id.user_name);
		
		success = (TextView) rootView.findViewById(R.id.data_browser_success);
		success.setVisibility(View.GONE);
		fail = (TextView) rootView.findViewById(R.id.data_browser_fail);
		fail.setVisibility(View.GONE);
	}

	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.data_browser_btn) {
			if (validateFields()) {
				FHAgent fhAgent = new FHAgent();
		        fhAgent.dataBrowser(et.getText().toString(), new FHActCallback() {
		            @Override
		            public void success(FHResponse fhResponse) {
		        		success.setVisibility(View.VISIBLE);
		        		Log.i("FEEDHENRY", "Data Browser Success!");
		            }

		            @Override
		            public void fail(FHResponse fhResponse) {
		        		fail.setVisibility(View.VISIBLE);
		        		Log.i("FEEDHENRY", "Data Browser Failed!");
		            }
		        });
		        KeyboardToggle.hideTheKeyboard(getActivity(), et);
			}
		}
	}

	
	private boolean validateFields() {
		if (et.getText().toString().length() == 0) {
			et.setError("Name is required!");
			et.requestFocus();
			return false;
		} else {
			return true;
		}
	}
}