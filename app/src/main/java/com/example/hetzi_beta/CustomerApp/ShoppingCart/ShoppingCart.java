package com.example.hetzi_beta.CustomerApp.ShoppingCart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.hetzi_beta.CustomerApp.HomePage.CustomerHomeActivity;
import com.example.hetzi_beta.CustomerApp.LiveSales.Deal;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Transactions.BusinessTransaction;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import static com.example.hetzi_beta.Utils.Utils.HTZ_CART_POPUP;

public class ShoppingCart {
    // Key: receiver_uid, Value: Map of transactions/deals from the user to this shop (key: offer_id)
    private Map<String, Map<String, BusinessTransaction>>       BTs_by_seller;
    private Map<String, Map<String, BusinessTransaction>>       CTs_by_seller;
    private Map<String, Map<String, Deal>>                      deals_by_seller;
    private Context     mContext;

    private Integer size;
    private String current_shop_name;
    private String current_shop_uid;

    private ShoppingCart(Context mContext) {
        this.mContext       = mContext;
        BTs_by_seller       = new HashMap<>();
        CTs_by_seller       = new HashMap<>();
        deals_by_seller     = new HashMap<>();
        size                = 0;
    }

    private static ShoppingCart instance = null;

    public static ShoppingCart getInstance(Context mContext) {
        if(instance == null) {
            instance = new ShoppingCart(mContext);
        }
        instance.mContext = mContext;
        return instance;
    }

    public void addDeal(Deal deal) {
        if (size == 0) {
            current_shop_uid    = deal.getShop().getUid();
            current_shop_name   = deal.getShop().getShopName();
        }


        if (!deal.getShop().getUid().equals(current_shop_uid)) {
            showDialogChangeShop(deal);
        } else {
            BusinessTransaction curr_tran = new BusinessTransaction(deal.getOffer());
            String offer_key = deal.getOffer().getFbKey();

            String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, BusinessTransaction>    curr_shop_BTs  = addItemsToList(curr_tran, offer_key, user_uid);
            Map<String, Deal>                   curr_shop_deals                 = addDealToList(deal, offer_key, user_uid);

            instance.deals_by_seller.put(user_uid, curr_shop_deals);
            instance.BTs_by_seller.put(user_uid, curr_shop_BTs);

            size++;
            ((CustomerHomeActivity)mContext).cartNotifPlus();
        }
    }

    private Map<String, Deal> addDealToList(Deal curr_deal, String offer_key, String user_uid) {
        Map<String, Deal> curr_shop_deals = instance.deals_by_seller.get(user_uid);
        if (curr_shop_deals == null) {
            // No Transactions for this shop yet in this session
            curr_shop_deals = new HashMap<>();
        }
        curr_shop_deals.put(offer_key, curr_deal);

        return curr_shop_deals;
    }

    private Map<String, BusinessTransaction> addItemsToList(BusinessTransaction curr_item, String offer_key, String user_uid) {
        Map<String, BusinessTransaction>    curr_shop_transactions  = instance.BTs_by_seller.get(user_uid);
        if (curr_shop_transactions == null) {

            // No Transactions for this shop yet in this session
            curr_shop_transactions = new HashMap<>();
            curr_shop_transactions.put(offer_key, curr_item);

        } else if (curr_shop_transactions.containsKey(offer_key)) {

            // User already added items from this offer to the cart, update quantity
            BusinessTransaction existing = curr_shop_transactions.get(offer_key);
            existing.plusOneItem();

        } else {

            // The shop had transactions before, but from this Sale this is the first
            curr_shop_transactions.put(offer_key, curr_item);

        }

        return curr_shop_transactions;
    }

    public Map<String, BusinessTransaction> getShopItems(String shop_uid) {
        return instance.BTs_by_seller.get(shop_uid);
    }

    public void removeItem(String offer_id, String shop_uid) {
        Map<String, BusinessTransaction> shop_transactions = instance.BTs_by_seller.get(shop_uid);
        size = size - shop_transactions.get(offer_id).getQuantity();
        shop_transactions.remove(offer_id);
    }


    public Integer getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Map<String, Map<String, BusinessTransaction>> getBTs_by_seller() {
        return BTs_by_seller;
    }

    public void setBTs_by_seller(Map<String, Map<String, BusinessTransaction>> BTs_by_seller) {
        this.BTs_by_seller = BTs_by_seller;
    }


    public Map<String, Map<String, Deal>> getDeals_by_seller() {
        return deals_by_seller;
    }

    public void setDeals_by_seller(Map<String, Map<String, Deal>> deals_by_seller) {
        this.deals_by_seller = deals_by_seller;
    }

    public String getCurrentShopName() {
        return current_shop_name;
    }

    public void setCurrentShopName(String current_shop_name) {
        this.current_shop_name = current_shop_name;
    }

    public String getCurrent_shop_uid() {
        return current_shop_uid;
    }

    public void setCurrent_shop_uid(String current_shop_uid) {
        this.current_shop_uid = current_shop_uid;
    }

    public void showDialogChangeShop(final Deal deal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setMessage("יש בסל מוצרים מחנות אחרת, מה לעשות איתם?");
        builder.setCancelable(true);

        builder.setNegativeButton("אפשר למחוק", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                clearCart();
                ((CustomerHomeActivity)mContext).cartNotifEmpty();
                addDeal(deal);
                dialog.cancel();
            }
        });

        builder.setPositiveButton("רוצה לקנות אותם", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(mContext,"המוצר לא נוסף לסל", Toast.LENGTH_SHORT).show();

                // launch popup cart activity
                Intent intent = new Intent(mContext, ViewCartPopupActivity.class);
                ((AppCompatActivity)mContext).startActivityForResult(intent, HTZ_CART_POPUP);

                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_dialog_round_corners));
        alertDialog.show();
    }

    public void clearCart() {
        BTs_by_seller.clear();
        deals_by_seller.clear();
        size = 0;
    }
}
