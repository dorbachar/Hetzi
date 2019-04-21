package com.example.hetzi_beta;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.hetzi_beta.Offers.EditOffersActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClickProducts(View view) {
        Intent intent = new Intent(this, EditOffersActivity.class);
        startActivity(intent);
    }
    public void onClickLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onClickShopPage(View view) {
        Intent intent = new Intent(this, ShopPageActivity.class);
        startActivity(intent);
    }

}
