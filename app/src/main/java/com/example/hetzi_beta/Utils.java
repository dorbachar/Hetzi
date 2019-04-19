package com.example.hetzi_beta;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hetzi_beta.Offers.OfferDetailsPopupActivity;

public class Utils {
    // Constants
    public static final int HTZ_PHOTO_PICKER = 101;
    public static final int HTZ_ADD_OFFER = 102;
    public static final int HTZ_SIGN_IN = 103;

    public static int timeFromStringToSecsAsInt(String input) {
        // TODO : TIME OVERHAUL
        return 0;
    }

    public static void updateViewImage(Activity activity, Uri image_uri, ImageView dest) {
        Glide.with(activity)
                .load(image_uri)
                .centerCrop()
                .into(dest);
    }

    public static Float priceAfterDiscount(Float orig_price, Integer discount) {
        float discount_percent = discount.floatValue() / 100;
        return orig_price * (1 - discount_percent);
    }
}
