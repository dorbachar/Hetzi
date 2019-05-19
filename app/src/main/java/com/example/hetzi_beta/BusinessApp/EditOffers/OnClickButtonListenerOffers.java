package com.example.hetzi_beta.BusinessApp.EditOffers;

import android.view.View;

import com.example.hetzi_beta.Offers.Offer;

import java.util.ArrayList;

public interface OnClickButtonListenerOffers {
    void onClickButtonOffers(View v, int position, ArrayList<Offer> offers);
}