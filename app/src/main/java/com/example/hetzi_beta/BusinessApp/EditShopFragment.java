package com.example.hetzi_beta.BusinessApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Shops.Shop;
import com.example.hetzi_beta.Utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.app.Activity.RESULT_OK;
import static com.example.hetzi_beta.Utils.Utils.HTZ_COVER_PHOTO_ULPOAD;
import static com.example.hetzi_beta.Utils.Utils.HTZ_LOGO_ULPOAD;

public class EditShopFragment extends Fragment {
    // This important member holds the currently displayed shop. It's loaded from the DB if the user
    // previously created a shop page, and pushed to DB in the end if the user wants to save changes.
    Shop shop_on_display;

    // Helper members to sync photos to/from Firebase
    Uri photo_firebase_uri;
    Uri logo_uri;
    Uri cover_uri;
    boolean logo_photo_ready;
    boolean cover_photo_ready;
    boolean logo_changed;
    boolean cover_photo_changed;

    // Views in the fragment
    private ImageView           mShopCoverPhotoImageView;
    private CircularImageView   mLogoCircularImageView;
    private TextView            mShopName;
    private TextView            mPhysicalAddress;
    private EditText            mWebsite;
    private EditText            mPhoneNumber;
    private EditText            mFacebookLink;
    private EditText            mInstagramLink;
    private FancyButton         mSaveChanges;
    private ProgressBar         mLogoProgress;
    private ProgressBar         mCoverProgress;

    // Firebase instance variables (Storage and Realtime Database)
    private FirebaseDatabase    mFirebaseDatabase;
    private DatabaseReference   mShopsDatabaseReference;
    private FirebaseStorage     mFirebaseStorage;
    private StorageReference    mShopPhotosStorageReference;

    public EditShopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This callback will only be called when MyFragment is at least Started.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view                  = inflater.inflate(R.layout.fragment_edit_shop,
                                                                    container, false);
        initViews(root_view);

        // Progress circles will be shown only while the photo is uploaded to the server
        mLogoProgress           .setVisibility(View.GONE);
        mCoverProgress          .setVisibility(View.GONE);
        logo_photo_ready        = false;
        cover_photo_ready       = false;
        logo_changed            = false;
        cover_photo_changed     = false;

        attachListenersToEditTexts();
        disableSaveChangesButton();

        // Init Firebase members
        mFirebaseStorage                = FirebaseStorage.getInstance();
        mFirebaseDatabase               = FirebaseDatabase.getInstance();
        mShopPhotosStorageReference     = mFirebaseStorage.getReference().child("shop_photos");
        mShopsDatabaseReference         = mFirebaseDatabase.getReference().child("shops");

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            loadExistingShopFromDbToViews(user);
        else {
            Toast.makeText(getActivity(),"Business must be signed in!" +
                    " Please block this interaction for unsigned users",Toast.LENGTH_LONG).show();
        }

        onClickImageView(mLogoCircularImageView, HTZ_LOGO_ULPOAD);
        onClickImageView(mShopCoverPhotoImageView, HTZ_COVER_PHOTO_ULPOAD);
        onClickSaveChanges();

