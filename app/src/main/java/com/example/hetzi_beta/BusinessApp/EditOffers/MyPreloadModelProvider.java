package com.example.hetzi_beta.BusinessApp.EditOffers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.example.hetzi_beta.Offers.Offer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyPreloadModelProvider implements ListPreloader.PreloadModelProvider<String> {
    private ArrayList<Offer> mOffers;
    private Context mContext;

    public MyPreloadModelProvider(ArrayList<Offer> mOffers, Context mContext) {
        this.mOffers = mOffers;
        this.mContext = mContext;
    }

    @Override
    @NonNull
    public List<String> getPreloadItems(int position) {
        String url = mOffers.get(position).getPhotoUrl();
        if (TextUtils.isEmpty(url)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(url);
    }

    @Override
    @Nullable
    public RequestBuilder getPreloadRequestBuilder(String url) {
        return
                Glide.with(mContext)
                        .load(url)
                        .centerCrop();
    }
}