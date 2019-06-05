package com.example.hetzi_beta.PastDeals;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hetzi_beta.CustomerApp.ShopsGrid.ShopsGridAdapter;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Transactions.Payment;

import java.util.ArrayList;

public class PaymentAdapter extends android.support.v7.widget.RecyclerView.Adapter<PaymentAdapter.PaymentViewholder> {
    public ArrayList<Payment> payments_list;
    public View.OnClickListener mClickListener;

    public PaymentAdapter(ArrayList<Payment> payments_list) {
        this.payments_list = payments_list;
    }

    @NonNull
    @Override
    public PaymentViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Called when a new ViewHolder instance is created by RecyclerView
        int             layout_id    = R.layout.item_payment;
        LayoutInflater inflater     = LayoutInflater.from(viewGroup.getContext());
        View            view         = inflater.inflate(layout_id, viewGroup, false);

        RecyclerView.ViewHolder holder = new PaymentAdapter.PaymentViewholder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onClick(view);
            }
        });

        return new PaymentViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewholder paymentViewholder, int i) {
        Payment curr_payment = payments_list.get(i);

        paymentViewholder.mTitle.setText(curr_payment.getTitle());
        paymentViewholder.mDate.setText(curr_payment.getDate());
        paymentViewholder.mSum.setText(curr_payment.getSum().toString());
    }

    @Override
    public int getItemCount() {
        return payments_list.size();
    }

    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }




    class PaymentViewholder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDate;
        private TextView mSum;

        public PaymentViewholder(@NonNull View itemView) {
            super(itemView);

            mTitle  = itemView.findViewById(R.id.title_TextView);
            mDate   = itemView.findViewById(R.id.date_TextView);
            mSum = itemView.findViewById(R.id.sum_TextView);
        }
    }
}
