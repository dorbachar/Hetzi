package com.example.hetzi_beta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class OfferAdapter extends android.support.v7.widget.RecyclerView.Adapter<OfferAdapter.OfferViewholder> {
    private ArrayList<Offer> mOffers;

    public OfferAdapter(ArrayList<Offer> offers) {
        mOffers = offers;
    }

    @Override
    public OfferViewholder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // called when a new ViewHolder instance is created by RecyclerView
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_offer;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        OfferViewholder viewHolder = new OfferViewholder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OfferViewholder holder, int position) {
        // called when RecyclerView wants to populate the view with data
        Offer current_offer = mOffers.get(position);

        // TODO : use https://bumptech.github.io/glide/int/recyclerview.html#recyclerviewpreloader
//        Glide.with(this)
//                .load(current_offer.getPhotoUrl())
//                .into(holder.background_image_RelativeLayout);

        holder.amount_TextView.setText(current_offer.getQuantity().toString());
        holder.time_TextView.setText(current_offer.getTimeInSecs().toString()); // TODO : TIME OVERHAUL
        holder.name_TextView.setText(current_offer.getTitle().toString());
        holder.orig_price_textView.setText(current_offer.getOrigPrice().toString());
        holder.price_TextView.setText(priceAfterDiscount(current_offer.getOrigPrice(),
                current_offer.getDiscount()).toString());
        holder.precentage_textView.setText(current_offer.getDiscount().toString());
    }

    private Float priceAfterDiscount(Float orig_price, Integer discount) {
        Float discount_percent = discount.floatValue() / 100;
        return orig_price * (1 - discount_percent);
    }

    @Override
    public int getItemCount() {
        // returns number of items in the data source
        return mOffers.size();
    }

    class OfferViewholder extends RecyclerView.ViewHolder {
        RelativeLayout background_image_RelativeLayout;
        TextView name_TextView;
        TextView time_TextView;
        TextView amount_TextView;
        TextView precentage_textView;
        TextView price_TextView;
        TextView orig_price_textView;

        public OfferViewholder(View itemView) {
            super(itemView);

            // Init all views
            background_image_RelativeLayout = itemView.findViewById(R.id.background_image_RelativeLayout);
            name_TextView = itemView.findViewById(R.id.product_item_name_TextView);
            time_TextView = itemView.findViewById(R.id.product_item_time_TextView);
            amount_TextView = itemView.findViewById(R.id.amount_TextView);
            precentage_textView = itemView.findViewById(R.id.precentage_textView);
            price_TextView = itemView.findViewById(R.id.price_TextView);
            orig_price_textView = itemView.findViewById(R.id.orig_price_textView);

        }
    }
}
