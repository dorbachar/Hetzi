package com.example.hetzi_beta.CustomerApp.ShoppingCart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hetzi_beta.CustomerApp.LiveSales.Deal;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Transactions.Transaction;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class ViewCartPopupActivity extends AppCompatActivity {
    private ListView offers_ListView;
    private ArrayList<Deal> deals_list = new ArrayList<>();
    private ArrayList<Transaction> trans_list = new ArrayList<>();
    private FancyButton mPayButton;
    private TextView mEmptyBasketTextView;
    private TextView mAddItemsTextView;
    private ImageView mCartPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart_popup);
        mEmptyBasketTextView    = findViewById(R.id.empty_basket_TextView);
        mAddItemsTextView       = findViewById(R.id.add_items_TextView);
        mPayButton              = findViewById(R.id.pay_FancyButton);
        mCartPic                = findViewById(R.id.cart_pic);

        Iterator it = ShoppingCart.getInstance().getDeals_by_seller().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            Iterator inner_itr = ((Map<String, Deal>)pair.getValue()).entrySet().iterator();
            while (inner_itr.hasNext()) {
                Map.Entry pair2 = (Map.Entry)inner_itr.next();
                deals_list.add((Deal)pair2.getValue());
            }
        }

        Iterator it2 = ShoppingCart.getInstance().getTrans_by_seller().entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pair3 = (Map.Entry)it2.next();

            Iterator inner_itr2 = ((Map<String, Transaction>)pair3.getValue()).entrySet().iterator();
            while (inner_itr2.hasNext()) {
                Map.Entry pair4 = (Map.Entry)inner_itr2.next();
                trans_list.add((Transaction) pair4.getValue());
            }
        }

        ViewCartAdapter adapter = new ViewCartAdapter(this, R.layout.item_cart_list, deals_list, trans_list);
        offers_ListView = findViewById(R.id.offers_list);
        offers_ListView.setAdapter(adapter);

        if (!deals_list.isEmpty() && !trans_list.isEmpty()) {
            uiCartNonEmpty();
        } else {
            uiCartEmpty();
        }

        mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void uiCartEmpty() {
        mCartPic.setImageResource(R.drawable.shopping_cart_plus_primarydark);
        mEmptyBasketTextView.setVisibility(View.VISIBLE);
        mAddItemsTextView.setVisibility(View.VISIBLE);
    }

    private void uiCartNonEmpty() {
        mCartPic.setImageResource(R.drawable.shopping_cart_primarydark);
        mEmptyBasketTextView.setVisibility(View.GONE);
        mAddItemsTextView.setVisibility(View.GONE);
    }


}
