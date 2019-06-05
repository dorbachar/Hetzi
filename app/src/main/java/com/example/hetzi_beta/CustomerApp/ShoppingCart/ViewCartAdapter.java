package com.example.hetzi_beta.CustomerApp.ShoppingCart;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hetzi_beta.CustomerApp.LiveSales.Deal;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Transactions.BusinessTransaction;
import com.example.hetzi_beta.Utils.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class ViewCartAdapter extends ArrayAdapter {
    private Activity context;
    private int list_item_resId;
    private ArrayList<Deal>                 deals_list;
    private ArrayList<BusinessTransaction>  BTs_list;

    @Override
    public int getCount() {
        return deals_list.size();
    }

    public ViewCartAdapter(Activity context, int item, ArrayList<Deal> deals_list, ArrayList<BusinessTransaction> trans_list) {
        super(context, item);

        this.list_item_resId = item;
        this.deals_list = deals_list;
        this.BTs_list = trans_list;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(list_item_resId, parent, false);

        TextView quantity_TextView       = rowView.findViewById(R.id.quantity_TextView);
        TextView title                   = rowView.findViewById(R.id.title_TextView);
        TextView short_description       = rowView.findViewById(R.id.short_description_TextView);
        TextView price                   = rowView.findViewById(R.id.price);
        CircularImageView img            = rowView.findViewById(R.id.offer_pic_CircularImageView);

        Integer quantity = BTs_list.get(position).getQuantity();
        quantity_TextView       .setText(quantity.toString());
        title                   .setText(deals_list.get(position).getOffer().getTitle());
        short_description        .setText(generateShortDescription(deals_list.get(position)));
        price                   .setText(((Float)(deals_list.get(position).getOffer().priceAfterDiscount() * quantity)).toString());
        Utils.updateViewImage(context, Uri.parse(deals_list.get(position).getOffer().getPhotoUrl()), img);

        return rowView;
    }

    private String generateShortDescription(Deal deal) {
        return deal.getShop().getShopName() + " ," + deal.getOffer().priceAfterDiscount().toString();
    }
}
