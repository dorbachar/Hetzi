package com.example.hetzi_beta.CustomerApp.ShoppingCart;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hetzi_beta.CustomerApp.LiveSales.Deal;
import com.example.hetzi_beta.Utils.HtzWrapperActivity;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Transactions.BusinessTransaction;
import com.example.hetzi_beta.Transactions.CustomerTransaction;
import com.example.hetzi_beta.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.example.hetzi_beta.Utils.Utils.HTZ_CART_POPUP;

public class ViewCartPopupActivity extends HtzWrapperActivity {
    private ListView                        offers_ListView;

    private ArrayList<Deal>                 deals_list = new ArrayList<>();
    private ArrayList<BusinessTransaction>  BT_list = new ArrayList<>();

    private FancyButton mPayButton;
    private TextView    mEmptyBasketTextView;
    private TextView    mAddItemsTextView;
    private ImageView   mCartPic;
    private TextView    mShopTitle;

    private TextView mTotalOrigPrice;
    private TextView mTotalSaved;
    private TextView mTotalToPay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart_popup);
        mEmptyBasketTextView    = findViewById(R.id.empty_basket_TextView);
        mAddItemsTextView       = findViewById(R.id.add_items_TextView);
        mPayButton              = findViewById(R.id.pay_FancyButton);
        mCartPic                = findViewById(R.id.cart_pic);
        mTotalOrigPrice         = findViewById(R.id.orig_amount_TextView);
        mTotalSaved             = findViewById(R.id.amount_saved_TextView);
        mTotalToPay             = findViewById(R.id.amount_to_pay_TextView);
        mShopTitle              = findViewById(R.id.shop_name_TextView);

        ShoppingCart cart =  ShoppingCart.getInstance(this);

        Iterator it = cart.getDeals_by_seller().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            Iterator inner_itr = ((Map<String, Deal>)pair.getValue()).entrySet().iterator();
            while (inner_itr.hasNext()) {
                Map.Entry pair2 = (Map.Entry)inner_itr.next();
                deals_list.add((Deal)pair2.getValue());
            }
        }

        Iterator it2 = cart.getBTs_by_seller().entrySet().iterator();
        while (it2.hasNext()) {
            Map.Entry pair3 = (Map.Entry)it2.next();

            Iterator inner_itr2 = ((Map<String, BusinessTransaction>)pair3.getValue()).entrySet().iterator();
            while (inner_itr2.hasNext()) {
                Map.Entry pair4 = (Map.Entry)inner_itr2.next();
                BT_list.add((BusinessTransaction) pair4.getValue());
            }
        }

        mTotalOrigPrice.setText(calcTotalOrigPrice().toString());
        mTotalToPay.setText(calcTotalPriceToPay().toString());
        mTotalSaved.setText(((Float)(calcTotalOrigPrice() - calcTotalPriceToPay())).toString());

        mShopTitle.setText(cart.getCurrentShopName());
        if (ShoppingCart.getInstance(ViewCartPopupActivity.this).isEmpty())
            Utils.disableButton(mPayButton, this, "offer");

        ViewCartAdapter adapter = new ViewCartAdapter(this, R.layout.item_cart_list, deals_list, BT_list);
        offers_ListView = findViewById(R.id.offers_list);
        offers_ListView.setAdapter(adapter);

        if (!deals_list.isEmpty() && !BT_list.isEmpty()) {
            uiCartNonEmpty();
        } else {
            uiCartEmpty();
        }

        onClickPayButton();
    }

    private void onClickPayButton() {
        mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ShoppingCart.getInstance(ViewCartPopupActivity.this).isEmpty()) {
                    pushTransactionsToDB();
                    updateOfferQuantity();

                    ShoppingCart.getInstance(ViewCartPopupActivity.this).clearCart();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("empty_cart", true);
                    setResult(HTZ_CART_POPUP, resultIntent);
                }

                finish();
            }

            private void updateOfferQuantity() {
                FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference mOffersDatabaseReference = mFirebaseDatabase.getReference()
                                            .child("offers/" + deals_list.get(0).getShop().getUid() + "/" );

                for (final Deal deal : deals_list) {
                    mOffersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int i = deals_list.indexOf(deal);
                            int new_quantity = deal.getOffer().getQuantity() - BT_list.get(i).getQuantity();

                            Map<String, Object> userUpdates = new HashMap<>();
                            userUpdates.put("quantity", new_quantity);

                            String offer_key = deal.getOffer().getFbKey();
                            mOffersDatabaseReference.child(offer_key).updateChildren(userUpdates);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            private void pushTransactionsToDB() {
                    FirebaseUser        user                    = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase    mFirebaseDatabase       = FirebaseDatabase.getInstance();

                    // Unifies all transactions of this payment under the timestamp
                    String payment_id   = Instant.now().toString().replaceAll("[^\\d]", "");

                    DatabaseReference   mBTsDatabaseReference   = mFirebaseDatabase.getReference().child("transactions/busi/" + deals_list.get(0).getShop().getUid() + "/" + payment_id);
                    DatabaseReference   mCTsDatabaseReference   = mFirebaseDatabase.getReference().child("transactions/cust/" + user.getUid() + "/" + payment_id);

                    for (BusinessTransaction curr_bt : BT_list) {
                        mBTsDatabaseReference.push().setValue(curr_bt);

                        int i = BT_list.indexOf(curr_bt);

                        CustomerTransaction curr_ct = new CustomerTransaction(deals_list.get(i).getOffer(), deals_list.get(i).getShop());

                        Float curr_sum = curr_ct.getSum();
                        curr_ct.setQuantity(curr_bt.getQuantity());
                        curr_ct.setSum(curr_ct.getQuantity() * curr_sum);
                        mCTsDatabaseReference.push().setValue(curr_ct);
                    }
                }
        });
    }

    private void uiCartEmpty() {
        mCartPic.setImageResource(R.drawable.shopping_cart_plus_primarydark);
        mEmptyBasketTextView.setVisibility(View.VISIBLE);
        mAddItemsTextView.setVisibility(View.VISIBLE);
    }

    private void uiCartNonEmpty() {
        mCartPic.setImageResource(R.drawable.shopping_cart_primarydark);
        mEmptyBasketTextView.setVisibility(View.GONE);
        mAddItemsTextView.setVisibility(View.GONE);
    }

    private Float calcTotalPriceToPay() {
        Float ret = 0f;
        for(BusinessTransaction s : BT_list) {
            ret += s.getSum();
        }
        return ret;
    }

    private Float calcTotalOrigPrice() {
        Float ret = 0f;
        for(int i = 0; i < deals_list.size(); i++) {
            ret += (deals_list.get(i).getOffer().getOrigPrice() * BT_list.get(i).getQuantity());
        }
        return ret;
    }
}
