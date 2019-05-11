package com.example.hetzi_beta.CustomerApp.LiveSales;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.Shop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LiveSalesFragment extends Fragment {
    private RecyclerView recyclerView;
    public LiveSaleAdapter mAdapter;
    public ArrayList<Deal> deals_list = new ArrayList<>();

    // Firebase related
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;
    private DatabaseReference mShopsDatabaseReference;

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

        setupAdapter();
        loadOffersFromDb();

        // Inflate the layouts for this fragment
        return inflater.inflate(R.layout.fragment_live_sales, container, false);
    }

    private void setupAdapter() {
        mAdapter            = new LiveSaleAdapter(deals_list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void loadOffersFromDb() {
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
                    for(Offer ofr : (ArrayList<Offer>)(pair.getValue())) {
                        deals_list.add(new Deal(ofr, (Shop)(pair.getKey())));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



    }
}