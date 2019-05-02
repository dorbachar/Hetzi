package com.example.hetzi_beta.Offers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.EditShopFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.hetzi_beta.Utils.HTZ_ADD_OFFER;

public class EditOffersActivity extends AppCompatActivity {
    private FloatingActionButton    fab;
    private ProgressDialog          dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offers);

    }
}