        return root_view;
    }

    private void attachListenersToEditTexts() {
        // Add listeners to EditTexts
        mShopName.addTextChangedListener(watcher);
        mWebsite.addTextChangedListener(watcher);
        mPhoneNumber.addTextChangedListener(watcher);
        mFacebookLink.addTextChangedListener(watcher);
        mInstagramLink.addTextChangedListener(watcher);
    }

    private void initViews(View root_view) {
        mShopCoverPhotoImageView        = root_view.findViewById(R.id.shop_cover_photo_ImageView);
        mLogoCircularImageView          = root_view.findViewById(R.id.logo_CircularImageView);
        mShopName                       = root_view.findViewById(R.id.shop_name_EditText);
        mPhysicalAddress                = root_view.findViewById(R.id.shop_address_TextView);
        mWebsite                        = root_view.findViewById(R.id.shop_site_EditText);
        mPhoneNumber                    = root_view.findViewById(R.id.shop_phonenumber_EditText);
        mSaveChanges                    = root_view.findViewById(R.id.save_changes_FancyButton);
        mLogoProgress                   = root_view.findViewById(R.id.determinateCircleLogo);
        mCoverProgress                  = root_view.findViewById(R.id.determinateCircleCover);
        mFacebookLink                   = root_view.findViewById(R.id.facebook_EditText);
        mInstagramLink                  = root_view.findViewById(R.id.insta_EditText);
    }

    private void onClickImageView(ImageView mDestImageView, final int requestCode) {
        mDestImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (Utils.isReadStoragePermissionGranted(getActivity(), requestCode)) {
                        startActivityGallery(requestCode);
                    }
                }
        });
    }

    public void startActivityGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), requestCode);
    }

    private void onClickSaveChanges() {
        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushShopToDbFromViews();
                disableSaveChangesButton();
            }
        });
    }

    private void loadExistingShopFromDbToViews(final FirebaseUser user) {
        // Update photo upload's path to specific user folder (Storage and RealtimeDB)
        mShopPhotosStorageReference     = mFirebaseStorage  .getReference().child("shop_photos/" + user.getUid());
        mShopsDatabaseReference         = mFirebaseDatabase .getReference().child("shops/" + user.getUid());
        shop_on_display                 = new Shop();

        mShopsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ( userExists(dataSnapshot.getKey(), user.getUid())) {
                    // Shop exists in DB. Download it to shop_on_display local variable and modify if needed

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // STUPID WORKAROUND the face that firebase creates a unique id that I don't know how to get
                        // and makes it the child of the uid named node.
                        // Note that the loop breaks after 1 time...

                        shop_on_display = postSnapshot.getValue(Shop.class); // Pull from DB
                        updateViewsFromShopMember();

                        // If any uri was downloaded, it is ready to be uploaded again if needed,
                        // than no reason for it to block SaveChanges button
                        if(shop_on_display.getLogoUri() != null) {
                            logo_uri = Uri.parse(shop_on_display.getLogoUri());
                            logo_photo_ready = true;
                            logo_changed = true;
                        }
                        if(shop_on_display.getCoverPhotoUri() != null) {
                            cover_uri = Uri.parse(shop_on_display.getCoverPhotoUri());
                            cover_photo_ready = true;
                            cover_photo_changed = true;
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateViewsFromShopMember() {
        // Update the view as per the info from DB
        String shop_name = shop_on_display.getShopName();

        mShopName           .setText(shop_name);
        mWebsite            .setText(shop_on_display.getWebsite());
        mPhoneNumber        .setText(shop_on_display.getPhone());
        mFacebookLink       .setText(shop_on_display.getFacebookUri());
        mInstagramLink      .setText(shop_on_display.getInstagramUri());

        if (Utils.SHOP_ADDRESS.containsKey(shop_name)) {
            mPhysicalAddress.setText(Utils.SHOP_ADDRESS.get(shop_name).getAddress());
        } else {
            mPhysicalAddress.setText("אין כתובת עדכנית במערכת");
        }


        Utils.updateViewImage(getActivity(), Uri.parse(shop_on_display.getLogoUri()), mLogoCircularImageView);
        Utils.updateViewImage(getActivity(), Uri.parse(shop_on_display.getCoverPhotoUri()), mShopCoverPhotoImageView);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK ) {
            // ~~~~~~ (1) Update the view immediately  ~~~~~~ //
            Uri selectedImageUri = data.getData();
            switch(requestCode) {
                case HTZ_LOGO_ULPOAD:
                    Utils.updateViewImage(getActivity(), selectedImageUri, mLogoCircularImageView);
                    mLogoProgress.setVisibility(View.VISIBLE);
                    logo_photo_ready = false;
                    break;
                case HTZ_COVER_PHOTO_ULPOAD:
                    Utils.updateViewImage(getActivity(), selectedImageUri, mShopCoverPhotoImageView);
                    mCoverProgress.setVisibility(View.VISIBLE);
                    cover_photo_ready = false;
                    break;
            }

            // ~~~~~~ (2) Upload Image to Firebase Storage  ~~~~~~ //
            StorageReference photoRef = mShopPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(selectedImageUri);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            photo_firebase_uri = uri;
                                            switch(requestCode) {
                                                case HTZ_LOGO_ULPOAD:
                                                    logo_uri = photo_firebase_uri;
                                                    mLogoProgress.setVisibility(View.GONE);
                                                    logo_photo_ready = true;
                                                    break;
                                                case HTZ_COVER_PHOTO_ULPOAD:
                                                    cover_uri = photo_firebase_uri;
                                                    mCoverProgress.setVisibility(View.GONE);
                                                    cover_photo_ready = true;
                                                    break;
                                            }

                                            checkEnableSaveButton();
                                        }
                                    });
                        }
                    });
        }
    }

    /*
    * showAskDialog -
    * Pops a dialog that prompts the user to save changes (or not).
    * Should be called when the back button is pressed (from CustomerHomeActivity, using onBackPressed()).
    *
    * */
    public void showAskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.drawable.shape_dialog_round_corners);
        builder.setMessage("לשמור שינויים בעמוד העסק?").setCancelable(true)
                .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cleanSession();
                        dialog.cancel();
                        getActivity().finish();
                    }
                })
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        pushShopToDbFromViews();
                        dialog.cancel();
                        getActivity().finish();
                    }
                });
        builder.show();
    }

    private boolean userExists(@NonNull String key_from_db, String firebase_uid) {
        return key_from_db.equals(firebase_uid);
    }

    /*
    * cleanSession -
    * If the logo or the cover photo were changed during this session, they were uploaded to the
    * Firebase Storage, and for this, needs to be deleted (a lot of wasted storage space...).
    *
    * */
    private void cleanSession() {
        if(logo_changed) {
            mShopPhotosStorageReference.child(logo_uri.getLastPathSegment()).delete();
        }
        if(cover_photo_changed){
            mShopPhotosStorageReference.child(cover_uri.getLastPathSegment()).delete();
        }
    }

    /*
    * pushShopToDbFromViews -
    * Used when user saves changes, in order to push the shop details as an entry to the DB.
    *
    * */
    private void pushShopToDbFromViews() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // If this is not the first time saving, we need to delete the previous shop page first
        // TODO : like in Offers, update the entry and don't delete it ....
        if (userExists(mShopsDatabaseReference.getKey(), user.getUid())) {
            mShopsDatabaseReference.getRef().removeValue();
        }


        // TODO : replace to shop_on_display = new Shop( all the params instead of set function );
        // Now properly set shop_on_display to contain the current info
        String shop_name = mShopName.getText().toString();
        shop_on_display.setShopName(shop_name);
        shop_on_display.setCoverPhotoUri(cover_uri.toString());
        shop_on_display.setLogoUri(logo_uri.toString());
        shop_on_display.setPhone(mPhoneNumber.getText().toString());
        shop_on_display.setWebsite(mWebsite.getText().toString());
        shop_on_display.setFacebookUri(mFacebookLink.getText().toString());
        shop_on_display.setInstagramUri(mInstagramLink.getText().toString());
        shop_on_display.setUid(user.getUid());

        if (Utils.SHOP_ADDRESS.containsKey(shop_name)) {
            shop_on_display.setLat(Utils.SHOP_ADDRESS.get(shop_name).getLatitude());
            shop_on_display.setLon(Utils.SHOP_ADDRESS.get(shop_name).getLongtitude());
        } else {
            // TODO : handle this case : when addresses are not from a constant map
        }

        // PUSH TO FIREBASE REALTIME DB
        mShopsDatabaseReference.push().setValue(shop_on_display, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError,
                                   DatabaseReference databaseReference) {
                // After the push action, I can get the fbKey and save it
                String key = databaseReference.getKey();
                Map<String, Object> userUpdates = new HashMap<>();

                userUpdates.put("fbKey", key);

                mShopsDatabaseReference.child(key).updateChildren(userUpdates);
            }
        });
    }

    /*
    * This piece of code is a generic one, just to set up a text watcher. The watcher is used so
    * that when an EditText's text was changed by the user, i'll be able to make the mSaveChanges
    * button enabled.
    *
    * */
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            checkEnableSaveButton();
        }
    };

    /*
    * These 4 methods are simple and straight-forward, but together they put the logic behind
    * enabling and disabling the mSaveChanges button.
    *
    * */
    private void        checkEnableSaveButton() {
        if ( !shouldDisableSaveChanges() ) {
            enableSaveChangesButton();
        } else {
            disableSaveChangesButton();
        }
    }
    private boolean     shouldDisableSaveChanges() {
        return emptyEditTextExists() || !cover_photo_ready || !logo_photo_ready;
    }
    private void        disableSaveChangesButton() {
        mSaveChanges.setBackground(getResources().getDrawable(R.drawable.shape_disabled_button));
        mSaveChanges.setEnabled(false);
    }
    private void        enableSaveChangesButton() {
        mSaveChanges.setBackground(getResources().getDrawable(R.drawable.shape_enabled_button));
        mSaveChanges.setEnabled(true);
    }
    private boolean     emptyEditTextExists() {
        // mPhysicalAddress.getText().toString().length() == 0 || mShopName.getText().toString().length() == 0
       return mWebsite.getText().toString().length() == 0 || mPhoneNumber.getText().toString().length() == 0;

    }
}
