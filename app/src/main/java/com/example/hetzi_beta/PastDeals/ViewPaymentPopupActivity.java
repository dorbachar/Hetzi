package com.example.hetzi_beta.PastDeals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Transactions.Payment;
import com.example.hetzi_beta.Transactions.Transaction;
import com.example.hetzi_beta.Utils.HtzWrapperActivity;
import com.example.hetzi_beta.Utils.Utils;

import java.util.ArrayList;

import static com.example.hetzi_beta.Utils.Utils.HTZ_BUSINESS;

public class ViewPaymentPopupActivity extends HtzWrapperActivity {
    private int side;
    private Payment payment_to_disp;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> prices = new ArrayList<>();
    private ArrayList<String> quantities = new ArrayList<>();
    private ListView listView;
    private ImageView returnImageview;
    private TextView shop_name;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_payment_popup);

        shop_name = findViewById(R.id.shop_name_TextView);
        date = findViewById(R.id.date_TextView);

        Intent intent = getIntent();
        side            = intent.getIntExtra("side", HTZ_BUSINESS);
        payment_to_disp = intent.getParcelableExtra("payment_object");

        for(Transaction t : payment_to_disp.getTransactions()) {
            titles.add(t.getTitle());
            prices.add(t.getSum().toString());
            quantities.add(t.getQuantity().toString());
        }

        shop_name.setText(payment_to_disp.getTitle());
        date.setText(payment_to_disp.getDate());

        onClickReturn();
        setupListAdapter();
    }

    private void setupListAdapter() {
        TransactionAdapter adapter1 = new TransactionAdapter(this, titles, prices, quantities, R.layout.item_receipt);
        listView = findViewById(R.id.items_list);
        listView.setAdapter(adapter1);
    }

    private void onClickReturn() {
        returnImageview = findViewById(R.id.return_ImageView);
        returnImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
