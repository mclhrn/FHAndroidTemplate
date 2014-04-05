package com.feedhenry.android.fragments;

import com.feedhenry.android.R;
import com.feedhenry.android.R.layout;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PushNotificationsFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_push_notifications, container, false);         
        return rootView;
    }
}
