package com.example.hetzi_beta.BusinessApp.HomePage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.hetzi_beta.BusinessApp.EditOffers.EditableOffersListFragment;
import com.example.hetzi_beta.BusinessApp.EditShopFragment;
import com.example.hetzi_beta.BusinessApp.PastDealsFragment;
import com.example.hetzi_beta.BusinessApp.ShopSettings.ShopSettingsFragment;
import com.example.hetzi_beta.BusinessApp.StatsFragment;
import com.example.hetzi_beta.Login.LoginActivity;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.ToolbarActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;


public class BusinessHomeActivity extends ToolbarActivity {

    private ViewPager mViewPager;
    private Toolbar mToolbar;

    String[] tab_titles = {
            "מבצעים",
            "עמוד עסק",
            "עסקאות",
            "תובנות",
            "הגדרות"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "נא להיכנס למערכת", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            mToolbar = findViewById(R.id.my_toolbar);
            mToolbar.setTitleTextColor(getResources().getColor(R.color.White));
            setSupportActionBar(mToolbar);

            mViewPager = findViewById(R.id.view_pager);
            setupViewPager(mViewPager);

            final TabLayout tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            tabLayout.getTabAt(0).setIcon(R.drawable.tab_icon_offers);
            tabLayout.getTabAt(1).setIcon(R.drawable.tab_icon_shop_page);
            tabLayout.getTabAt(2).setIcon(R.drawable.tab_icon_past_deals);
            tabLayout.getTabAt(3).setIcon(R.drawable.tab_icon_stats);
            tabLayout.getTabAt(4).setIcon(R.drawable.tab_icon_settings);

            tabLayout.setTabTextColors(getResources().getColor(R.color.White), getResources().getColor(R.color.colorAccent));
            tabLayout.setTabRippleColor(null);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mToolbar.setTitle(tab_titles[tab.getPosition()]);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            // TODO : Works but screen glitches and EditTexts gets squeeshed
            KeyboardVisibilityEvent.setEventListener(this,
                    new KeyboardVisibilityEventListener() {
                        @Override
                        public void onVisibilityChanged(boolean isOpen) {
                            tabLayout.setVisibility(isOpen ? View.GONE : View.VISIBLE);
                        }
                    });
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new EditableOffersListFragment(), tab_titles[0]);
        adapter.addFragment(new EditShopFragment(), tab_titles[1]);
        adapter.addFragment(new PastDealsFragment(), tab_titles[2]);
        adapter.addFragment(new StatsFragment(), tab_titles[3]);
        adapter.addFragment(new ShopSettingsFragment(), tab_titles[4]);

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