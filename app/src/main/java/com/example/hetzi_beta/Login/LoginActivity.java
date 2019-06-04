package com.example.hetzi_beta.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.hetzi_beta.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import static com.example.hetzi_beta.Utils.HTZ_SIGN_IN;

public class LoginActivity extends AppCompatActivity {
    // Firebase related
    public FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAccountsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseDatabase           = FirebaseDatabase.getInstance();
        mAccountsDatabaseReference  = mFirebaseDatabase.getReference().child("accounts");

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
                );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppThemeFirebaseAuth)
                        .setLogo(R.drawable.logo)
                        .build(),
                HTZ_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == HTZ_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                CustomerAccount account = new CustomerAccount(user.getUid());
//                mAccountsDatabaseReference.push().setValue(account);
                Toast.makeText(this, "Welcome " + user.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                finish();
                // ...
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
