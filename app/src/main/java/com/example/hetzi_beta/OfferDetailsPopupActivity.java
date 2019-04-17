package com.example.hetzi_beta;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

import static com.example.hetzi_beta.EditOffersActivity.HTZ_ADD_OFFER;

/*
* OfferDetailsPopupActivity -
* This is the Pop-up activity that appears when the retailer clicks '+' (FAB).
* The form contains textual/spinner details, and photo upload that happens in an asynchronous manner,
* using the 'UploadImageToStorage' class (overrides Asynctask).
*
* */

public class OfferDetailsPopupActivity extends AppCompatActivity {
    // Constants
    public static final int HTZ_PHOTO_PICKER =  1;

    // Product details that needs to be saved for a while
    public Uri  p_photo_url;
    public Integer upload_progress;

    // Variables for UI Items
    private ImageButton mPhotoPickerButton;
    private Button mPublishButton;
    private Spinner mDiscountSpinner;
    private Spinner mTimeSpinner;
    private EditText mName;
    private EditText mQuantity;
    private EditText mPrice;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_offer_details);

        // Firebase variables initialization
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child("product_photos");
        mOffersDatabaseReference = mFirebaseDatabase.getReference().child("offers");

        // Attach members to xml elements
        mPhotoPickerButton = findViewById(R.id.product_photo_picker);
        mPublishButton = findViewById(R.id.publish_button);
        mDiscountSpinner = findViewById(R.id.discount_Spinner);
        mTimeSpinner = findViewById(R.id.time_Spinner);
        mName = findViewById(R.id.product_name_EditText);
        mQuantity = findViewById(R.id.quantity_EditText);
        mPrice = findViewById(R.id.price_EditText);

        // Init spinners (discount and time)
        ArrayAdapter<CharSequence> discount_adapter = ArrayAdapter.createFromResource(this,
                R.array.discounts_array, android.R.layout.simple_spinner_dropdown_item);
        mDiscountSpinner.setAdapter(discount_adapter);

        ArrayAdapter<CharSequence> time_adapter = ArrayAdapter.createFromResource(this,
                R.array.times_array, android.R.layout.simple_spinner_dropdown_item);
        mTimeSpinner.setAdapter(time_adapter);

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

        // Retailer finished editing offer, and submiting the form
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p_photo_url == null) {
                    Toast.makeText(OfferDetailsPopupActivity.this, R.string.item_must_have_photo, Toast.LENGTH_SHORT).show();
                } else {

                    // Create offer from user input
                    Offer n_offer = new Offer   (
                            mName.getText().toString(),
                            p_photo_url.toString(),  // photourl is updated on 'UploadImageToStorage'
                            Integer.parseInt(mQuantity.getText().toString()),
                            Float.parseFloat(mPrice.getText().toString()),
                            Integer.parseInt(mDiscountSpinner.getSelectedItem().toString()),
                            timeFromStringToSecsAsInt(mTimeSpinner.getSelectedItem().toString())
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

    public int timeFromStringToSecsAsInt(String input) {
        // TODO : TIME OVERHAUL
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HTZ_PHOTO_PICKER && resultCode == RESULT_OK) {

            // ~~~~~~ Upload Product Image to Firebase Storage  ~~~~~~ //
            Uri selectedImageUri = data.getData();
            // Initializing a UploadImageToStorageTask and letting it handle the photo upload in the background
            UploadImageToStorageTask up_task = new UploadImageToStorageTask();
            up_task.execute(selectedImageUri);
        }
    }


    // ----------------------------- UploadImageToStorageTask ----------------------------- //

    private class UploadImageToStorageTask extends AsyncTask<Uri, Integer, Uri> {
        private FirebaseStorage mFirebaseStorage;
        private StorageReference mPhotosStorageReference;

        @Override
        protected Uri doInBackground(Uri... uris) {
            // for now only one image
            Uri img_local_uri = uris[0];
            upload_progress = 0;

            mFirebaseStorage = FirebaseStorage.getInstance();
            mPhotosStorageReference = mFirebaseStorage.getReference().child("product_photos");

            StorageReference photoRef = mPhotosStorageReference.child(img_local_uri.getLastPathSegment());
            photoRef.putFile(img_local_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    p_photo_url = uri;
                                }
                            });
                        }
                    });

            return p_photo_url;
        }

        @Override
        public void onPostExecute(Uri result) {
            // Glide handles the image replacement in the given ImageButton
            int dize = 0;
            Glide.with(OfferDetailsPopupActivity.this)
                    .load(result)
                    .centerCrop()
                    .into(mPhotoPickerButton);

        }

        @Override
        public void onProgressUpdate(Integer... progress) {
            upload_progress = progress[0];
        }
    }

}


