package com.example.hetzi_beta.BusinessApp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hetzi_beta.R;
public class StatsFragment extends Fragment {
    public StatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layouts for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

}
