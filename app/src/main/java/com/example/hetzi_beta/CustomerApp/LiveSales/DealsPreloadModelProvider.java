package com.example.hetzi_beta.CustomerApp.LiveSales;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DealsPreloadModelProvider implements ListPreloader.PreloadModelProvider<String> {
    private ArrayList<Deal> mDeals;
    private Context mContext;

    public DealsPreloadModelProvider(ArrayList<Deal> mDeals, Context mContext) {
        this.mDeals = mDeals;
        this.mContext = mContext;
    }

    @Override
    @NonNull
    public List<String> getPreloadItems(int position) {
        String url = mDeals.get(position).getOffer().getPhotoUrl();
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
