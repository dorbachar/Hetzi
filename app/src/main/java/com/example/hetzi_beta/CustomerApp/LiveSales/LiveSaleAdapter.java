package com.example.hetzi_beta.CustomerApp.LiveSales;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hetzi_beta.BusinessApp.EditOffers.EditOfferAdapter;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Utils;

import java.util.ArrayList;

public class LiveSaleAdapter extends android.support.v7.widget.RecyclerView.Adapter<LiveSaleAdapter.SaleViewHolder> {
    private static final String TAG = "LiveSaleAdapter";
    private ArrayList<Deal> mDeals;
    private Context mContext;

    public LiveSaleAdapter(ArrayList<Deal> deals) {
        mDeals = deals;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext                     = viewGroup.getContext();
        int             layout_id    = R.layout.item_cust_offer;
        LayoutInflater  inflater     = LayoutInflater.from(mContext);
        View            view         = inflater.inflate(layout_id, viewGroup, false);

        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SaleViewHolder holder, int position) {
        Deal current_deal = mDeals.get(position);
        Float   price_after_discount    = Utils.round(Utils.priceAfterDiscount(current_deal.getOffer().getOrigPrice(),
                current_deal.getOffer().getDiscount()), 2);

        // Offer Details // TODO : fix after adding Preloader to here too
//        Glide.with(mContext)
//                .load(current_deal.getOffer().getPhotoUrl())
//                .centerCrop()
//                .into(holder.background_image_offer_item);

        holder.amount_TextView      .setText(current_deal.getOffer().getQuantity().toString());
        holder.time_TextView        .setText(current_deal.getOffer().getTimeInSecs().toString()); // TODO : TIME OVERHAUL
        holder.name_TextView        .setText(current_deal.getOffer().getTitle());
        holder.orig_price_TextView  .setText(current_deal.getOffer().getOrigPrice().toString());
        holder.price_TextView       .setText(price_after_discount.toString());
        holder.precentage_TextView  .setText(current_deal.getOffer().getDiscount().toString());

        // Shop Details // TODO : fix after adding Preloader to here too
        ////        Glide.with(mContext)
        ////                .load(current_deal.getShop().getLogoUri())
        ////                .centerCrop()
        ////                .into(holder.shopLogo);
        holder.shopName             .setText(current_deal.getShop().getShopName());
    }

    @Override
    public int getItemCount() {
        return mDeals.size();
    }

    class SaleViewHolder extends RecyclerView.ViewHolder {
        // Offer Details
        ImageView               background_image_offer_item;
        TextView                name_TextView;
        TextView                time_TextView;
        TextView                amount_TextView;
        TextView                precentage_TextView;
        TextView                price_TextView;
        TextView                orig_price_TextView;
        Button                  mAddOneButton;

        // Shop Details
        ImageView               shopLogo;
        TextView                shopName;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);

            // Init all views
            background_image_offer_item     = itemView.findViewById(R.id.item_photo_ImageView);
            name_TextView                   = itemView.findViewById(R.id.product_item_name_TextView);
            time_TextView                   = itemView.findViewById(R.id.product_item_time_TextView);
            amount_TextView                 = itemView.findViewById(R.id.amount_TextView);
            precentage_TextView             = itemView.findViewById(R.id.precentage_textView);
            price_TextView                  = itemView.findViewById(R.id.price_TextView);
            orig_price_TextView             = itemView.findViewById(R.id.orig_price_textView);
            mAddOneButton                   = itemView.findViewById(R.id.add_one_Button);
            shopLogo                        = itemView.findViewById(R.id.shop_logo_CircularImageView);
            shopName                        = itemView.findViewById(R.id.shop_name_TextView);
        }
    }
}
