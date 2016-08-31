package com.greenfam.fmdeals.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greenfam.fmdeals.R;

/**
 * Created by quang on 8/31/2016.
 */
public class LocalBusinessFragment extends Fragment {

    public LocalBusinessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.local_business_fragment, container, false);
    }
}
