package com.example.hetzi_beta.Offers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Utils;

import java.util.ArrayList;

public class OfferAdapter extends android.support.v7.widget.RecyclerView.Adapter<OfferAdapter.OfferViewholder> {
    private ArrayList<Offer> mOffers;
    private Context mContext;
    private OnClickButtonListener mOnClickButtonListener;

    public OfferAdapter(ArrayList<Offer> offers, OnClickButtonListener mOnClickButtonListener) {
        mOffers = offers;
        this.mOnClickButtonListener = mOnClickButtonListener;
    }

    @Override
    public OfferViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Called when a new ViewHolder instance is created by RecyclerView
        mContext                     = viewGroup.getContext();
        int             layout_id    = R.layout.item_editable_offer;
        LayoutInflater  inflater     = LayoutInflater.from(mContext);
        View            view         = inflater.inflate(layout_id, viewGroup, false);

        return new OfferViewholder(view, mOnClickButtonListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfferViewholder holder, int position) {
        // Called when RecyclerView wants to populate the view with data
        Offer   current_offer           = mOffers.get(position);
        Float   price_after_discount    = Utils.round(Utils.priceAfterDiscount(current_offer.getOrigPrice(),
                                                                        current_offer.getDiscount()), 2);

        Glide.with(mContext)
                .load(current_offer.getPhotoUrl())
                .centerCrop()
                .into(holder.background_image_offer_item);

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

    class OfferViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView               background_image_offer_item;
        TextView                name_TextView;
        TextView                time_TextView;
        TextView                amount_TextView;
        TextView                precentage_TextView;
        TextView                price_TextView;
        TextView                orig_price_TextView;
        Button                  mEditOfferButton;
        OnClickButtonListener   mOnClickButtonListener;

        public OfferViewholder(View itemView, OnClickButtonListener mOnClickButtonListener) {
            super(itemView);

            // Init all views
            background_image_offer_item     = itemView.findViewById(R.id.item_photo_ImageView);
            name_TextView                   = itemView.findViewById(R.id.product_item_name_TextView);
            time_TextView                   = itemView.findViewById(R.id.product_item_time_TextView);
            amount_TextView                 = itemView.findViewById(R.id.amount_TextView);
            precentage_TextView             = itemView.findViewById(R.id.precentage_textView);
            price_TextView                  = itemView.findViewById(R.id.price_TextView);
            orig_price_TextView             = itemView.findViewById(R.id.orig_price_textView);
            mEditOfferButton                = itemView.findViewById(R.id.edit_offer_Button);
            this.mOnClickButtonListener     = mOnClickButtonListener;

            mEditOfferButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickButtonListener.onClickButton(v, getAdapterPosition(), mOffers);
        }
    }
}


