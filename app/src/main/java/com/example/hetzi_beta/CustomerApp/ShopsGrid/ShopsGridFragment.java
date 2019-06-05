package com.example.hetzi_beta.CustomerApp.ShopsGrid;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.example.hetzi_beta.CustomerApp.HomePage.CustomerHomeActivity;
import com.example.hetzi_beta.CustomerApp.HomePage.ShopSwitcherFromFragment;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.Shop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopsGridFragment extends Fragment implements ShopSwitcherFromFragment {
    public ArrayList<Shop> shops_list = new ArrayList<>();

    // RecyclerView related
    private RecyclerView recyclerView;
    public ShopsGridAdapter mAdapter;

    // Firebase related
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mShopsDatabaseReference;

    // Glide related
    public ViewPreloadSizeProvider              mPreloadSizeProvider;
    public ListPreloader.PreloadModelProvider   mPreloadModelProvider;

    public ShopsGridFragment() {

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

        getShopsFromDbToShopsList();
        setupAdapter();
        setupGlidePreloader();

        return root_view;
    }

    private void getShopsFromDbToShopsList() {
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
    }

    private void setupGlidePreloader() {
        // Glide preloader
        mPreloadSizeProvider = new ViewPreloadSizeProvider();
        mPreloadModelProvider = new ShopsGridPreloadModelProvider(shops_list, getActivity());
        RecyclerViewPreloader<String> preloader =
                new RecyclerViewPreloader<>(Glide.with(this), mPreloadModelProvider, mPreloadSizeProvider, 10);
        recyclerView.addOnScrollListener(preloader);
    }

    private void setupAdapter() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter            = new ShopsGridAdapter(getActivity(), shops_list, this);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.indexOfChild(v);
                Shop this_shop = shops_list.get(pos);
                switchToShopPage(this_shop);
            }
        });
    }

    @Override
    public void switchToShopPage(Shop shop) {
        ((CustomerHomeActivity)getActivity()).switchToShopPage(shop);
    }
}
