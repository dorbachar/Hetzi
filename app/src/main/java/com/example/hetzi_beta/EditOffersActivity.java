package com.example.hetzi_beta;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
* EditOffersActivity -
* This is the activity which in it the retailer can add new offers and edit the existing ones.
*
* 14/04/19 - So far this activity contains only a FAB, from which the Product Details Pop-up is called.
*
* */

public class EditOffersActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOffersDatabaseReference = mFirebaseDatabase.getReference().child("offers");

        // Setting up the FAB so it leads to the Product Details Pop-up
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditOffersActivity.this, ProductDetailsPopupActivity.class);
                startActivity(intent);
            }
        });
    }
}
