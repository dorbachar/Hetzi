package com.example.hetzi_beta.BusinessApp.HomePage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.hetzi_beta.BusinessApp.PastDealsFragment;
import com.example.hetzi_beta.BusinessApp.SettingsFragment;
import com.example.hetzi_beta.BusinessApp.StatsFragment;
import com.example.hetzi_beta.Offers.EditableOffersListFragment;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.EditShopFragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.List;


public class BusinessHomeActivity extends AppCompatActivity {

    private SectionsPageAdapter     mSectionsPageAdapter;
    private ViewPager               mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
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

        // TODO : Works but screen glitches and EditTexts gets squeeshed
        KeyboardVisibilityEvent.setEventListener(this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        tabLayout.setVisibility(isOpen ? View.GONE : View.VISIBLE);
                    }
                });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new EditableOffersListFragment(), "מבצעים");
        adapter.addFragment(new EditShopFragment(), "עמוד עסק");
        adapter.addFragment(new PastDealsFragment(), "עסקאות");
        adapter.addFragment(new StatsFragment(), "תובנות");
        adapter.addFragment(new SettingsFragment(), "הגדרות");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed () {
        // TODO : not working... see github for details
        EditShopFragment edit_shop = (EditShopFragment) getSupportFragmentManager().findFragmentById(R.id.shop_page_fragment);
        if(edit_shop != null && edit_shop.changesMade()) {
            edit_shop.showAskDialog();
        } else {
            finish();
        }
    }
}