package com.example.hetzi_beta.Offers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.example.hetzi_beta.MainActivity;
import com.example.hetzi_beta.R;
import static com.example.hetzi_beta.Utils.*;

import com.example.hetzi_beta.Shops.Shop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditableOffersListFragment extends Fragment {
    public TextView                             mNoOffersTextView;
    public TextView                             mAddOffersTextView;

    // Firebase related
    public FirebaseDatabase                     mFirebaseDatabase;
    private DatabaseReference                   mOffersDatabaseReference;

    // Glide related
    public ViewPreloadSizeProvider              mPreloadSizeProvider;
    public ListPreloader.PreloadModelProvider   mPreloadModelProvider;

    // RecyclerView Related
    public ArrayList<Offer>                     offers_list;
    public OfferAdapter                         adapter;
    public RecyclerView                         rvOffers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view                  = inflater.inflate(R.layout.fragment_editable_offers_list,
                                            container, false);

//        ((EditOffersActivity)getActivity()).showLoading();

        rvOffers            = root_view.findViewById(R.id.offers_RecyclerView);
        mNoOffersTextView   = root_view.findViewById(R.id.no_sales);
        mAddOffersTextView  = root_view.findViewById(R.id.add_offers);
        mFirebaseDatabase   = FirebaseDatabase.getInstance();
        offers_list         = new ArrayList<>();

        setupAdapter();
        setupGlidePreloader();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadOffersFromDb(user);
        } else {
            Toast.makeText(getActivity(),"Business must be signed in!" +
                    " Please block this interaction for unsigned users",Toast.LENGTH_LONG).show();
        }

//        ((EditOffersActivity)getActivity()).hideLoading();

        return root_view;
    }

    private void setupGlidePreloader() {
        // Glide preloader
        mPreloadSizeProvider = new ViewPreloadSizeProvider();
        mPreloadModelProvider = new MyPreloadModelProvider(offers_list, getActivity());
        RecyclerViewPreloader<String> preloader =
                new RecyclerViewPreloader<>(Glide.with(this), mPreloadModelProvider, mPreloadSizeProvider, 10);
        rvOffers.addOnScrollListener(preloader);
    }

    private void setupAdapter() {
        // Set adapter for RecyclerView
        adapter = new OfferAdapter(offers_list);
        rvOffers.setAdapter(adapter);
        rvOffers.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void loadOffersFromDb(final FirebaseUser user) {
        mOffersDatabaseReference = mFirebaseDatabase.getReference().child("offers/" + user.getUid());

        mOffersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot curr_offer_snapshot : dataSnapshot.getChildren()) {
                        Offer curr_offer = curr_offer_snapshot.getValue(Offer.class);
                        offers_list.add(curr_offer);
                    }
                    adapter.notifyDataSetChanged();
                    mNoOffersTextView.setVisibility(View.GONE);
                    mAddOffersTextView.setVisibility(View.GONE);
                }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
