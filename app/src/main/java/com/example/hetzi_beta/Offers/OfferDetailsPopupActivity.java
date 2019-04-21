package com.example.hetzi_beta.Offers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.hetzi_beta.Utils.HTZ_ADD_OFFER;
import static com.example.hetzi_beta.Utils.HTZ_PHOTO_PICKER;

/*
 * OfferDetailsPopupActivity -
 * This is the Pop-up activity that appears when the retailer clicks '+' (FAB).
 * The form contains textual/spinner details, and photo upload.
 *
 * */

public class OfferDetailsPopupActivity extends AppCompatActivity {
    // Product details
    public Uri photo_firebase_uri;
    public boolean photo_done;

    // UI Items
    private ImageView mPhotoPickerButton;
    private Button mPublishButton;
    private SeekBar mDiscountSeekbar;
    private Spinner mTimeSpinner;
    private EditText mName;
    private EditText mQuantity;
    private EditText mPrice;
    private ProgressBar mUploadProgress;
    private TextView mDiscountPercent;

    // Firebase instance variables (Storage and Realtime Database)
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer_popup);
        initViewsAndReferences();
        initSpinners();
        onClickPickPhoto();
        onClickPublish();

        mDiscountSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 10;
                progress = progress * 10;
                mDiscountSeekbar.setProgress(progress);
                mDiscountPercent.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void disablePublishButton() {
        mPublishButton.setBackground(getResources().getDrawable(R.drawable.shape_disabled_button));
        mPublishButton.setEnabled(false);
    }

    private void enablePublishButton() {
        mPublishButton.setBackground(getResources().getDrawable(R.drawable.shape_enabled_button));
        mPublishButton.setEnabled(true);
    }

    private void initSpinners() {
        ArrayAdapter<CharSequence> time_adapter =
                ArrayAdapter.createFromResource(this,
                R.array.times_array, android.R.layout.simple_spinner_dropdown_item);
        mTimeSpinner.setAdapter(time_adapter);
    }

    private void initViewsAndReferences() {
        // Firebase variables initialization
        mFirebaseDatabase           = FirebaseDatabase.getInstance();
        mFirebaseStorage            = FirebaseStorage.getInstance();
        mPhotosStorageReference     = mFirebaseStorage.getReference().child("product_photos");
        mOffersDatabaseReference    = mFirebaseDatabase.getReference().child("offers");

        // Attach members to xml elements
        mPhotoPickerButton  = findViewById(R.id.product_photo_picker);
        mPublishButton      = findViewById(R.id.publish_button);
        mDiscountSeekbar    = findViewById(R.id.discount_SeekBar);
        mTimeSpinner        = findViewById(R.id.time_Spinner);
        mName               = findViewById(R.id.product_name_EditText);
        mQuantity           = findViewById(R.id.quantity_EditText);
        mPrice              = findViewById(R.id.price_EditText);
        mUploadProgress     = findViewById(R.id.determinateBar);
        mDiscountPercent    = findViewById(R.id.percent_number_TextView);

        // Listen to text changes
        mName       .addTextChangedListener(watcher);
        mQuantity   .addTextChangedListener(watcher);
        mPrice      .addTextChangedListener(watcher);

        photo_done = false;
        photo_firebase_uri = null;
    }

    private void onClickPickPhoto() {
        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), HTZ_PHOTO_PICKER);
            }
        });
    }

    private void onClickPublish() {
        // Retailer finished editing offer, and submiting the form
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create offer object from user input
                Offer n_offer = new Offer   (
                                mName.getText().toString(),
                                photo_firebase_uri.toString(),  // photourl is updated on 'UploadImageToStorage'
                                Integer.parseInt(mQuantity.getText().toString()),
                                Float.parseFloat(mPrice.getText().toString()),
                                mDiscountSeekbar.getProgress(),
                                Utils.timeFromStringToSecsAsInt(mTimeSpinner.getSelectedItem().toString())
                                            );

                // Push to Firebase Realtime DataBase
                mOffersDatabaseReference.push().setValue(n_offer);

                // Return offer to EditOffersActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("offer", n_offer);
                setResult(HTZ_ADD_OFFER, resultIntent);
                finish();
            }
        });
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            if (    mName.getText().toString().length() == 0 && mPrice.getText().toString().length() == 0 &&
                    mQuantity.getText().toString().length() == 0 && !photo_done ) {
                disablePublishButton();
            } else if( photo_firebase_uri != null ) {
                enablePublishButton();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HTZ_PHOTO_PICKER && resultCode == RESULT_OK) {
            // ~~~~~~ (1) Update the view immediately  ~~~~~~ //
            Uri selectedImageUri = data.getData();
            Utils.updateViewImage(OfferDetailsPopupActivity.this, selectedImageUri, mPhotoPickerButton);

            // ~~~~~~ (2) Delete previous photo if existed  ~~~~~~ //
            if(photo_firebase_uri != null) {
                mFirebaseStorage.getReferenceFromUrl((photo_firebase_uri).toString()).delete();
            }

            // ~~~~~~ (3) Upload Image to Firebase Storage  ~~~~~~ //
            // TODO : (?) move actual photo upload to onClickPublish (?)
            StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            mUploadProgress.setProgress(0);
            UploadTask uploadTask = photoRef.putFile(selectedImageUri);
            uploadTask
            .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photo_firebase_uri = uri;
                        photo_done = true;
                        enablePublishButton();
                    }
                });
            }
            })
            .addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    mUploadProgress.setProgress(progress.intValue());
                }
            });
        }
    }

}


