package com.example.hetzi_beta.Shops;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.hetzi_beta.R;

public class ShopPageActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_page);

    }

    @Override
    public void onBackPressed () {
        EditShopFragment edit_shop = (EditShopFragment) getSupportFragmentManager().findFragmentById(R.id.shop_page_fragment);
        if(edit_shop.changesMade()) {
            edit_shop.showAskDialog();
        } else {
            finish();
        }
    }
}
