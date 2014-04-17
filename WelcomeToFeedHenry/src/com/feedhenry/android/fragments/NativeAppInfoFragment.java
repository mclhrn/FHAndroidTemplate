package com.feedhenry.android.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feedhenry.android.R;

public class NativeAppInfoFragment extends Fragment {
	
	private View rootView;
	private String manufacturer, model, product, serial, cpu, host, release, codename;
	private int sdkNum;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_app_info, container, false);
        getVars();
        initUI(); 
        return rootView;
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