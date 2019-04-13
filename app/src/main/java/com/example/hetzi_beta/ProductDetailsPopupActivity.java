package com.example.hetzi_beta;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

public class ProductDetailsPopupActivity extends AppCompatActivity {
    // Product details that needs to be saved for a while
    private String  p_photo_url;

    // Variables for UI Items
    private ImageButton mPhotoPickerButton;
    private Button mPublishButton;
    private Spinner mDiscountSpinner;
    private Spinner mTimeSpinner;
    private EditText mName;
    private EditText mQuantity;
    private EditText mPrice;

    // Constants
    private static final int HTZ_PHOTO_PICKER =  1;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product_details);

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

        //
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create offer from user input, and push to DB
                Offer n_offer = new Offer   (
                                            mName.getText().toString(),
                                            p_photo_url,
                                            Integer.parseInt(mQuantity.getText().toString()),
                                            Integer.parseInt(mPrice.getText().toString()),
                                            Integer.parseInt(mDiscountSpinner.getSelectedItem().toString()),
                                            timeFromStringToSecsAsInt(mTimeSpinner.getSelectedItem().toString())
                                            );
                mOffersDatabaseReference.push().setValue(n_offer);
                finish();
            }
        });
    }

    public int timeFromStringToSecsAsInt(String input) {
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HTZ_PHOTO_PICKER && resultCode == RESULT_OK) {

            // ~~~~~~ Upload Product Image to Firebase Storage  ~~~~~~ //
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        Boolean done = false;
                        Toast.makeText(ProductDetailsPopupActivity.this, "Uplodaing image...", Toast.LENGTH_SHORT).show();
                        while(!done) {
                            // The try-catch here is basically so the url i'm trying to get later using downloadUrl
                            // will be valid and the app won't crash. As long as the task is not complete, I just display
                            // a toaster.
                            try {
                                downloadUrl.getResult();
                            } catch (IllegalStateException e) {
                                continue;
                            }
                            done = true;
                        }
                        p_photo_url = downloadUrl.getResult().toString();
                        // Later, after the user finished the form, this url will be
                        // used to build an Offer object and push to DB
                    }
            });

        }
    }
}
