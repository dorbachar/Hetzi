package com.example.hetzi_beta;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import com.example.hetzi_beta.BusinessApp.HomePage.BusinessHomeActivity;
import com.example.hetzi_beta.CustomerApp.HomePage.CustomerHomeActivity;
import com.example.hetzi_beta.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.threetenabp.AndroidThreeTen;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidThreeTen.init(this);

        FancyButton business_button = findViewById(R.id.business_hub_button);
        FancyButton customer_button = findViewById(R.id.customers_hub_button);

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(business_button, "translationY", 40f);
        animation1.setDuration(2000);
        animation1.setRepeatCount(Animation.INFINITE);
        animation1.setRepeatMode(ValueAnimator.REVERSE);
        animation1.start();

        ObjectAnimator animation2 = ObjectAnimator.ofFloat(customer_button, "translationY", 40f);
        animation2.setDuration(2000);
        animation2.setRepeatCount(Animation.INFINITE);
        animation2.setRepeatMode(ValueAnimator.REVERSE);
        animation2.start();
    }

    public void onClickBusinessHub(View view) {
        Intent intent = new Intent(this, BusinessHomeActivity.class);
        startActivity(intent);
    }


    public void onClickCustomersHub(View view) {
        Intent intent = new Intent(this, CustomerHomeActivity.class);
        startActivity(intent);
    }

}
