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
import com.example.hetzi_beta.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.hetzi_beta.Utils.Utils.HTZ_INVALID_DISTANCE;

public class LiveSaleAdapter extends android.support.v7.widget.RecyclerView.Adapter<LiveSaleAdapter.SaleViewHolder> {
    private ArrayList<Deal> mDeals;
    private Context mContext;
    private OnClickButtonListenerDeals mOnClickButtonListener;

    public LiveSaleAdapter(ArrayList<Deal> deals, OnClickButtonListenerDeals mOnClickButtonListener) {
        mDeals = deals;
        this.mOnClickButtonListener = mOnClickButtonListener;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext                     = viewGroup.getContext();
        int             layout_id    = R.layout.item_cust_offer;
        LayoutInflater  inflater     = LayoutInflater.from(mContext);
        View            view         = inflater.inflate(layout_id, viewGroup, false);

        return new SaleViewHolder(view, mOnClickButtonListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final SaleViewHolder holder, int position) {
        Deal current_deal   = mDeals.get(position);
        Offer current_offer = current_deal.getOffer();

        loadOffer(holder, current_deal, current_offer);
        loadShop(holder, current_deal);

        setupTimer(holder, current_offer);
    }

    private void setupTimer(@NonNull SaleViewHolder holder, Offer current_offer) {
        if (holder.timer != null) holder.timer.cancel();

        if(current_offer.isActive()) {
            activateTimer(holder, current_offer);
        } else {
            resetTimer(holder, current_offer);
        }

        timerAndAddOneViewsBehavior(holder, current_offer);
    }

    /*
    * Desired:
    *
    * Sale Live             : white clock (mTimer), enabled button (mAddOneButton).
    * Sale Ended            : no clock, no button.
    * Sale Haven't Begun    : grey clock, no button.
    * */
    private void timerAndAddOneViewsBehavior(@NonNull SaleViewHolder holder, Offer current_offer) {
        TextView    mTimer_c        = holder.mTimer;
        Button      mAddOneButton_c = holder.mAddOneButton;

        if (current_offer.hasEnded()) {
            Utils.disableButton(mAddOneButton_c, mContext, "offer");
            mAddOneButton_c.setTextColor(mContext.getResources().getColor(R.color.Black_Transparent));
            mTimer_c.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
        } else if (!current_offer.hasStarted()) {
            Utils.disableButton(mAddOneButton_c, mContext, "offer");
            mAddOneButton_c.setTextColor(mContext.getResources().getColor(R.color.Black_Transparent));
            mTimer_c.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
        } else {
            Utils.enableButton(mAddOneButton_c, mContext, "offer");
            mAddOneButton_c.setTextColor(mContext.getResources().getColor(R.color.White));
            mTimer_c.setTextColor(mContext.getResources().getColor(R.color.White));
        }
    }

    public void setTimer(final SaleViewHolder holder, Integer hours, Integer minutes, Integer seconds) {
        String s_hours      = hours >= 10 ? hours.toString() : "0" + hours.toString();
        String s_minutes    = minutes >= 10 ? minutes.toString() : "0" + minutes.toString();
        String s_seconds    = seconds >= 10 ? seconds.toString() : "0" + seconds.toString();
        holder.mTimer.setText( s_hours  + ":" + s_minutes  + ":" + s_seconds );
    }

    private void resetTimer(@NonNull final SaleViewHolder holder, final Offer current_offer) {
        Integer hours_for_timer     = current_offer.durationMinutes() / 60;
        Integer minutes_for_timer   = current_offer.durationMinutes() % 60 == 0 ? 0 : 30;

        setTimer(holder, hours_for_timer, minutes_for_timer, 0);
    }

    private void activateTimer(@NonNull final SaleViewHolder holder, final Offer current_offer) {
        holder.timer = new CountDownTimer(current_offer.secondsTillEnd()*1000, 1000) {
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

                setTimer(holder, hrs, mns, scs);
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

        holder.quantity_TextView.setText(current_offer.getQuantity().toString());
        holder.time_TextView        .setText(Utils.getTimeEstimateString(current_offer));
        holder.name_TextView        .setText(current_offer.getTitle());
        holder.orig_price_TextView  .setText(current_offer.getOrigPrice().toString());
        holder.price_TextView       .setText(price_after_discount.toString());
        holder.precentage_TextView  .setText(current_offer.getDiscount().toString());
        holder.mTimer               .setText(resetTimer(current_offer));

        Float dist = current_deal.getDistanceFromUser();
        if (dist.equals(HTZ_INVALID_DISTANCE)) {
            holder.mDistance.setText("מיקום כבוי");
            holder.mKilometerWord.setText("");
        } else {
            if (dist < 1) {
                holder.mDistance.setText(((Float)(dist*1000)).toString());
                holder.mKilometerWord.setText(" מטר");
            } else {
                holder.mDistance.setText(dist.toString());
                holder.mKilometerWord.setText(" קילומטר");
            }
        }
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

    // Clean all elements of the recycler
    public void clear() {
        mDeals.clear();
        notifyDataSetChanged();
    }



    class SaleViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder  implements View.OnClickListener {
        // Offer Details
        ImageView                   background_image_offer_item;
        TextView                    name_TextView;
        TextView                    time_TextView;
        TextView                    quantity_TextView;
        TextView                    precentage_TextView;
        TextView                    price_TextView;
        TextView                    orig_price_TextView;
        Button                      mAddOneButton;
        TextView                    mTimer;
        OnClickButtonListenerDeals  mOnClickButtonListener;

        // Shop Details
        ImageView                   shopLogo;
        TextView                    shopName;
        TextView                    mDistance;
        TextView                    mKilometerWord;

        // Timer
        CountDownTimer              timer;

        public SaleViewHolder(@NonNull View itemView, OnClickButtonListenerDeals mOnClickButtonListener) {
            super(itemView);

            // Init all views
            background_image_offer_item     = itemView.findViewById(R.id.item_photo_ImageView);
            name_TextView                   = itemView.findViewById(R.id.product_item_name_TextView);
            time_TextView                   = itemView.findViewById(R.id.estimate_time_TextView);
            quantity_TextView               = itemView.findViewById(R.id.amount_TextView);
            precentage_TextView             = itemView.findViewById(R.id.precentage_textView);
            price_TextView                  = itemView.findViewById(R.id.price_TextView);
            orig_price_TextView             = itemView.findViewById(R.id.orig_price_textView);
            mAddOneButton                   = itemView.findViewById(R.id.add_one_Button);
            shopLogo                        = itemView.findViewById(R.id.shop_logo_CircularImageView);
            shopName                        = itemView.findViewById(R.id.shop_name_TextView);
            mTimer                          = itemView.findViewById(R.id.offer_timer_EditText);
            mDistance                       = itemView.findViewById(R.id.distance_TextView);
            mKilometerWord                  = itemView.findViewById(R.id.kilometer);
            this.mOnClickButtonListener     = mOnClickButtonListener;

            mAddOneButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickButtonListener.onClickButtonDeals(v, getAdapterPosition(), mDeals, quantity_TextView);
        }
    }
}
