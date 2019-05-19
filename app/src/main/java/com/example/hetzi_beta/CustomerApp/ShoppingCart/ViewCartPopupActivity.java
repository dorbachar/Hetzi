package com.example.hetzi_beta.CustomerApp.ShoppingCart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hetzi_beta.CustomerApp.LiveSales.Deal;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Transactions.Transaction;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ViewCartPopupActivity extends AppCompatActivity {
    private ListView offers_ListView;
    private ArrayList<Deal> deals_list = new ArrayList<>();
    private ArrayList<Transaction> trans_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart_popup);

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

        TextView shop_name = findViewById(R.id.shop_name_TextView);
        shop_name.setText(deals_list.get(0).getShop().getShopName());
    }



}
