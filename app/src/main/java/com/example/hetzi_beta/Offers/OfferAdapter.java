package com.example.hetzi_beta.Offers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Utils;

import java.util.ArrayList;


public class OfferAdapter extends android.support.v7.widget.RecyclerView.Adapter<OfferAdapter.OfferViewholder> {
    private ArrayList<Offer> mOffers;

    public OfferAdapter(ArrayList<Offer> offers) {
        mOffers = offers;
    }

    @Override
    public OfferViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Called when a new ViewHolder instance is created by RecyclerView
        Context         context      = viewGroup.getContext();
        int             layout_id    = R.layout.item_offer;
        LayoutInflater  inflater     = LayoutInflater.from(context);
        View            view         = inflater.inflate(layout_id, viewGroup, false);

        return new OfferViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewholder holder, int position) {
        // Called when RecyclerView wants to populate the view with data
        Offer   current_offer           = mOffers.get(position);
        Float   price_after_discount    = Utils.priceAfterDiscount(current_offer.getOrigPrice(),
                                                                        current_offer.getDiscount());

        // TODO : use https://bumptech.github.io/glide/int/recyclerview.html#recyclerviewpreloader
        //        Glide.with(this)
        //                .load(current_offer.getPhotoUrl())
        //                .into(holder.background_image_RelativeLayout);

        holder.amount_TextView      .setText(current_offer.getQuantity().toString());
        holder.time_TextView        .setText(current_offer.getTimeInSecs().toString()); // TODO : TIME OVERHAUL
        holder.name_TextView        .setText(current_offer.getTitle());
        holder.orig_price_TextView  .setText(current_offer.getOrigPrice().toString());
        holder.price_TextView       .setText(price_after_discount.toString());
        holder.precentage_TextView  .setText(current_offer.getDiscount().toString());
    }

    @Override
    public int getItemCount() {
        // important for RecyclerView
        return mOffers.size();
    }

    class OfferViewholder extends RecyclerView.ViewHolder {
        RelativeLayout  background_image_RelativeLayout;
        TextView        name_TextView;
        TextView        time_TextView;
        TextView        amount_TextView;
        TextView        precentage_TextView;
        TextView        price_TextView;
        TextView        orig_price_TextView;

        public OfferViewholder(View itemView) {
            super(itemView);

            // Init all views
            background_image_RelativeLayout = itemView.findViewById(R.id.background_image_RelativeLayout);
            name_TextView                   = itemView.findViewById(R.id.product_item_name_TextView);
            time_TextView                   = itemView.findViewById(R.id.product_item_time_TextView);
            amount_TextView                 = itemView.findViewById(R.id.amount_TextView);
            precentage_TextView             = itemView.findViewById(R.id.precentage_textView);
            price_TextView                  = itemView.findViewById(R.id.price_TextView);
            orig_price_TextView             = itemView.findViewById(R.id.orig_price_textView);

        }
    }
}
