package com.example.hetzi_beta.CustomerApp.ShopsGrid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.example.hetzi_beta.Shops.Shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopsGridPreloadModelProvider implements ListPreloader.PreloadModelProvider<String> {
    private ArrayList<Shop> mShops;
    private Context         mContext;

    public ShopsGridPreloadModelProvider(ArrayList<Shop> mShops, Context mContext) {
        this.mShops     = mShops;
        this.mContext   = mContext;
    }

    // TODO : Background thread (Service or asyntask)
    @Override
    @NonNull
    public List<String> getPreloadItems(int position) {
        // TODO : Get and Preload both images
        String url = mShops.get(position).getLogoUri();
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
