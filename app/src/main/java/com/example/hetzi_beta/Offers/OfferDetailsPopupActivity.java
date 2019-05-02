package com.example.hetzi_beta.Offers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.example.hetzi_beta.Utils.HTZ_ADD_OFFER;
import static com.example.hetzi_beta.Utils.HTZ_CAMERA;
import static com.example.hetzi_beta.Utils.HTZ_GALLERY;

/*
 * OfferDetailsPopupActivity -
 * This is the Pop-up activity that appears when the retailer clicks '+' (FAB).
 * The form contains textual/spinner details, and photo upload.
 *
 * */

public class OfferDetailsPopupActivity extends AppCompatActivity {
    public class OfferStartDate {
        public Integer     start_day;
        public Integer     start_month;
        public Integer     start_year;
        public Integer     start_hour;
        public Integer     start_minute;
    }

    private File            photo_from_camera;

    // Product details
    public Uri              photo_firebase_uri;
    public boolean          photo_done;
    public OfferStartDate   offer_date;

    // UI Items
    private ImageView       mPhotoPickerButton;
    private Button          mPublishButton;
    private SeekBar         mDiscountSeekbar;
    private Spinner         mTimeSpinner;
    private EditText        mName;
    private EditText        mQuantity;
    private EditText        mPrice;
    private TextView        mDiscountPercent;
    private Button          mDateButton;
    private Button          mTimeButton;
    private TextView        mAfterDiscount;
    private ProgressBar     mPhotoProgress;

