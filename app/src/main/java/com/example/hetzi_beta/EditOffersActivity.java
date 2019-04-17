package com.example.hetzi_beta;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/*
* EditOffersActivity -
* This is the activity which in it the retailer can add new offers and edit the existing ones.
*
* 14/04/19 - So far this activity contains only a FAB, from which the Product Details Pop-up is called.
*
* */

public class EditOffersActivity extends AppCompatActivity {
    public static final int HTZ_ADD_OFFER =  1;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;
    private ArrayList<Offer> offers_list;

    private OfferAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);
        offers_list = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOffersDatabaseReference = mFirebaseDatabase.getReference().child("offers");

        RecyclerView rvOffers = findViewById(R.id.offers_RecyclerView);
        adapter = new OfferAdapter(offers_list);
        rvOffers.setAdapter(adapter);
        rvOffers.setLayoutManager( new LinearLayoutManager(this));

        // Setting up the FAB so it leads to the Product Details Pop-up
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditOffersActivity.this, ProductDetailsPopupActivity.class);
                startActivityForResult(intent, HTZ_ADD_OFFER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Offer created_offer = data.getParcelableExtra("offer");
        offers_list.add(created_offer);
        adapter.notifyDataSetChanged();
    }
}
