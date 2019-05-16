package com.example.hetzi_beta.CustomerApp.HomePage;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.example.hetzi_beta.CustomerApp.CustomerSettingsFragment;
import com.example.hetzi_beta.CustomerApp.DiscoverFragment;
import com.example.hetzi_beta.CustomerApp.FavouritesFragment;
import com.example.hetzi_beta.CustomerApp.LiveSales.LiveSalesFragment;
import com.example.hetzi_beta.CustomerApp.ShopsListFragment;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.ToolbarActivity;
import com.example.hetzi_beta.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import static com.example.hetzi_beta.Utils.HTZ_LOCATION_NOT_FOUND;


public class CustomerHomeActivity extends ToolbarActivity {
    private ViewPager mViewPager;
    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);

        final TabLayout tabLayout = setupTabLayout();
        setupKeyboardVisibility(tabLayout);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Utils.isLocationPermissionGranted(this)) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
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







    private void setupKeyboardVisibility(final TabLayout tabLayout) {
        // TODO : Works but screen glitches and EditTexts gets squeeshed
        KeyboardVisibilityEvent.setEventListener(this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        tabLayout.setVisibility(isOpen ? View.GONE : View.VISIBLE);
                    }
                });
    }

    private TabLayout setupTabLayout() {
        final TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.tab_icon_offers);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_icon_shops);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_icon_favourites);
        tabLayout.getTabAt(3).setIcon(R.drawable.tab_icon_discover);
        tabLayout.getTabAt(4).setIcon(R.drawable.tab_icon_settings);

        tabLayout.setTabTextColors(getResources().getColor(R.color.White), getResources().getColor(R.color.colorAccent));
        tabLayout.setTabRippleColor(null);
        return tabLayout;
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new LiveSalesFragment(), "מבצעים");
        adapter.addFragment(new ShopsListFragment(), "חנויות");
        adapter.addFragment(new FavouritesFragment(), "מועדפים");
        adapter.addFragment(new DiscoverFragment(), "Discover");
        adapter.addFragment(new CustomerSettingsFragment(), "הגדרות");

        viewPager.setAdapter(adapter);
    }

//    @Override
//    public void onBackPressed () {
        // TODO : not working... see github for details
//        EditShopFragment edit_shop = (EditShopFragment) getSupportFragmentManager().findFragmentById(R.id.shop_page_fragment);
//        if(edit_shop != null && edit_shop.changesMade()) {
//            edit_shop.showAskDialog();
//        } else {
//            finish();
//        }
//    }
}