    // Firebase instance variables (Storage and Realtime Database)
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOffersDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer_popup);

        initViewsAndReferences();
        initSpinners();

        disablePublishButton();
        mPhotoProgress.setVisibility(View.GONE);
        resetTimeAndDate();

        onClickPickPhoto();
        onClickPublish();
        onClickDateAndTime();
        setupSeekBarListener();

        Intent intent = getIntent();
        if(!intent.getBooleanExtra("new", true)) {

        }
    }

    private void resetTimeAndDate() {
        final Calendar c = Calendar.getInstance();
        int default_hour = c.get(Calendar.HOUR_OF_DAY) + 1;
        int default_minute = 0;

        displayChosenDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
        displayChosenTime(default_hour, default_minute);

        offer_date.start_day    = c.get(Calendar.DAY_OF_MONTH);
        offer_date.start_month  = c.get(Calendar.MONTH)+1;
        offer_date.start_year   = c.get(Calendar.YEAR);
        offer_date.start_hour   = default_hour;
        offer_date.start_minute = default_minute;
    }

    private void onClickDateAndTime() {
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
    }

    private void setupSeekBarListener() {
        mDiscountSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Discount percent is in 10s
                progress = progress / 10;
                progress = progress * 10;
                mDiscountSeekbar.setProgress(progress);
                mDiscountPercent.setText(String.valueOf(progress));

                // Updating the price after discount if needed
                updatePriceAfterDiscount();
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
        mPublishButton.setTextColor(getResources().getColor(R.color.Black));
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
        mPhotosStorageReference     = mFirebaseStorage.getReference().child("product_photos/" + user.getUid());
        mOffersDatabaseReference    = mFirebaseDatabase.getReference().child("offers/" + user.getUid());

        // Attach members to xml elements
        mPhotoPickerButton  = findViewById(R.id.product_photo_picker);
        mPublishButton      = findViewById(R.id.publish_button);
        mDiscountSeekbar    = findViewById(R.id.discount_SeekBar);
        mTimeSpinner        = findViewById(R.id.time_Spinner);
        mName               = findViewById(R.id.product_name_EditText);
        mQuantity           = findViewById(R.id.quantity_EditText);
        mPrice              = findViewById(R.id.price_EditText);
        mDiscountPercent    = findViewById(R.id.percent_number_TextView);
        mDateButton         = findViewById(R.id.actual_date_Button);
        mTimeButton         = findViewById(R.id.actual_time_Button);
        mAfterDiscount      = findViewById(R.id.after_discount_TextView);
        mPhotoProgress      = findViewById(R.id.determinateCircle);

        // Listen to text changes
        mName       .addTextChangedListener(watcher);
        mQuantity   .addTextChangedListener(watcher);
        mPrice      .addTextChangedListener(watcher);

        photo_done = false;
        photo_firebase_uri = null;
        offer_date = new OfferStartDate();
    }

    private void onClickPickPhoto() {
        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPhotoOrGallery();
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
                            Utils.timeFromStringToSecsAsInt(mTimeSpinner.getSelectedItem().toString()),
                            offer_date.start_day, offer_date.start_month, offer_date.start_year,
                            offer_date.start_hour, offer_date.start_minute
                    );

                    // Push to Firebase Realtime DataBase
                    mOffersDatabaseReference.push().setValue(n_offer);

                    // Return offer to EditableOffersListFragment
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
            checkEnablePublishButton();
            updatePriceAfterDiscount();
        }
    };

    public void checkEnablePublishButton() {
        // Take care of publish button
        if ( emptyEditTextsExists() || !photoReady() ||
                !isValidStartDate(offer_date.start_day, offer_date.start_month, offer_date.start_year,
                        offer_date.start_hour, offer_date.start_minute )) {
            disablePublishButton();
        } else {
            enablePublishButton();
        }
    }

    private boolean photoReady() {
        return photo_done && photo_firebase_uri != null;
    }

    private boolean emptyEditTextsExists() {
        return mName.getText().toString().length() == 0 || mPrice.getText().toString().length() == 0 ||
                mQuantity.getText().toString().length() == 0;
    }

    private void updatePriceAfterDiscount() {
        // Fix after discount TextView
        if (mPrice.getText().toString().length() != 0) {
            mAfterDiscount.setText( Utils.round(Utils.priceAfterDiscount(Float.parseFloat(mPrice.getText().toString()),
                    mDiscountSeekbar.getProgress()),2).toString() );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == HTZ_GALLERY || requestCode == HTZ_CAMERA) && resultCode == RESULT_OK) {
            // ~~~~~~ (1) Update the view immediately  ~~~~~~ //
            Uri selectedImageUri = null;

            if (requestCode == HTZ_CAMERA) {
                selectedImageUri = Uri.fromFile(photo_from_camera);
                Utils.updateViewImage(OfferDetailsPopupActivity.this, photo_from_camera, mPhotoPickerButton);
            }
            else if (requestCode == HTZ_GALLERY) {
                selectedImageUri = data.getData();
                Utils.updateViewImage(OfferDetailsPopupActivity.this, selectedImageUri, mPhotoPickerButton);
            }


            photo_done = false;
            mPhotoProgress.setVisibility(View.VISIBLE);
            checkEnablePublishButton();

            // ~~~~~~ (2) Delete previous photo if existed  ~~~~~~ //
            if(photo_firebase_uri != null) {
                mFirebaseStorage.getReferenceFromUrl((photo_firebase_uri).toString()).delete();
            }

            // ~~~~~~ (3) Upload Image to Firebase Storage  ~~~~~~ //
            StorageReference photoRef = mPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
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
                        mPhotoProgress.setVisibility(View.GONE);
                        checkEnablePublishButton();
                    }
                });
            }
            });
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        ((DatePickerFragment) newFragment).setLaunchingActivity(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        ((TimePickerFragment) newFragment).setLaunchingActivity(this);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void displayChosenTime(Integer hour, Integer minute) {
        if (minute < 10) {
            this.mTimeButton.setText(hour.toString() + ":0" + minute.toString());
        } else {
            this.mTimeButton.setText(hour.toString() + ":" + minute.toString());
        }
    }

    public void displayChosenDate(Integer day, Integer month, Integer year) {
        this.mDateButton.setText(day.toString() + "/" + month.toString() + "/" + year.toString());
    }

    private boolean isValidStartDate(Integer start_day, Integer start_month, Integer start_year,
                                     Integer start_hour, Integer start_minute) {
        final Calendar c = Calendar.getInstance();

        if(  start_year == c.get(Calendar.YEAR)) {
            if (start_month > c.get(Calendar.MONTH) + 1  ) {
                return true;
            } else if (start_month == c.get(Calendar.MONTH) + 1) {
                if (start_day > c.get(Calendar.DAY_OF_MONTH))
                    return true;
                else if (start_day == c.get(Calendar.DAY_OF_MONTH)) {
                    if (start_hour > c.get(Calendar.HOUR_OF_DAY))
                            return true;
                    else if(start_hour == c.get(Calendar.HOUR_OF_DAY))
                        return start_minute >= c.get(Calendar.MINUTE);
                }
            }
        } else if (start_year > c.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }

    public void showDialogPhotoOrGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.drawable.shape_dialog_round_corners);

        builder.setMessage("לבחור מהגלריה או לפתוח מצלמה?").setCancelable(true)
                .setNegativeButton("גלריה", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , HTZ_GALLERY);
                        dialog.cancel();
                    }
                })
                .setPositiveButton("מצלמה", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File dir=
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Timestamp(System.currentTimeMillis()));
                        photo_from_camera = new File(dir, user.getUid() + timestamp + "from_camera.jpeg");
                        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo_from_camera));

                        // Below 2 lines are to avoid FileUriExposedException
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());

                        startActivityForResult(i, HTZ_CAMERA);
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}


