package com.feedhenry.android.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.fh.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feedhenry.android.R;
import com.feedhenry.android.server.FHAgent;
import com.feedhenry.android.utilities.MyLocation;
import com.feedhenry.android.utilities.MyLocation.LocationResult;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;

public class LocationFragment extends Fragment implements OnClickListener {

	private View rootView;
	private EditText et;
	private TextView locationSuccess, weatherDate, weatherTemp, weatherDesc;
	private String mDate, mTemp, mDesc;
	private Button locationBtn, weatherBtn;
	private Bitmap weatherBitmap;
	private LinearLayout ll;
	private ImageView iv;
	private double lat;
	private double lng;
	private boolean showLocation = false;
	private ProgressDialog dialog;

	/**
	 * TODO Check for GPS and launch settings if switched off
	 **/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_location, container,
				false);
		initUI();

		// Set state for orientation change
		if (savedInstanceState != null) {
			lat = savedInstanceState.getDouble("lat");
			lng = savedInstanceState.getDouble("lng");
			showLocation = savedInstanceState.getBoolean("showLocation");
			if (showLocation) {
				locationSuccess.setVisibility(View.VISIBLE);
				weatherBtn.setVisibility(View.VISIBLE);
				ll.setVisibility(View.VISIBLE);
				mDate = savedInstanceState.getString("date");
				mTemp = savedInstanceState.getString("temp");
				mDesc = savedInstanceState.getString("desc");
				weatherBitmap = savedInstanceState.getParcelable("image");
				weatherDate.setText(mDate);
				weatherTemp.setText(mTemp);
				weatherDesc.setText(mDesc);
				iv.setImageBitmap(weatherBitmap);
			}
		}
		return rootView;
	}

	// Save state for orientation change
	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		savedState.putDouble("lat", lat);
		savedState.putDouble("lng", lng);
		savedState.putBoolean("showLocation", showLocation);
		savedState.putParcelable("image", weatherBitmap);
		savedState.putString("date", mDate);
		savedState.putString("temp", mTemp);
		savedState.putString("desc", mDesc);
	}

	private void initUI() {
		Typeface font = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/fontawesome-webfont.ttf");

		// Set handles to UI objects
		et = (EditText) rootView.findViewById(R.id.coords);
		locationBtn = (Button) rootView.findViewById(R.id.location_btn);
		locationSuccess = (TextView) rootView
				.findViewById(R.id.location_success);
		weatherBtn = (Button) rootView.findViewById(R.id.weather_btn);
		ll = (LinearLayout) rootView.findViewById(R.id.weather_block);
		weatherDate = (TextView) rootView.findViewById(R.id.weather_date);
		weatherTemp = (TextView) rootView.findViewById(R.id.weather_temp);
		weatherDesc = (TextView) rootView.findViewById(R.id.weather_desc);
		iv = (ImageView) rootView.findViewById(R.id.weather_icon);

		// Set fonts on buttons
		locationBtn.setTypeface(font);
		weatherBtn.setTypeface(font);

		// Set click listeners
		locationBtn.setOnClickListener(this);
		weatherBtn.setOnClickListener(this);

		// Set initial visibility
		weatherBtn.setVisibility(View.GONE);
		locationSuccess.setVisibility(View.GONE);
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
		switch (view.getId()) {
		case R.id.location_btn:
			getLocation();
			break;
		case R.id.weather_btn:
			getWeather();
			break;
		}
	}

	private void getWeather() {

		// Display loading dialog
		if (dialog == null) {
			setDialog();
		} else {
			dialog.show();
		}

		// Weather FH call
		FHAgent fhAgent = new FHAgent();
		fhAgent.getWeather(lat, lng, new FHActCallback() {
			@Override
			public void success(FHResponse fhResponse) {
				dialog.dismiss();
				parseWeather(fhResponse.getJson());
				Log.i("FEEDHENRY",
						"Weather Success!" + fhResponse.getRawResponse());
			}

			@Override
			public void fail(FHResponse fhResponse) {
				dialog.dismiss();
				Log.i("FEEDHENRY", "Weather Failed!");
			}
		});
	}

	private void getLocation() {

		// Display loading dialog
		if (dialog == null) {
			setDialog();
		} else {
			dialog.show();
		}
		
		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				try {
					if (null != location) {
						dialog.dismiss();
						lat = location.getLatitude();
						lng = location.getLongitude();
						et.setText(lat + ", " + lng);
						locationSuccess.setVisibility(View.VISIBLE);
						weatherBtn.setVisibility(View.VISIBLE);
						showLocation = true;
					}
				} catch (Exception e) {
					dialog.dismiss();
					Log.i("FEEDHENRY",
							"Error fetching weather: " + e.getMessage());
				}

			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(getActivity(), locationResult);
	}

	private void parseWeather(JSONObject json) {

		/*
		 * TODO No error handling here for bad json data
		 */
		ll.setVisibility(View.VISIBLE);
		JSONObject obj = json.getJSONArray("data").getJSONObject(0);

		String URL = obj.getString("icon");
		GetImageTask task = new GetImageTask();
		task.execute(new String[] { URL });

		mDate = obj.getString("date");
		mTemp = obj.getString("low") + " - " + obj.getString("high") + " ("
				+ (char) 0x00B0 + "C)";
		mDesc = obj.getString("desc");

		weatherDate.setText(mDate);
		weatherTemp.setText(mTemp);
		weatherDesc.setText(mDesc);
	}

	// Async task to fetch weather image
	private class GetImageTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap map = null;
			for (String url : urls) {
				map = downloadImage(url);
			}
			return map;
		}

		// Sets the Bitmap returned by doInBackground
		@Override
		protected void onPostExecute(Bitmap result) {
			Log.i("FEEDHENRY", "Bitmap result: " + result.toString());
			weatherBitmap = result;
			iv.setImageBitmap(weatherBitmap);
		}

		// Creates Bitmap from InputStream and returns it
		private Bitmap downloadImage(String url) {
			Bitmap bitmap = null;
			InputStream stream = null;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;

			try {
				stream = getHttpConnection(url);
				bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return bitmap;
		}

		// Makes HttpURLConnection and returns InputStream
		private InputStream getHttpConnection(String urlString)
				throws IOException {
			InputStream stream = null;
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			try {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();

				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					stream = httpConnection.getInputStream();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return stream;
		}
	}
}
