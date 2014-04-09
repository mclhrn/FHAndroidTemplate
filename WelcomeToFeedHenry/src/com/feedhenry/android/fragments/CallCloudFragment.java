package com.feedhenry.android.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feedhenry.android.R;
import com.feedhenry.android.server.FHAgent;
import com.feedhenry.android.utilities.MyToast;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;

public class CallCloudFragment extends Fragment implements OnClickListener {

	private View rootView;
	private LinearLayout ll;
	private ProgressDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_call_cloud, container,
				false);
		initUI();
		return rootView;
	}

	public void initUI() {
		// TODO Possible Memory Issues Here creating new instances of font
		Typeface font = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/fontawesome-webfont.ttf");

		Button cloudButton = (Button) rootView
				.findViewById(R.id.call_cloud_btn);
		cloudButton.setTypeface(font);
		cloudButton.setOnClickListener(this);

		ll = (LinearLayout) rootView.findViewById(R.id.cloud_toggle);
		ll.setVisibility(View.GONE);
	}

	private void setDialog() {
		dialog = new ProgressDialog(this.getActivity());
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setMessage("Loading...");
		dialog.show();
	}

	@Override
	public void onClick(View view) {
		setDialog();
		callCloud();
	}

	private void callCloud() {
		// Use FH Agent to call the FH Cloud
		FHAgent fhAgent = new FHAgent();
		fhAgent.cloudCall(new FHActCallback() {
			@Override
			public void success(FHResponse fhResponse) {
				ll.setVisibility(View.VISIBLE);
				TextView tv = (TextView) rootView
						.findViewById(R.id.cloud_action_response_title);
				tv.setText("Response: "
						+ fhResponse.getJson().getString("text"));
				dialog.dismiss();
				Log.i("FEEDHENRY", "Cloud Call Success! "
						+ fhResponse.getJson().getString("text"));
			}

			@Override
			public void fail(FHResponse fhResponse) {
				dialog.dismiss();
				MyToast.showToast("Could not reach cloud");
				Log.i("FEEDHENRY", fhResponse.getRawResponse());
				Log.i("FEEDHENRY", "Cloud Call Failed!");
			}
		});
	}
}
