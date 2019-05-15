package com.example.hetzi_beta.CustomerApp.LiveSales;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LiveSaleAdapter extends android.support.v7.widget.RecyclerView.Adapter<LiveSaleAdapter.SaleViewHolder> {
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
        Offer current_offer = current_deal.getOffer();

        loadOffer(holder, current_deal, current_offer);
        loadShop(holder, current_deal);

        if(current_offer.isActive()) {
            activateTimer(holder, current_offer);
        } else {
            resetTimer(holder, current_offer);
        }

        if (current_offer.hasEnded()) {
            Utils.disableButton(holder.mAddOneButton, mContext, "offer");
            holder.mTimer.setTextColor(mContext.getResources().getColor(R.color.Grey));
        } else {
            holder.mTimer.setTextColor(mContext.getResources().getColor(R.color.White));
        }
    }

    private void resetTimer(@NonNull final SaleViewHolder holder, final Offer current_offer) {
        Integer hours_for_timer     = current_offer.durationMinutes() / 60;
        Integer minutes_for_timer   = current_offer.durationMinutes() % 60 == 0 ? 0 : 30;

        String s_hours      = hours_for_timer > 10 ? hours_for_timer.toString() : "0" + hours_for_timer.toString();
        String s_minutes    = minutes_for_timer > 10 ? minutes_for_timer.toString() : "0" + minutes_for_timer.toString();

        holder.mTimer.setText( s_hours  + ":" + s_minutes  + ":00" );
    }

    private void activateTimer(@NonNull final SaleViewHolder holder, final Offer current_offer) {
        new CountDownTimer(current_offer.secondsTillEnd()*1000, 1000) {
            Integer hours_for_timer = current_offer.minutesTillEnd()/60;
            Integer minutes_for_timer = current_offer.minutesTillEnd() % 60;

            Map<String, Integer> time_counter = new HashMap<String, Integer>(){{
                put("Hours", hours_for_timer);
                put("Minutes", minutes_for_timer);
                put("Seconds", 0);
            }};

            public void onTick(long millisUntilFinished) {
                updateTimeMap();

                Integer hrs = time_counter.get("Hours");
                Integer mns = time_counter.get("Minutes");
                Integer scs = time_counter.get("Seconds");
                String s_hours      = hrs > 10 ? hrs.toString() : "0" + hrs.toString();
                String s_minutes    = mns > 10 ? mns.toString() : "0" + mns.toString();
                String s_seconds    = scs > 10 ? scs.toString() : "0" + scs.toString();

                holder.mTimer.setText( s_hours  + ":" + s_minutes  + ":" + s_seconds );
            }

            private void updateTimeMap() {
                Integer new_secs = time_counter.get("Seconds") - 1;
                if (new_secs >= 0) {
                    time_counter.put("Seconds", new_secs);
                } else {
                    time_counter.put("Seconds", 59);
                    Integer new_mins = time_counter.get("Minutes") - 1;
                    if (new_mins >= 0) {
                        time_counter.put("Minutes", new_mins);
                    } else {
                        time_counter.put("Minutes", 59);
                        Integer new_hours = time_counter.get("Hours") - 1;
                        if (new_hours >= 0) {
                            time_counter.put("Hours", new_hours);
                        } else {
                            time_counter.put("Hours", 0);
                        }
                    }
                }
            }

            public void onFinish() {

            }
        }.start();
    }

    private void loadShop(@NonNull SaleViewHolder holder, Deal current_deal) {
        // Shop Details
        Glide.with(mContext)
                .load(current_deal.getShop().getLogoUri())
                .centerCrop()
                .into(holder.shopLogo);
        holder.shopName             .setText(current_deal.getShop().getShopName());
    }

    private void loadOffer(@NonNull SaleViewHolder holder, Deal current_deal, Offer current_offer) {
        // Offer Details
        Glide.with(mContext)
                .load(current_deal.getOffer().getPhotoUrl())
                .centerCrop()
                .into(holder.background_image_offer_item);

        Float   price_after_discount    = Utils.round(Utils.priceAfterDiscount(current_deal.getOffer().getOrigPrice(),
                current_deal.getOffer().getDiscount()), 2);

        holder.amount_TextView      .setText(current_offer.getQuantity().toString());
        holder.time_TextView        .setText(Utils.getTimeEstimateString(current_offer));
        holder.name_TextView        .setText(current_offer.getTitle());
        holder.orig_price_TextView  .setText(current_offer.getOrigPrice().toString());
        holder.price_TextView       .setText(price_after_discount.toString());
        holder.precentage_TextView  .setText(current_offer.getDiscount().toString());
        holder.mTimer               .setText(resetTimer(current_offer));
    }

    private String resetTimer(Offer offer) {
        Integer hour = offer.durationMinutes()/60;
        Integer minute = offer.durationMinutes()%60 == 0 ? 0 : 30;

        return hour.toString() + ":" + minute.toString() + ":" + "00";
    }

    @Override
    public int getItemCount() {
        return mDeals.size();
    }

    class SaleViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        // Offer Details
        ImageView               background_image_offer_item;
        TextView                name_TextView;
        TextView                time_TextView;
        TextView                amount_TextView;
        TextView                precentage_TextView;
        TextView                price_TextView;
        TextView                orig_price_TextView;
        Button                  mAddOneButton;
        TextView                mTimer;
        // Shop Details
        ImageView               shopLogo;
        TextView                shopName;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);

            // Init all views
            background_image_offer_item     = itemView.findViewById(R.id.item_photo_ImageView);
            name_TextView                   = itemView.findViewById(R.id.product_item_name_TextView);
            time_TextView                   = itemView.findViewById(R.id.estimate_time_TextView);
            amount_TextView                 = itemView.findViewById(R.id.amount_TextView);
            precentage_TextView             = itemView.findViewById(R.id.precentage_textView);
            price_TextView                  = itemView.findViewById(R.id.price_TextView);
            orig_price_TextView             = itemView.findViewById(R.id.orig_price_textView);
            mAddOneButton                   = itemView.findViewById(R.id.add_one_Button);
            shopLogo                        = itemView.findViewById(R.id.shop_logo_CircularImageView);
            shopName                        = itemView.findViewById(R.id.shop_name_TextView);
            mTimer                          =itemView.findViewById(R.id.offer_timer_EditText);
        }
    }
}
