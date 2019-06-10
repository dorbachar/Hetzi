package com.example.hetzi_beta.PastDeals;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Transactions.BusinessTransaction;
import com.example.hetzi_beta.Transactions.CustomerTransaction;
import com.example.hetzi_beta.Transactions.Payment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hetzi_beta.Utils.Utils.HTZ_BUSINESS;
import static com.example.hetzi_beta.Utils.Utils.HTZ_CUSTOMER;

public class PastDealsFragment extends Fragment {
    private int side;
    private SwipeRefreshLayout  mSwipeContainer;
    // RecyclerView Related
    public ArrayList<Payment>   payments_list = new ArrayList<>();
    public PaymentAdapter       adapter;
    public RecyclerView         rvPayments;

    // Firebase related
    FirebaseDatabase    mFirebaseDatabase;
    DatabaseReference   mTransDatabaseReference;

    public PastDealsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_past_deals, container, false);

        side                    = getArguments().getInt("side");
        rvPayments              = root_view.findViewById(R.id.payments_RecyclerView);

        setupAdapter();
        buildPaymentsList();
        setupSwipeRefresh(root_view);

        return root_view;
    }


    private void setupSwipeRefresh(View root_view) {
        mSwipeContainer              = root_view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                buildPaymentsList();
            }
        });
        mSwipeContainer.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimaryLight);
    }

    private void buildPaymentsList() {
        FirebaseUser user          = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase          = FirebaseDatabase.getInstance();

        switch(side) {
            case HTZ_BUSINESS:
                mTransDatabaseReference   = mFirebaseDatabase.getReference().child("transactions/busi/" + user.getUid() );
                break;
            case HTZ_CUSTOMER:
                mTransDatabaseReference   = mFirebaseDatabase.getReference().child("transactions/cust/" + user.getUid() );
                break;
        }

        mTransDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot curr_payment : dataSnapshot.getChildren()) {
                    Payment p = new Payment();
                    for(DataSnapshot curr_transaction : curr_payment.getChildren()) {
                        switch(side) {
                            case HTZ_BUSINESS:
                                p.addToTransactions(curr_transaction.getValue(BusinessTransaction.class));
                                break;
                            case HTZ_CUSTOMER:
                                p.addToTransactions(curr_transaction.getValue(CustomerTransaction.class));
                                break;
                        }
                    }
                    p.updateDetailsAfterPopulation(side);
                    payments_list.add(p);
                }
                adapter.notifyDataSetChanged();
                mSwipeContainer.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setupAdapter() {
        // Set adapter for RecyclerView
        adapter = new PaymentAdapter(payments_list);
        rvPayments.setAdapter(adapter);
        rvPayments.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = rvPayments.indexOfChild(v);
                Payment this_payment = payments_list.get(pos);
                showPaymentTransactionsPopup(this_payment, side);
            }
        });
    }

    private void showPaymentTransactionsPopup(Payment this_payment, int side) {
        Intent intent = new Intent(getActivity(), ViewPaymentPopupActivity.class);
        intent.putExtra("side", side);
        intent.putExtra("payment_object", this_payment);
        startActivity(intent);
    }
}
