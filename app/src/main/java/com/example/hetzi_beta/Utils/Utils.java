package com.example.hetzi_beta.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class Utils {
    // Constants
    public static final int     HTZ_GALLERY = 101;
    public static final int     HTZ_ADD_OFFER = 102;
    public static final int     HTZ_SIGN_IN = 103;
    public static final int     HTZ_COVER_PHOTO_ULPOAD = 104;
    public static final int     HTZ_LOGO_ULPOAD = 105;
    public static final int     HTZ_CAMERA = 106;
    public static final int     HTZ_LOCATION = 107;
    public static final Double  HTZ_LOCATION_NOT_FOUND = 0.0;
    public static final Float   HTZ_INVALID_DISTANCE = Float.valueOf(1000000000);
    public static final int     HTZ_CART_POPUP = 108;
    public static final int     HTZ_CUSTOMER = 109;
    public static final int     HTZ_BUSINESS = 110;


    public static Map<String, HtzAddress> SHOP_ADDRESS = new HashMap<String, HtzAddress>() {{
        put("ארומה אספרסו בר",  new HtzAddress(32.164903, 34.823134, "קניון שבעת הכוכבים, הרצליה"));
        put("רולדין",           new HtzAddress(32.082902, 34.781394, "אבן גבירול 90, תל אביב"));
        put("ג'פאניקה",         new HtzAddress(32.084303, 34.774167, "שד' בן גוריון 40, תל אביב"));
        put("המאפה הצרפתי",     new HtzAddress(32.023053, 34.799227, "הפלד 7, חולון"));
        put("הו מאמא!",         new HtzAddress(32.072084, 34.773703, "קינג ג'ורג' 26, תל אביב"));
        put("קפה נונה",         new HtzAddress(32.077034, 34.781574, "אבן גבירול 44, תל אביב"));
        put("קפה טוסקנה",       new HtzAddress(32.167820, 34.840367, "הנדיב 3, הרצליה"));
    }};

    public static Double user_lat = HTZ_LOCATION_NOT_FOUND;
    public static Double user_lon = HTZ_LOCATION_NOT_FOUND;

    // -------------- Math ------------- //
    public static Float priceAfterDiscount(Float origPrice, Integer discount) {
        float discount_percent = discount.floatValue() / 100;
        return round(origPrice * (1 - discount_percent),2);
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


    public static void disableButton(Button button, Context context, String type) {
        switch (type) {
            case "round":
                button.setBackground(context.getResources().getDrawable(R.drawable.shape_disabled_button));
                break;
            case "offer":
                button.setBackground(context.getResources().getDrawable(R.drawable.shape_regular_button_disabled));
                break;
        }
        button.setEnabled(false);
    }

    public static void enableButton(Button button, Context context, String type) {
        switch (type) {
            case "round":
                button.setBackground(context.getResources().getDrawable(R.drawable.shape_enabled_button));
                button.setTextColor(context.getResources().getColor(R.color.Black));
                break;
            case "offer":
                button.setBackground(context.getResources().getDrawable(R.drawable.shape_regular_button));
                break;
        }
        button.setEnabled(true);
    }

    public static void disableButton(FancyButton button, Context context, String type) {
        switch (type) {
            case "round":
                button.setBackground(context.getResources().getDrawable(R.drawable.shape_disabled_button));
                break;
            case "offer":
                button.setBackground(context.getResources().getDrawable(R.drawable.shape_regular_button_disabled));
                break;
        }
        button.setEnabled(false);
    }

    public static void enableButton(FancyButton button, Context context, String type) {
        switch (type) {
            case "round":
                button.setBackground(context.getResources().getDrawable(R.drawable.shape_enabled_button));
                button.setTextColor(context.getResources().getColor(R.color.Black));
                break;
            case "offer":
                button.setBackground(context.getResources().getDrawable(R.drawable.shape_regular_button));
                break;
        }
        button.setEnabled(true);
    }

    // hideKeyboard(getActivity()); // won't work!
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isReadStoragePermissionGranted(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                return true;
            } else {
                // Permission is revoked
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            // Permission is granted
            return true;
        }
    }

    public static boolean isWriteStoragePermissionGranted(Activity context, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                return true;
            } else {
                // Permission is revoked
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            // Permission is granted
            return true;
        }
    }

    public static boolean isLocationPermissionGranted(Activity context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                return true;
            } else {
                // Permission is revoked
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, HTZ_LOCATION);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            // Permission is granted
            return true;
        }
    }

    // ------------- Hetzi ----------- //
    public static String getTimeEstimateString(Offer offer) {
        String estimate = "";

        Instant start = Instant.parse(offer.getS_time());
        ZonedDateTime z_start = start.atZone(ZoneId.of("Israel"));

        Instant end = Instant.parse(offer.getE_time());
        ZonedDateTime z_end = end.atZone(ZoneId.of("Israel"));

        if (end.isBefore(Instant.now())) {
            return "המבצע נגמר";
        }

        if (start.isAfter(Instant.now())) {
            // Display estimate of when it starts
            estimate += "מתחיל ";
            Long days = ChronoUnit.DAYS.between(Instant.now(), start);
            if (days > 0) {
                if (days == 1) {
                    estimate += "מחר ";
                } else if (days == 2) {
                    estimate += "מחרתיים ";
                } else if (days <= 7) {
                    estimate += "בעוד " + days.toString() + " ימים";
                    return estimate;
                } else {
                    return "לא בשבוע הקרוב";
                }
            } else {
                estimate += "היום ";
            }

            // At what time
            int hour = z_start.getHour();
            if (hour < 12 && hour > 3) {
                estimate += "בבוקר";
            } else if (hour < 16) {
                estimate += "בצהריים";
            } else if (hour < 18) {
                estimate += "אחר הצהריים";
            } else if (hour < 22) {
                estimate += "בערב";
            } else {
                estimate += "בלילה";
            }
        } else {
            // Offer live right now
            estimate = "נגמר בעוד ";
            Integer hours = (int) ChronoUnit.HOURS.between(Instant.now(), end);
            if (hours > 0) {
                estimate += hours.toString();
                estimate += " שעות!";
            } else {
                Integer minutes = (int) ChronoUnit.MINUTES.between(Instant.now(), end);
                estimate += minutes.toString();
                estimate += " דקות!";
            }
        }

        return estimate;
    }

    @SuppressLint("MissingPermission")
    public static void updateUserLocation(Activity activity) {
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        if (Utils.isLocationPermissionGranted(activity)) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Utils.user_lat = location.getLatitude();
                                Utils.user_lon = location.getLongitude();
                            } else {
                                Utils.user_lat = HTZ_LOCATION_NOT_FOUND;
                                Utils.user_lon = HTZ_LOCATION_NOT_FOUND;
                            }
                        }
                    });
        }
    }
}
