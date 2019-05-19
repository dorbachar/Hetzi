package com.example.hetzi_beta.CustomerApp.LiveSales;

import android.view.View;
import android.widget.TextView;

import com.example.hetzi_beta.Offers.Offer;

import java.util.ArrayList;

public interface OnClickButtonListenerDeals {
    void onClickButtonDeals(View v, int position, ArrayList<Deal> deals, TextView quantity);
}