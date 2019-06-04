package com.example.hetzi_beta.CustomerApp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hetzi_beta.CustomerApp.HomePage.CustomerHomeActivity;
import com.example.hetzi_beta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomerSettingsFragment extends Fragment {
//    private OnFragmentInteractionListener mListener;

    public CustomerSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view      = inflater.inflate(R.layout.fragment_customer_settings, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        return root_view;
    }
}
