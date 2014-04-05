package com.feedhenry.android.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.fh.JSONObject;

import android.app.Fragment;
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
	private TextView locationSuccess;
	private Button location, weather;
	private LinearLayout ll;
	private ImageView iv;
	private double lat;
	private double lng;
	private boolean showLocation = false;

	/**
	 * 
	 * TODO save UI state on orientation change
	 * 
	 * TODO Check for GPS and launch settings if switched off
	 * 
	 * **/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_location, container,
				false);
		initUI();
		if (savedInstanceState != null) {
			lat = savedInstanceState.getDouble("lat");
			lng = savedInstanceState.getDouble("lng");
			showLocation = savedInstanceState.getBoolean("showLocation");
			if (showLocation) {
				locationSuccess.setVisibility(View.VISIBLE);
				weather.setVisibility(View.VISIBLE);
				ll.setVisibility(View.VISIBLE);
			}
		}
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		savedState.putDouble("lat", lat);
		savedState.putDouble("lng", lng);
		savedState.putBoolean("showLocation", showLocation);
	}

	private void initUI() {
		Typeface font = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/fontawesome-webfont.ttf");

		location = (Button) rootView.findViewById(R.id.location_btn);
		location.setTypeface(font);
		location.setOnClickListener(this);
		weather = (Button) rootView.findViewById(R.id.weather_btn);
		weather.setTypeface(font);
		weather.setOnClickListener(this);
		weather.setVisibility(View.GONE);

		locationSuccess = (TextView) rootView
				.findViewById(R.id.location_success);
		locationSuccess.setVisibility(View.GONE);

		ll = (LinearLayout) rootView.findViewById(R.id.weather_block);
		ll.setVisibility(View.GONE);

		et = (EditText) rootView.findViewById(R.id.coords);
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
		FHAgent fhAgent = new FHAgent();
		fhAgent.getWeather(lat, lng, new FHActCallback() {
			@Override
			public void success(FHResponse fhResponse) {
				parseWeather(fhResponse.getJson());
				Log.i("FEEDHENRY",
						"Weather Success!" + fhResponse.getRawResponse());
			}
			@Override
			public void fail(FHResponse fhResponse) {
				Log.i("FEEDHENRY", "Weather Failed!");
			}
		});
	}

	
	private void getLocation() {
		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				try {
					if (null != location) {
						lat = location.getLatitude();
						lng = location.getLongitude();
						et.setText(lat + ", " + lng);
						locationSuccess.setVisibility(View.VISIBLE);
						weather.setVisibility(View.VISIBLE);
						showLocation = true;
					}
				} catch (Exception e) {
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

		JSONObject obj = json.getJSONArray("data").getJSONObject(0);
//		JSONObject obj = array.getJSONObject(0);

		// get weather icon from returned URL
		ll.setVisibility(View.VISIBLE);
		iv = (ImageView) rootView.findViewById(R.id.weather_icon);
		String URL = obj.getString("icon");
		GetImageTask task = new GetImageTask();
		task.execute(new String[] { URL });

		TextView tv = (TextView) rootView.findViewById(R.id.weather_date);
		tv.setText(obj.getString("date"));
		tv = (TextView) rootView.findViewById(R.id.weather_temp);
		tv.setText(obj.getString("low") + " - " + obj.getString("high") + " ("
				+ (char) 0x00B0 + "C)");
		tv = (TextView) rootView.findViewById(R.id.weather_desc);
		tv.setText(obj.getString("desc"));
	}

	// Private Async task to fetch weather image
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
			iv.setImageBitmap(result);
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
