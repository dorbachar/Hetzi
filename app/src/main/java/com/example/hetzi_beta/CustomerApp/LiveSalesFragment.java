package com.example.hetzi_beta.CustomerApp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hetzi_beta.R;

public class LiveSalesFragment extends Fragment {
    public LiveSalesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layouts for this fragment
        return inflater.inflate(R.layout.fragment_live_sales, container, false);
    }
}
