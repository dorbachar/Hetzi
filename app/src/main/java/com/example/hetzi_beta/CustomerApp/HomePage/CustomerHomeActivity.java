package com.example.hetzi_beta.CustomerApp.HomePage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hetzi_beta.CustomerApp.CustomerSettingsFragment;
import com.example.hetzi_beta.CustomerApp.DiscoverFragment;
import com.example.hetzi_beta.CustomerApp.FavouritesFragment;
import com.example.hetzi_beta.CustomerApp.LiveSales.LiveSalesFragment;
import com.example.hetzi_beta.CustomerApp.ShoppingCart.ViewCartPopupActivity;
import com.example.hetzi_beta.CustomerApp.ShopsList.ShopsListFragment;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.CustomerApp.ShoppingCart.ShoppingCart;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.hetzi_beta.Utils.HTZ_ADD_OFFER;


public class CustomerHomeActivity extends AppCompatActivity {
    private ViewPager                       mViewPager;
    private Toolbar                         mToolbar;
    private TextView                        mToolbarTitle;
    private FancyButton                     mNotifNumber;
    private ImageView                       mCartButton;

    String[] toolbar_titles = {
            "כל המבצעים",
            "כל החנויות",
            "מועדפים",
            "DISCOVER",
            "הגדרות"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // Toolbar related
        mToolbar        = findViewById(R.id.my_toolbar);
        mNotifNumber    = findViewById(R.id.notif_num);
        mToolbarTitle   = mToolbar.findViewById(R.id.toolbar_title_TextView);
        mNotifNumber.setVisibility(View.GONE);

        mViewPager      = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);

        final TabLayout tabLayout = setupTabLayout();
        setupKeyboardVisibility(tabLayout);

        mCartButton     = mToolbar.findViewById(R.id.cart_icon_ImageView);
        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerHomeActivity.this, ViewCartPopupActivity.class);
                intent.putExtra("new", true);
                startActivityForResult(intent, HTZ_ADD_OFFER);
            }
        });
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


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mToolbarTitle.setText(toolbar_titles[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

    public void cartNotifPlus() {
        ShoppingCart cart = ShoppingCart.getInstance();
        Integer curr_size = cart.getSize();

        if (curr_size == 1) {
            mNotifNumber.setVisibility(View.VISIBLE);
        }

        mNotifNumber.setText(curr_size.toString());
    }

    public void cartNotifMinus(Integer amount) {
        ShoppingCart cart = ShoppingCart.getInstance();
        Integer curr_size = cart.getSize();
        cart.setSize(curr_size - amount);

        if (curr_size == 0) {
            mNotifNumber.setVisibility(View.GONE);
        }

        mNotifNumber.setText(curr_size.toString());
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