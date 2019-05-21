package com.example.hetzi_beta.CustomerApp.ShopsList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hetzi_beta.CustomerApp.LiveSales.Deal;
import com.example.hetzi_beta.CustomerApp.LiveSales.LiveSaleAdapter;
import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.Shop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ShopsListFragment extends Fragment {
    private RecyclerView recyclerView;
    public ShopsListAdapter mAdapter;
    public ArrayList<Shop> shops_list = new ArrayList<>();

    // Firebase related
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mShopsDatabaseReference;

    public ShopsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_shops_list, container, false);
        recyclerView = root_view.findViewById(R.id.shops_RecyclerView);
        mFirebaseDatabase   = FirebaseDatabase.getInstance();
        mShopsDatabaseReference     = mFirebaseDatabase.getReference().child("shops");

        // Add Shops as keys and Offer Lists as values in offers_n_stores_cache
        mShopsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot curr_shop : dataSnapshot.getChildren()) {
                    for(DataSnapshot only_child : curr_shop.getChildren()) {
                        Shop shop = only_child.getValue(Shop.class);
                        shops_list.add(shop);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        setupAdapter();

        return root_view;
    }

    private void setupAdapter() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter            = new ShopsListAdapter(shops_list, getActivity());
        recyclerView.setAdapter(mAdapter);
    }

}
