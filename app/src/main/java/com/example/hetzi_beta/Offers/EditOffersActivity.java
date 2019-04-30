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

        fab = findViewById(R.id.fab);

        onClickFAB();
    }

    private void onClickFAB() {
        // Setting up the FAB so it leads to the Product Details Pop-up
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditOffersActivity.this, OfferDetailsPopupActivity.class);
                intent.putExtra("new", true);
                startActivityForResult(intent, HTZ_ADD_OFFER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        EditableOffersListFragment offers_list_fragment = (EditableOffersListFragment) getSupportFragmentManager().findFragmentById(R.id.offers_fragment);

        if (data != null && requestCode == HTZ_ADD_OFFER && offers_list_fragment != null) {
            // from FAB
            offers_list_fragment.mNoOffersTextView.setVisibility(View.GONE);
            offers_list_fragment.mAddOffersTextView.setVisibility(View.GONE);
            Offer created_offer = data.getParcelableExtra("offer");
            offers_list_fragment.offers_list.add(created_offer);
            offers_list_fragment.adapter.notifyDataSetChanged();
        }
    }

    public void showLoading() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("טוען מבצעים");
        dialog.show();
    }

    public void hideLoading() {
        dialog.dismiss();
    }
}
