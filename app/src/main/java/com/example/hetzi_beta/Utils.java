package com.example.hetzi_beta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.math.BigDecimal;

public class Utils {
    // Constants
    public static final int HTZ_GALLERY = 101;
    public static final int HTZ_ADD_OFFER = 102;
    public static final int HTZ_SIGN_IN = 103;
    public static final int HTZ_COVER_PHOTO_ULPOAD = 104;
    public static final int HTZ_LOGO_ULPOAD = 105;
    public static final int HTZ_CAMERA = 106;

    // -------------- Math ------------- //

    public static Float priceAfterDiscount(Float orig_price, Integer discount) {
        float discount_percent = discount.floatValue() / 100;
        return orig_price * (1 - discount_percent);
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static Float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static int timeFromStringToSecsAsInt(String input) {
        // TODO : TIME OVERHAUL
        return 0;
    }

    // ------------- Android ----------- //

    public static void updateViewImage(Activity activity, Uri image_uri, ImageView dest) {
        Glide.with(activity)
                .load(image_uri)
                .centerCrop()
                .into(dest);
    }

    public static void updateViewImage(Activity activity, File image_as_file, ImageView dest) {
        Glide.with(activity)
                .load(image_as_file)
                .centerCrop()
                .into(dest);
    }
}
