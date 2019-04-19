package com.example.hetzi_beta.Offers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.hetzi_beta.Utils.*;

/*
 * OfferDetailsPopupActivity -
 * This is the Pop-up activity that appears when the retailer clicks '+' (FAB).
 * The form contains textual/spinner details, and photo upload.
 *
 * */

public class OfferDetailsPopupActivity extends AppCompatActivity {
    // Product details
    public Uri p_photo_url;

    // UI Items
    private ImageButton mPhotoPickerButton;
    private Button mPublishButton;
    private Spinner mDiscountSpinner;
    private Spinner mTimeSpinner;
    private EditText mName;
    private EditText mQuantity;
    private EditText mPrice;

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
    }

    private void initSpinners() {
        // Init spinners (discount and time)
        ArrayAdapter<CharSequence> discount_adapter =
                ArrayAdapter.createFromResource(this,
                R.array.discounts_array, android.R.layout.simple_spinner_dropdown_item);
        mDiscountSpinner.setAdapter(discount_adapter);

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
        mDiscountSpinner    = findViewById(R.id.discount_Spinner);
        mTimeSpinner        = findViewById(R.id.time_Spinner);
        mName               = findViewById(R.id.product_name_EditText);
        mQuantity           = findViewById(R.id.quantity_EditText);
        mPrice              = findViewById(R.id.price_EditText);
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
                if (p_photo_url == null) {
                    // TODO : when uploading the photo is moved to here (clicking publish) - the toast is no longer needed.
                    // In any case the displayed photo is local
                    Toast.makeText(OfferDetailsPopupActivity.this, R.string.item_must_have_photo, Toast.LENGTH_SHORT).show();
                } else {

                    // Create offer object from user input
                    Offer n_offer = new Offer   (
                                    mName.getText().toString(),
                                    p_photo_url.toString(),  // photourl is updated on 'UploadImageToStorage'
                                    Integer.parseInt(mQuantity.getText().toString()),
                                    Float.parseFloat(mPrice.getText().toString()),
                                    Integer.parseInt(mDiscountSpinner.getSelectedItem().toString()),
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
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HTZ_PHOTO_PICKER && resultCode == RESULT_OK) {
            // ~~~~~~ Upload Product Image to Firebase Storage  ~~~~~~ //
            Uri selectedImageUri = data.getData();
            Utils.updateViewImage(OfferDetailsPopupActivity.this, selectedImageUri, mPhotoPickerButton);

            mFirebaseStorage = FirebaseStorage.getInstance();
            mPhotosStorageReference = mFirebaseStorage.getReference().child("product_photos");

            StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            // TODO : move actual photo upload to onClickPublish()
            UploadTask uploadTask = photoRef.putFile(selectedImageUri);
            uploadTask
            .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        p_photo_url = uri;
                    }
                });
            }
            });
        }
    }

}


