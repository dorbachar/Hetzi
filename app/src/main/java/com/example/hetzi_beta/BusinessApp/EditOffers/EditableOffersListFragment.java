package com.example.hetzi_beta.BusinessApp.EditOffers;

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
import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.Offers.OnClickButtonListener;
import com.example.hetzi_beta.R;
import static com.example.hetzi_beta.Utils.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditableOffersListFragment extends Fragment implements OnClickButtonListener {
    public TextView                             mNoOffersTextView;
    public TextView                             mAddOffersTextView;
    private FloatingActionButton                fab;

    // Firebase related
    public FirebaseDatabase                     mFirebaseDatabase;
    private DatabaseReference                   mOffersDatabaseReference;

    // Glide related
    public ViewPreloadSizeProvider              mPreloadSizeProvider;
    public ListPreloader.PreloadModelProvider   mPreloadModelProvider;

    // RecyclerView Related
    public ArrayList<Offer>                     offers_list;
    public EditOfferAdapter                     adapter;
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

        rvOffers            = root_view.findViewById(R.id.offers_RecyclerView);
        mNoOffersTextView   = root_view.findViewById(R.id.no_sales);
        mAddOffersTextView  = root_view.findViewById(R.id.add_offers);
        fab                 = root_view.findViewById(R.id.fab);

        offers_list         = new ArrayList<>();
        mFirebaseDatabase   = FirebaseDatabase.getInstance();

        onClickFAB();
        setupAdapter();
        setupGlidePreloader();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadOffersFromDb(user);
        } else {
            Toast.makeText(getActivity(),"Business must be signed in!" +
                    " Please block this interaction for unsigned users",Toast.LENGTH_LONG).show();
        }

        return root_view;
    }

    private void onClickFAB() {
        // Setting up the FAB so it leads to the Product Details Pop-up
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OfferDetailsPopupActivity.class);
                intent.putExtra("new", true);
                startActivityForResult(intent, HTZ_ADD_OFFER);
            }
        });
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
        adapter = new EditOfferAdapter(offers_list, this);
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

    /*

    * We get here in 2 situations: after creating a new Offer or after editing an existing one. In
    * both cases, we can just check for duplicates (which will exist if it was an edit) and then
    * add the new/modified offer to the list.
    *
    * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == HTZ_ADD_OFFER ) {
            mNoOffersTextView.setVisibility(View.GONE);
            mAddOffersTextView.setVisibility(View.GONE);
            Offer created_offer = data.getParcelableExtra("offer");

            if ( created_offer != null ) {
                removeExistingOfferIfExists(created_offer);
                offers_list.add(created_offer);
            } else {
                // created_offer == null indicated DELETE was clicked
                String delete_key = data.getStringExtra("delete_key");
                removeExistingOfferByKey(delete_key);
                if(offers_list.size() == 0) {
                    mNoOffersTextView.setVisibility(View.VISIBLE);
                    mAddOffersTextView.setVisibility(View.VISIBLE);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    /*
    * removeExistingOfferIfExists -
    *
    * Simply loops through the list of Offers, and removes an Offer with target_offer's Firebase Key.
    * Used to re-add the edited Offer after editing.
    *
    * */
    public void removeExistingOfferIfExists(Offer target_offer) {
        for (Offer curr_offer : offers_list) {
            if (curr_offer.getFbKey().equals(target_offer.getFbKey())) {
                offers_list.remove(curr_offer);
                break;
            }
        }
    }

    public void removeExistingOfferByKey(String key) {
        for (Offer curr_offer : offers_list) {
            if (curr_offer.getFbKey().equals(key)) {
                offers_list.remove(curr_offer);
                break;
            }
        }
    }

    /*
    * onClickButton -
    *
    * Implementation of OnClickButtonListener.onClickButton, so that when calling
    * startActivityForResult, the onActivityResult method will be called as I want.
    *
    * */
    @Override
    public void onClickButton(View v, int position, ArrayList<Offer> offers) {
        Offer from_item = offers.get(position);

        Intent intent = new Intent(getActivity(), OfferDetailsPopupActivity.class);
        intent.putExtra("new", false);
        intent.putExtra("offer_fromRecycler", from_item);
        startActivityForResult(intent, HTZ_ADD_OFFER);
    }
}
