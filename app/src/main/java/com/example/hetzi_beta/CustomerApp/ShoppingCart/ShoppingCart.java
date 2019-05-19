package com.example.hetzi_beta.CustomerApp.ShoppingCart;

import com.example.hetzi_beta.CustomerApp.LiveSales.Deal;
import com.example.hetzi_beta.Transactions.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    // Key: receiver_uid, Value: Map of transactions/deals from the user to this shop (key: offer_id)
    private Map<String, Map<String, Transaction>>   trans_by_seller; // TODO : used for saving transactions in user&shop DB
    private Map<String, Map<String, Deal>>          deals_by_seller; // used for displaying the

    private Integer size;

    private ShoppingCart() {
        trans_by_seller = new HashMap<>();
        deals_by_seller = new HashMap<>();
        size = 0;
    }

    private static ShoppingCart instance = null;

    public static ShoppingCart getInstance() {
        if(instance == null) {
            instance = new ShoppingCart();
        }
        return instance;
    }

    public void addDeal(Deal deal) {
        Transaction curr_tran = new Transaction(deal.getOffer(), deal.getShop());
        String offer_key = deal.getOffer().getFbKey();

        String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Transaction>    curr_shop_transactions  = addTransToList(curr_tran, offer_key, user_uid);
        Map<String, Deal>           curr_shop_deals         = addDealToList(deal, offer_key, user_uid);

        instance.deals_by_seller.put(user_uid, curr_shop_deals);
        instance.trans_by_seller.put(user_uid, curr_shop_transactions);

        size++;
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
    private Map<String, Transaction> addTransToList(Transaction curr_tran, String offer_key, String user_uid) {
        // TODO : all of this should be converted to the new concept for Transaction,
        //  which in it a Transaction is not Offer-specific (aka offer_key), but only
        //  payer-receiver specific and the sum is the total sum in the cart.
        Map<String, Transaction>    curr_shop_transactions  = instance.trans_by_seller.get(user_uid);
        if (curr_shop_transactions == null) {

            // No Transactions for this shop yet in this session
            curr_shop_transactions = new HashMap<>();
            curr_shop_transactions.put(offer_key, curr_tran);

        } else if (curr_shop_transactions.containsKey(offer_key)) {

            // User already added items from this offer to the cart, update quantity
            Transaction existing = curr_shop_transactions.get(offer_key);
            existing.setQuantity(existing.getQuantity() + 1);

        } else {

            // The shop had transactions before, but from this Sale this is the first
            curr_shop_transactions.put(offer_key, curr_tran);

        }

        return curr_shop_transactions;
    }

    public Map<String, Transaction> getShopTransactions(String shop_uid) {
        return instance.trans_by_seller.get(shop_uid);
    }

    public void removeTransaction(String offer_id, String shop_uid) {
        Map<String, Transaction> shop_transactions = instance.trans_by_seller.get(shop_uid);
        size = size - shop_transactions.get(offer_id).getQuantity();
        shop_transactions.remove(offer_id);
    }


    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Map<String, Map<String, Transaction>> getTrans_by_seller() {
        return trans_by_seller;
    }

    public void setTrans_by_seller(Map<String, Map<String, Transaction>> trans_by_seller) {
        this.trans_by_seller = trans_by_seller;
    }


    public Map<String, Map<String, Deal>> getDeals_by_seller() {
        return deals_by_seller;
    }

    public void setDeals_by_seller(Map<String, Map<String, Deal>> deals_by_seller) {
        this.deals_by_seller = deals_by_seller;
    }
}
