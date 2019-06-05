package com.example.hetzi_beta.CustomerApp.LiveSales;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.Shop;
import com.example.hetzi_beta.CustomerApp.ShoppingCart.ShoppingCart;
import com.example.hetzi_beta.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LiveSalesFragment extends Fragment implements OnClickButtonListenerDeals {
    private RecyclerView        recyclerView;
    public LiveSaleAdapter      mAdapter;
    public ArrayList<Deal>      deals_list = new ArrayList<>();
    public Shop                 shop_to_fetch;

    // Firebase related
    public FirebaseDatabase     mFirebaseDatabase;
    private DatabaseReference   mOffersDatabaseReference;
    private DatabaseReference   mShopsDatabaseReference;

    // Glide related
    public ViewPreloadSizeProvider              mPreloadSizeProvider;
    public ListPreloader.PreloadModelProvider   mPreloadModelProvider;

    // Views
    public ProgressBar  mLoadingCircle;
    public TextView     mLoadingText;
    public TextView     mNothingText;

    // Filter Buttons
    private RelativeLayout mFilters;
    public ImageView    mDistanceFilter;
    public ImageView    mTimeFilter;
    public ImageView    mPriceFilter;
    public TextView     mDistanceFilterText;
    public TextView     mTimeFilterText;
    public TextView     mPriceFilterText;

    public LiveSalesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root_view      = inflater.inflate(R.layout.fragment_live_sales, container, false);
        recyclerView        = root_view.findViewById(R.id.sales_RecyclerView);
        mFirebaseDatabase   = FirebaseDatabase.getInstance();

        mLoadingCircle  = root_view.findViewById(R.id.loading_circle_ProgressBar);
        mLoadingText    = root_view.findViewById(R.id.loading_TextView);
        mNothingText    = root_view.findViewById(R.id.nothing_TextView);
        mNothingText.setVisibility(View.GONE);

        setupFilterButtons(root_view);
        Utils.updateUserLocation(getActivity());

        shop_to_fetch = getArguments().getParcelable("shop_to_fetch");

        setupAdapter();
        if (inAllSalesTab()) {
            loadAllOffersFromDb();
        } else {
            mFilters.setVisibility(View.GONE);
            loadOffersOfShop(shop_to_fetch);
        }

        setupGlidePreloader();

        return root_view;
    }


    private void setupFilterButtons(View root_view) {
        mFilters                = root_view.findViewById(R.id.filters_RelativeLayout);
        mDistanceFilterText     = root_view.findViewById(R.id.distance_filter_TextView);
        mTimeFilterText         = root_view.findViewById(R.id.time_filter_TextView);
        mPriceFilterText        = root_view.findViewById(R.id.price_filter_TextView);

        mDistanceFilter         = root_view.findViewById(R.id.distance_filter_ImageView);
        mDistanceFilter         .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("distance");
            }
        });

        mTimeFilter             = root_view.findViewById(R.id.time_filter_ImageView);
        mTimeFilter             .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("time");
            }
        });

        mPriceFilter            = root_view.findViewById(R.id.price_filter_ImageView);
        mPriceFilter             .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter("price");
            }
        });
    }

    private void applyFilter(String filter) {
        updateFilterIcons(filter);

        for(Deal curr_deal : deals_list) { // TODO : export to a background task!
            if(curr_deal.getSortFilter().equals(filter)) {
                return;
            }
            curr_deal.setSortFilter(filter);
        }
        Collections.sort(deals_list);
        mAdapter.notifyDataSetChanged();
    }

    private void updateFilterIcons(String filter) {
        Integer primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        Integer primary     = getResources().getColor(R.color.colorPrimary);

        // button_colors[0] : distance, 1 : time, 2 : price
        Integer[] button_colors = new Integer[]{primary, primary, primary};

        switch(filter) {
            case "distance":
                button_colors[0] = primaryDark;
                mDistanceFilter.setImageResource(R.drawable.filter_location_selected);
                mTimeFilter.setImageResource(R.drawable.filter_time_unselected);
                mPriceFilter.setImageResource(R.drawable.filter_dollar_unselected);
                break;
            case "time":
                button_colors[1] = primaryDark;
                mDistanceFilter.setImageResource(R.drawable.filter_location_unselected);
                mTimeFilter.setImageResource(R.drawable.filter_time_selected);
                mPriceFilter.setImageResource(R.drawable.filter_dollar_unselected);
                break;
            case "price":
                button_colors[2] = primaryDark;
                mDistanceFilter.setImageResource(R.drawable.filter_location_unselected);
                mTimeFilter.setImageResource(R.drawable.filter_time_unselected);
                mPriceFilter.setImageResource(R.drawable.filter_dollar_selected);
                break;
        }

        mDistanceFilterText .setTextColor(button_colors[0]);
        mTimeFilterText     .setTextColor(button_colors[1]);
        mPriceFilterText    .setTextColor(button_colors[2]);
    }

    private void setupAdapter() {
        if (inAllSalesTab()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
        }

        mAdapter            = new LiveSaleAdapter(deals_list, this);
        recyclerView.setAdapter(mAdapter);
    }

    private boolean inAllSalesTab() {
        return shop_to_fetch == null;
    }


    public void loadOffersOfShop(final Shop shop) {
        mOffersDatabaseReference    = mFirebaseDatabase.getReference().child("offers");
        mOffersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot curr_shop_offers : dataSnapshot.getChildren()) {
                    if (curr_shop_offers.getKey().equals(shop.getUid())) {
                        for (DataSnapshot curr_offer : curr_shop_offers.getChildren()) {
                            deals_list.add(new Deal(curr_offer.getValue(Offer.class), shop));
                        }
                        break;
                    }

                }
                mAdapter.notifyDataSetChanged();
                loadingDone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadingDone() {
        mLoadingCircle.setVisibility(View.GONE);
        mLoadingText.setVisibility(View.GONE);
        if (recyclerView.getAdapter().getItemCount() == 0 && !inAllSalesTab() ) {
            mNothingText.setVisibility(View.VISIBLE);
        }
    }

    public void loadAllOffersFromDb() {
        final Map<String, ArrayList<Offer>>     offers_cache_map = new HashMap<>();
        final Map<Shop, ArrayList<Offer>>       offers_n_stores_cache = new HashMap<>();

        mOffersDatabaseReference    = mFirebaseDatabase.getReference().child("offers");
        mShopsDatabaseReference     = mFirebaseDatabase.getReference().child("shops");

        // Add Offers as values in lists to offers_cache_map with uid as key
        mOffersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot curr_shop_offers : dataSnapshot.getChildren()) {
                    ArrayList<Offer> shop_offers = new ArrayList<>();
                    for (DataSnapshot curr_offer : curr_shop_offers.getChildren()) {
                        shop_offers.add(curr_offer.getValue(Offer.class));
                    }
                    String curr_shop_uid = curr_shop_offers.getKey();
                    offers_cache_map.put(curr_shop_uid, shop_offers);
                }
                mAdapter.notifyDataSetChanged();
                loadingDone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Add Shops as keys and Offer Lists as values in offers_n_stores_cache
        mShopsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot curr_shop : dataSnapshot.getChildren()) {
                    String curr_key = curr_shop.getKey();

                    ArrayList<Offer> shop_offers = offers_cache_map.get(curr_key);
                    for(DataSnapshot only_child : curr_shop.getChildren()) {
                        if ( shop_offers != null ) {
                            Shop shop = only_child.getValue(Shop.class);
                            offers_n_stores_cache.put(shop, shop_offers);
                        }
                    }
                }

                // Update deals_list
                Iterator it = offers_n_stores_cache.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if (deals_list.size() == 0) {
                        // Add all offers, since the list is empty
                        for(Offer ofr : (ArrayList<Offer>)(pair.getValue())) {
                            if (ofr.getQuantity() > 0)
                                deals_list.add(new Deal(ofr, (Shop)(pair.getKey())));
                        }
                    } else {
                        // Add only the non-existant offers
                        for(Offer ofr : (ArrayList<Offer>)(pair.getValue())) {
                            if (!offerInDealsList(ofr)) {
                                if (ofr.getQuantity() > 0)
                                    deals_list.add(new Deal(ofr, (Shop)(pair.getKey())));
                            }
                        }
                    }

                    loadingDone();

                }
                mAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



    }

    private boolean offerInDealsList(Offer offer) {
        for(Deal deal : deals_list) {
            if(deal.getOffer().getFbKey().equals(offer.getFbKey())) {
                return true;
            }
        }
        return false;
    }

    private void setupGlidePreloader() {
        // Glide preloader
        mPreloadSizeProvider = new ViewPreloadSizeProvider();
        mPreloadModelProvider = new com.example.hetzi_beta.CustomerApp.LiveSales.DealsPreloadModelProvider(deals_list, getActivity());
        RecyclerViewPreloader<String> preloader =
                new RecyclerViewPreloader<>(Glide.with(this), mPreloadModelProvider, mPreloadSizeProvider, 10);
        recyclerView.addOnScrollListener(preloader);
    }

    @Override
    public void onClickButtonDeals(View v, int position, ArrayList<Deal> deals, TextView card_quantity) {
        // (1) Update the shopping cart
        Deal from_item = deals.get(position);

        ShoppingCart cart = ShoppingCart.getInstance(getActivity());
        cart.addDeal(from_item);

        // (2) Update the offer card
        Integer current_displayed_quantity = Integer.parseInt(card_quantity.getText().toString());
        current_displayed_quantity--;
        card_quantity.setText(current_displayed_quantity.toString());

        // (3) Disable the 'Add One' button if needed
        if (current_displayed_quantity == 0) {
            Utils.disableButton((Button)v, getActivity(), "offer");
        }
    }
}
