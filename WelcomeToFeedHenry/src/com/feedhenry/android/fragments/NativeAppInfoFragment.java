package com.feedhenry.android.fragments;

import org.json.fh.JSONObject;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feedhenry.android.R;
import com.feedhenry.android.server.FHAgent;
import com.feedhenry.android.utilities.MyToast;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;

public class NativeAppInfoFragment extends Fragment {
	
	private View rootView;
	private String manufacturer, model, product, serial, cpu, host, release, codename;
	private String fhVarAppName, fhVarAppDomain, fhVarAppEnv, fhVarAppPort;
	private int sdkNum;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_app_info, container, false);
        getVars();
        initUI();
        getCloudInfo();
        return rootView;
    }

	
	private void getCloudInfo() {
		// Use FH Agent to call the FH Vars
				FHAgent fhAgent = new FHAgent();
				fhAgent.getFHVars(new FHActCallback() {
					@Override
					public void success(FHResponse fhResponse) {
						parseInfo(fhResponse.getJson());
						Log.i("FEEDHENRY", "FH Vars Call Success! "
								+ fhResponse.getJson());
					}
					@Override
					public void fail(FHResponse fhResponse) {
						
						MyToast.showToast("Could not reach cloud");
						Log.i("FEEDHENRY", fhResponse.getRawResponse());
						Log.i("FEEDHENRY", "FH Vars Call Failed!");
					}
				});
	}
	
	
	private void parseInfo(JSONObject json) {
		
		TextView tv8 = (TextView) rootView.findViewById(R.id.textView8);
        TextView tv9 = (TextView) rootView.findViewById(R.id.textView9);
        TextView tv10 = (TextView) rootView.findViewById(R.id.textView10);
        TextView tv11 = (TextView) rootView.findViewById(R.id.textView11);
        
        // This assumes that every cloud app will have these Environment Vars
        tv8.setText(json.getString("appName"));
        tv9.setText(json.getString("domain"));
        tv10.setText(json.getString("env"));
		tv11.setText(json.getString("port"));
	}


	private void getVars() {
		manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        product = Build.PRODUCT;
        serial = Build.SERIAL;
        cpu = Build.CPU_ABI;
        host = Build.HOST;
        release = Build.VERSION.RELEASE;	
        sdkNum = Build.VERSION.SDK_INT;				
        codename = "";
        if(Build.VERSION_CODES.ICE_CREAM_SANDWICH == sdkNum || Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 == sdkNum) {
        	codename = "Ice Cream Sandwich";
        } else if(Build.VERSION_CODES.JELLY_BEAN == sdkNum || Build.VERSION_CODES.JELLY_BEAN_MR1 == sdkNum || Build.VERSION_CODES.JELLY_BEAN_MR2 == sdkNum) {
        	codename = "Jelly Bean";
        } else if(sdkNum > 18) {
        	codename = "Kit Kat";
        }
	}

	
	private void initUI() {
		TextView tv1 = (TextView) rootView.findViewById(R.id.textView1);
        TextView tv2 = (TextView) rootView.findViewById(R.id.textView2);
        TextView tv3 = (TextView) rootView.findViewById(R.id.textView3);
        TextView tv4 = (TextView) rootView.findViewById(R.id.textView4);
        TextView tv5 = (TextView) rootView.findViewById(R.id.textView5);
        TextView tv6 = (TextView) rootView.findViewById(R.id.textView6);
        TextView tv7 = (TextView) rootView.findViewById(R.id.textView7);
        
        tv1.setText(manufacturer);
        tv2.setText(model);
        tv3.setText(product);
        tv4.setText(serial);
        tv5.setText(cpu);
        tv6.setText(host);
        tv7.setText("Android Version: " + release + "  " + codename);
	}
}