package com.example.hetzi_beta.CustomerApp.ShopsGrid;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hetzi_beta.CustomerApp.HomePage.CustomerHomeActivity;
import com.example.hetzi_beta.CustomerApp.LiveSales.LiveSalesFragment;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.Shop;
import com.example.hetzi_beta.Utils.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ViewShopPageFragment extends Fragment {
    // This important member holds the currently displayed shop. It's loaded from the DB if the user
    // previously created a shop page, and pushed to DB in the end if the user wants to save changes.
    Shop shop_on_display;

    // Views in the fragment
    private ImageView           mShopCoverPhotoImageView;
    private CircularImageView   mLogoCircularImageView;
    private TextView            mShopName;
    private TextView            mPhysicalAddress;
    private TextView            mWebsite;
    private TextView            mPhoneNumber;

    public ViewShopPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This callback will only be called when MyFragment is at least Started.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_view_shop_page, container, false);

        ((CustomerHomeActivity)getActivity()).setEnableBackButton(true);

        initViews(root_view);
        shop_on_display = getArguments().getParcelable("shop_object");
        updateViewsFromShopMember();

        loadShopLiveSales();

        return root_view;
    }

    private void loadShopLiveSales() {
        // Load shop's sales to the inner fragment of this fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("shop_to_fetch", shop_on_display);

        Fragment fragment = new LiveSalesFragment();
        fragment.setArguments(bundle);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction
                .replace(R.id.shop_sales_FrameLayout, fragment)
                .commit();
    }

    private void initViews(View root_view) {
        mShopCoverPhotoImageView        = root_view.findViewById(R.id.shop_cover_photo_ImageView);
        mLogoCircularImageView          = root_view.findViewById(R.id.logo_CircularImageView);
        mShopName                       = root_view.findViewById(R.id.shop_name_EditText);
        mPhysicalAddress                = root_view.findViewById(R.id.shop_address_TextView);
        mWebsite                        = root_view.findViewById(R.id.shop_site_EditText);
        mPhoneNumber                    = root_view.findViewById(R.id.shop_phonenumber_EditText);
    }

    private void updateViewsFromShopMember() {
        // Update the view as per the info from DB
        String shop_name = shop_on_display.getShopName();

        mShopName           .setText(shop_name);
        mWebsite            .setText(shop_on_display.getWebsite());
        mPhoneNumber        .setText(shop_on_display.getPhone());

        if (Utils.SHOP_ADDRESS.containsKey(shop_name)) {
            mPhysicalAddress.setText(Utils.SHOP_ADDRESS.get(shop_name).getAddress());
        } else {
            mPhysicalAddress.setText("אין כתובת עדכנית במערכת");
        }


        Utils.updateViewImage(getActivity(), Uri.parse(shop_on_display.getLogoUri()), mLogoCircularImageView);
        Utils.updateViewImage(getActivity(), Uri.parse(shop_on_display.getCoverPhotoUri()), mShopCoverPhotoImageView);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((CustomerHomeActivity)getActivity()).setToolbarTitle("כל החנויות");
        ((CustomerHomeActivity)getActivity()).setEnableBackButton(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
