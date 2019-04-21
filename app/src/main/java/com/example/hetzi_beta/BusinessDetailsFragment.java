package com.example.hetzi_beta;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikhaellopez.circularimageview.CircularImageView;

public class BusinessDetailsFragment extends Fragment {
    public BusinessDetailsFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment BusinessDetailsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static BusinessDetailsFragment newInstance(String param1, String param2) {
//        BusinessDetailsFragment fragment = new BusinessDetailsFragment();
//        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
////        args.putString(ARG_PARAM2, param2);
////        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View _view = inflater.inflate(R.layout.fragment_business_details, container, false);
//
//        CircularImageView circularImageView = _view.findViewById(R.id.logo_CircularImageView);
//        // Set Border
//        circularImageView.setBorderColor(getResources().getColor(R.color.Grey));
//        circularImageView.setBorderWidth(10);
//        // Add Shadow with default param
//        circularImageView.addShadow();
//        // or with custom param
//        circularImageView.setShadowRadius(15);
//        circularImageView.setShadowColor(Color.RED);
//        circularImageView.setBackgroundColor(Color.RED);
//        circularImageView.setShadowGravity(CircularImageView.ShadowGravity.CENTER);


        return inflater.inflate(R.layout.fragment_business_details, container, false);
    }
}
