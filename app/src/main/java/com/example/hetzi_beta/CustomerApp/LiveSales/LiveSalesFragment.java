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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.example.hetzi_beta.CustomerApp.HomePage.CustomerHomeActivity;
import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.Shop;
import com.example.hetzi_beta.CustomerApp.ShoppingCart.ShoppingCart;
import com.example.hetzi_beta.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LiveSalesFragment extends Fragment implements OnClickButtonListenerDeals {
    private RecyclerView recyclerView;
    public LiveSaleAdapter mAdapter;
    public ArrayList<Deal> deals_list = new ArrayList<>();

    // Firebase related
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;
    private DatabaseReference mShopsDatabaseReference;

    // Glide related
    public ViewPreloadSizeProvider              mPreloadSizeProvider;
    public ListPreloader.PreloadModelProvider   mPreloadModelProvider;

    // Views
    public ProgressBar mLoadingCircle;
    public TextView mLoadingText;

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

        setupAdapter();
        loadAllOffersFromDb();
        setupGlidePreloader();

        return root_view;
    }

    private void setupAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter            = new LiveSaleAdapter(deals_list, this);
        recyclerView.setAdapter(mAdapter);
    }

    public void loadAllOffersFromDb() {
        final Map<String, ArrayList<Offer>> offers_cache_map = new HashMap<>();
        final Map<Shop, ArrayList<Offer>> offers_n_stores_cache = new HashMap<>();

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
                            deals_list.add(new Deal(ofr, (Shop)(pair.getKey())));
                        }
                    } else {
                        // Add only the non-existant offers
                        for(Offer ofr : (ArrayList<Offer>)(pair.getValue())) {
                            if (!offerInDealsList(ofr)) {
                                deals_list.add(new Deal(ofr, (Shop)(pair.getKey())));
                            }
                        }
                    }

                }
                mAdapter.notifyDataSetChanged();

                mLoadingCircle.setVisibility(View.GONE);
                mLoadingText.setVisibility(View.GONE);
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
        mPreloadModelProvider = new DealsPreloadModelProvider(deals_list, getActivity());
        RecyclerViewPreloader<String> preloader =
                new RecyclerViewPreloader<>(Glide.with(this), mPreloadModelProvider, mPreloadSizeProvider, 10);
        recyclerView.addOnScrollListener(preloader);
    }

    /*
     * onClickButtonOffers -
     *
     * Implementation of OnClickButtonListenerOffers.onClickButtonOffers, so that when calling
     * startActivityForResult, the onActivityResult method will be called as I want.
     *
     * */
    @Override
    public void onClickButtonDeals(View v, int position, ArrayList<Deal> deals, TextView card_quantity) {
        // (1) Update the shopping cart
        Deal from_item = deals.get(position);

        ShoppingCart cart = ShoppingCart.getInstance();
        cart.addDeal(from_item);

        CustomerHomeActivity caller = (CustomerHomeActivity) getActivity();
        caller.cartNotifPlus();

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