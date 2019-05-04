package com.example.hetzi_beta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.hetzi_beta.BusinessApp.HomePage.BusinessHomeActivity;
import com.example.hetzi_beta.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void onClickLogout(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    public void onClickBusinessHub(View view) {
        Intent intent = new Intent(this, BusinessHomeActivity.class);
        startActivity(intent);
    }

}
