package com.example.hetzi_beta.Offers;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.example.hetzi_beta.R;
import static com.example.hetzi_beta.Utils.*;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/*
 * EditOffersActivity -
 * This is the activity which in it the retailer can add new offers and edit the existing ones.
 *
 * 14/04/19 - So far this activity contains only a FAB, from which the Product Details Pop-up is called.
 *
 * */

public class EditOffersActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<Offer> offers_list;
    private OfferAdapter adapter;
    private FloatingActionButton fab;
    private RecyclerView rvOffers;
    private ViewPreloadSizeProvider mPreloadSizeProvider;
    private ListPreloader.PreloadModelProvider mPreloadModelProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offers);

        fab                 = findViewById(R.id.fab);
        rvOffers            = findViewById(R.id.offers_RecyclerView);
        mFirebaseDatabase   = FirebaseDatabase.getInstance();
        offers_list         = new ArrayList<>();

        setupAdapter();
        onClickFAB();
        setupGlidePreloader();
    }

    private void setupGlidePreloader() {
        // Glide preloader
        mPreloadSizeProvider = new ViewPreloadSizeProvider();
        mPreloadModelProvider = new MyPreloadModelProvider(offers_list, this);
        RecyclerViewPreloader<String> preloader =
                new RecyclerViewPreloader<>(Glide.with(this), mPreloadModelProvider, mPreloadSizeProvider, 10);
        rvOffers.addOnScrollListener(preloader);
    }

    private void setupAdapter() {
        // Set adapter for RecyclerView
        adapter = new OfferAdapter(offers_list);
        rvOffers.setAdapter(adapter);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void onClickFAB() {
        // Setting up the FAB so it leads to the Product Details Pop-up
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditOffersActivity.this, OfferDetailsPopupActivity.class);
                startActivityForResult(intent, HTZ_ADD_OFFER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == HTZ_ADD_OFFER) {
            // from FAB
            Offer created_offer = data.getParcelableExtra("offer");
            offers_list.add(created_offer);
            adapter.notifyDataSetChanged();
        }
    }
}
