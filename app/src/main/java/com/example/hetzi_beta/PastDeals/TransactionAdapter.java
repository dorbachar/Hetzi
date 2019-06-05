package com.example.hetzi_beta.PastDeals;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hetzi_beta.R;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter {
    private Activity context;
    private ArrayList<String> titles;
    private ArrayList<String> prices;
    private ArrayList<String> quantities;
    private int list_item_resId;

    public TransactionAdapter(Activity context, ArrayList<String> titles, ArrayList<String> prices, ArrayList<String> quantities, int item) {
        super(context, item);

        this.context            = context;
        this.titles             = titles;
        this.prices             = prices;
        this.quantities         = quantities;
        this.list_item_resId    = item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(list_item_resId, parent, false);

        TextView title_TextView         = rowView.findViewById(R.id.title_TextView);
        TextView quantity_TextView      = rowView.findViewById(R.id.quantity_TextView);
        TextView price_TextView         = rowView.findViewById(R.id.price);
        TextView price_per_one_TextView = rowView.findViewById(R.id.price_per_one_TextView);

        title_TextView      .setText(titles.get(position));
        quantity_TextView   .setText(quantities.get(position));
        price_TextView      .setText(prices.get(position));

        Float price_per_one = Float.parseFloat(prices.get(position)) /  Float.parseFloat(quantities.get(position));
        price_per_one_TextView.setText(price_per_one.toString());

        return rowView;
    }

    @Override
    public int getCount() {
        return titles.size();
    }
}
