package com.example.hetzi_beta.BusinessApp.EditOffers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hetzi_beta.Offers.Offer;
import com.example.hetzi_beta.R;
import com.example.hetzi_beta.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

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

    // Activity globals that just... helps.
    private File                photo_from_camera;
    private String              photo_path_from_camera;
    private boolean             edit_mode;
    private String              curr_fbKey;
    private ArrayList<String>   modified_fields;

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
    private Button          mDeleteButton;

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

        Utils.disableButton(mPublishButton, this, "round");
        mPhotoProgress.setVisibility(View.GONE);
        resetTimeAndDate();

        onClickPickPhoto();
        onClickPublish();
        onClickDateAndTime();
        onClickDelete();
        setupSeekBarListener();

        handleExistingOfferScenario();
    }

    /*
     * onActivityResult -
     *
     * onActivityResult is long in this activity, because it handles photos which come from both
     * Gallery and Camera.
     *
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == HTZ_GALLERY || requestCode == HTZ_CAMERA) && resultCode == RESULT_OK) {
            // ~~~~~~ (1) Update the view immediately, and save the image URI  ~~~~~~ //
            Uri selectedImageUri = null;

            if (requestCode == HTZ_CAMERA) {
                File f = new File(photo_path_from_camera);
                selectedImageUri = Uri.fromFile(f);
                Utils.updateViewImage(OfferDetailsPopupActivity.this, photo_from_camera, mPhotoPickerButton);
            }
            else if (requestCode == HTZ_GALLERY) {
                selectedImageUri = data.getData();
                Utils.updateViewImage(OfferDetailsPopupActivity.this, selectedImageUri, mPhotoPickerButton);
            }

            // right after updating the view, show the Progress Circle and disable the publish button
            // until upload is done
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
                                            // When upload is complete, we can remove the Progress Circle and possibly enable the Publish button
                                            photo_firebase_uri = uri;
                                            photo_done = true;
                                            mPhotoProgress.setVisibility(View.GONE);
                                            modified_fields.add("photoUrl");
                                            checkEnablePublishButton();
                                        }
                                    });
                        }
                    });
        }
    }



    // -------------------------- Publish Button Related Methods ------------------------------- //
    /*
    * onClickPublish -
    *
    * Launched when the user clicks 'Publish Offer'.
    * This method creates an Offer object based on the values currently filled in all the fields
    * of the popup activity, and pushes this Offer to Firebase Realtime DataBase.
    *
    * In addition, it puts the offer as EXTRA to the intent (Offer is a Parcelable object) so that
    * it can be displayed in the RecyclerView of the Offers List, instead of pulling it from Firebase again.
    *
    * */
    private void onClickPublish() {
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create offer object from user input
                final Offer n_offer = createOfferObjectFromView();
                if(!edit_mode) // edit_mode is for existing offers
                {
                    // Push to Firebase Realtime DataBase
                    mOffersDatabaseReference.push().setValue(n_offer, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference) {
                            String key = databaseReference.getKey();
                            Map<String, Object> userUpdates = new HashMap<>();
                            userUpdates.put("fbKey", key);
                            mOffersDatabaseReference.child(key).updateChildren(userUpdates);
                            n_offer.setFbKey(key);

                            // Return offer to EditableOffersListFragment
                            // This code is duplicated (and not placed outside of the 'else') because
                            // I have to update n_offer's fbKey before returning it to EditableOffersListFragment
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("offer", n_offer);
                            setResult(HTZ_ADD_OFFER, resultIntent);
                            finish();
                        }
                    });
                } else {
                    Map<String, Object> userUpdates = new HashMap<>();
                    for(String field : modified_fields) {
                        getUserUpdateFromString(n_offer, userUpdates, field);
                    }
                    mOffersDatabaseReference.child(curr_fbKey).updateChildren(userUpdates);

                    // Return offer to EditableOffersListFragment
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("offer", n_offer);
                    setResult(HTZ_ADD_OFFER, resultIntent);
                    finish();
                }

            }
        });
    }

    /*
     * createOfferObjectFromView -
     *
     * Creates an Offer object from the data currently in display and returns it.
     *
     * */
    private Offer createOfferObjectFromView() {
        return new Offer(
                mName.getText().toString(),
                photo_firebase_uri.toString(),
                Integer.parseInt(mQuantity.getText().toString()),
                Float.parseFloat(mPrice.getText().toString()),
                mDiscountSeekbar.getProgress(),
                mTimeSpinner.getSelectedItem().toString(),
                offer_date.start_day, offer_date.start_month, offer_date.start_year,
                offer_date.start_hour, offer_date.start_minute
        );
    }

    /*
     * getUserUpdateFromString -
     *
     * Takes an offer and a map with user changes, and a field name as String.
     * The method puts the value from n_offer, corresponding to the given field, in the \
     * userUpdates map.
     *
     * */
    private void getUserUpdateFromString(Offer n_offer, Map<String, Object> userUpdates, String field) {
        if(field.equals("title") || field.equals("photoUrl") || field.equals("s_date"))
            userUpdates.put(field, n_offer.getFieldFromString(field));
        else if(field.equals("origPrice"))
            userUpdates.put(field, Float.parseFloat(n_offer.getFieldFromString(field)));
        else
            userUpdates.put(field, Integer.parseInt(n_offer.getFieldFromString(field)));
    }

    /*
    * checkEnablePublishButton -
    *
    * This method both CHECKS if the PublishButton should be active, and ENABLES or DISABLES it
    * accordingly.
    *
    * */
    public void checkEnablePublishButton() {
        if ( emptyEditTextsExists() || !photoReady() ||
                !isValidStartDate(offer_date.start_day, offer_date.start_month, offer_date.start_year,
                        offer_date.start_hour, offer_date.start_minute )) {
            Utils.disableButton(mPublishButton, this, "round");
        } else {
            Utils.enableButton(mPublishButton, this, "round");
        }
    }

    /*
    * photoReady -
    *
    * Checks the two conditions for a photo to be ready (meaning it's uploaded to Firebase successfully).
    *
    * */
    private boolean photoReady() {
        return photo_done && photo_firebase_uri != null;
    }

    /*
    * emptyEditTextsExists -
    *
    * Return TRUE if one of the EditTexts is empty, and FALSE otherwise.
    *
    * */
    private boolean emptyEditTextsExists() {
        return mName.getText().toString().length() == 0 || mPrice.getText().toString().length() == 0 ||
                mQuantity.getText().toString().length() == 0;
    }

    // ----------------------------------------------------------------------------------------- //



    // ------------------------------- Edit Mode Related Methods ------------------------------- //
    /*
     * showDialogConfirmDeletion -
     *
     * Pops a dialog that verifies that the user indeed wants to delete the Offer. If yes, the Offer
     * is deleted from RecyclerView (using EXTRA to the Fragment) and Firebase.
     *
     * */
    public void showDialogConfirmDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.drawable.shape_dialog_round_corners);

        builder.setMessage("באמת למחוק?");
        builder.setCancelable(true);

        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Delete from Firebase
                mOffersDatabaseReference.child(curr_fbKey).removeValue();

                // Return info to remove the Offer from RecyclerView
                Intent resultIntent = new Intent();
                resultIntent.putExtra("offer", (Parcelable)null);
                resultIntent.putExtra("delete_key", curr_fbKey);
                setResult(HTZ_ADD_OFFER, resultIntent);
                dialog.cancel();
                finish();
            }
        });

        builder.show();
    }

    private void onClickDelete() {
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogConfirmDeletion();
            }
        });
    }

    /*
     * handleExistingOfferScenario -
     *
     * This method checks if there is an incoming Offer, and if so - loads the Offer details to view
     * and sets the relevant memebers to their correct value.
     * If there is now incoming Offer, only some adjustments to the UI and memebers handling is
     * required.
     *
     * */
    private void handleExistingOfferScenario() {
        Intent intent = getIntent();
        if( isExistingOffer(intent) ) {
            Offer from_recycler = intent.getParcelableExtra("offer_fromRecycler");
            putOfferInViews(from_recycler);

            edit_mode           = true;
            curr_fbKey          = from_recycler.getFbKey();
            photo_firebase_uri  = Uri.parse(from_recycler.getPhotoUrl());
            photo_done          = true;

            mPublishButton.setText("שמור שינויים");
        } else {
            edit_mode = false;

            // Set the Publish button width to match_parent, and hide the 'delete' button
            ViewGroup.LayoutParams params = mPublishButton.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mPublishButton.setLayoutParams(params);
            mDeleteButton.setVisibility(View.GONE);
        }
    }

    /*
     * isExistingOffer - Relies on EXTRA to check if there is an existing Offer attached.
     *
     * @return : TRUE if an exisiting Offer is attached, FALSE otherwise.
     *
     * */
    private boolean isExistingOffer(Intent intent) {
        return !intent.getBooleanExtra("new", true);
    }

    /*
     * putOfferInViews -
     *
     * This method handles setting all the views in the popup according to a given Offer.
     *
     * @param : Offer in - the Offer to set the views by.
     *
     * */
    private void putOfferInViews(Offer offer) {
        Utils.updateViewImage(this, Uri.parse(offer.getPhotoUrl()), mPhotoPickerButton);

        mDiscountSeekbar        .setProgress(offer.getDiscount());
        mTimeSpinner            .setSelection(determineTimeSpinnerIndex(offer));
        mName                   .setText(offer.getTitle());
        mQuantity               .setText(offer.getQuantity().toString());
        mPrice                  .setText(offer.getOrigPrice().toString());
        mDiscountPercent        .setText(offer.getDiscount().toString());

        displayChosenDate(offer.getFromDate("day"), offer.getFromDate("month"), offer.getFromDate("year"));
        displayChosenTime(offer.getFromDate("hour"), offer.getFromDate("minute"));
        updatePriceAfterDiscount();
    }

    private Integer determineTimeSpinnerIndex(Offer offer) {
        Instant start = Instant.parse(offer.getS_time());
        Instant end   = Instant.parse(offer.getE_time());

        long gap = ChronoUnit.MINUTES.between(start, end);

        return (int)((gap / 30) - 1);
    }

    // ----------------------------------------------------------------------------------------- //



    // ------------------------------- Date/Time Related Methods ------------------------------- //
    /*
     * showDatePickerDialog, showTimePickerDialog -
     *
     * These functions create and show a popup for picking a date/time.
     * The logic for the picker (date validity and enabling the mSaveChanges button, for example)
     * is implemented in the different Fragments' file.
     *
     * */
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

    /*
    * displayChosenTime, displayChosenDate -
    *
    * These functions update the relevant EditTexts to display a given date/time.
    *
     * */
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

    /*
    * isValidStartDate -
    *
    * Simple function, containing the logic behind determining whether or not the picked date
    * (given here as 5 seperate ints) is ok as an Offer's starting date.
    *
    * */
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
        } else return start_year > c.get(Calendar.YEAR);

        return false;
    }

    /*
     * resetTimeAndDate -
     *
     * Resets the time and date both in display and in memory.
     *
     * */
    private void resetTimeAndDate() {
        final Calendar c = Calendar.getInstance();
        int default_hour = c.get(Calendar.HOUR_OF_DAY) + 1; // since there is a time gap between user's click and the call to getInstance()
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
                modified_fields.add("s_date");
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                modified_fields.add("s_date");
            }
        });
    }
    // ----------------------------------------------------------------------------------------- //


    // -------------------------- Camera/Gallery Related Methods ------------------------------- //
    private void onClickPickPhoto() {
        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPhotoOrGallery();
            }
        });
    }

    /*
     * showDialogPhotoOrGallery -
     *
     * Pops a dialog that encourages the user to pick a source for the picture, and launches the
     * appropriate activity, corresponding to the user's choice.
     *
     * */
    public void showDialogPhotoOrGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.drawable.shape_dialog_round_corners);

        builder.setMessage("לבחור מהגלריה או לפתוח מצלמה?");
        builder.setCancelable(true);

        builder.setNegativeButton("גלריה", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivityGallery();
                dialog.cancel();
            }
        });

        builder.setPositiveButton("מצלמה", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivityCamera();
                dialog.cancel();
            }
        });

        builder.show();
    }

    /*
    * startActivityCamera -
    *
    * Creates an intent dedicated to opening the camera and take a photo to save in onActivityResult.
    *
    * Built with Google's kind help:
    *   https://developer.android.com/training/camera/photobasics.html#TaskPath
    * */
    private void startActivityCamera() {
        Intent take_pic_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            photo_from_camera = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (photo_from_camera != null) {
            Uri photoURI = FileProvider.getUriForFile(OfferDetailsPopupActivity.this,
                    "com.example.hetzi_beta.fileprovider",
                    photo_from_camera);

            // Below 2 lines are to avoid FileUriExposedException
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            take_pic_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(take_pic_intent, HTZ_CAMERA);
        }
    }

    /*
     * startActivityGallery -
     *
     * Creates an intent dedicated to opening the gallery and pick a photo to save in onActivityResult.
     *
     * */
    private void startActivityGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, HTZ_GALLERY);
    }

    /*
    * createImageFile -
    *
    * This function only prepares (and creates) the file that we can write the photo to.
    * The timestamp stage is for the name to be completely unique.
    *
    * */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );

        // save the created path in photo_path_from_camera for later use (in onActivityResult)
        photo_path_from_camera = image.getAbsolutePath();
        return image;
    }
    // ----------------------------------------------------------------------------------------- //

    // ------------------------------- Inits and Setups ---------------------------------------- //
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
                modified_fields.add("discount");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        mDeleteButton       = findViewById(R.id.delete_button);

        // Listen to text changes
        mName       .addTextChangedListener(nameWatcher);
        mQuantity   .addTextChangedListener(quantityWatcher);
        mPrice      .addTextChangedListener(priceWatcher);

        photo_done = false;
        photo_firebase_uri = null;
        offer_date = new OfferStartDate();
        modified_fields = new ArrayList<>();
    }

    /*
     * updatePriceAfterDiscount -
     *
     * This method updates the TextView that displays the price after discount according to the price
     * that's currently displayed in the Price TextView, and the discount as displayed in the SeekBar.
     *
     * */
    private void updatePriceAfterDiscount() {
        if (mPrice.getText().toString().length() != 0) {
            mAfterDiscount.setText( Utils.round(Utils.priceAfterDiscount(Float.parseFloat(mPrice.getText().toString()),
                    mDiscountSeekbar.getProgress()),2).toString() );
        }
    }
    // ----------------------------------------------------------------------------------------- //

    // ------------------------------------- Text Watchers ------------------------------------- //
    /*
     * Different watchers are needed in order to update the modified_fields list (for edit_mode).
     * Except modified_fields, the logic in all of those watchers is the same.
     *
     * */
    private final TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            modified_fields.add("title");
            checkEnablePublishButton();
            updatePriceAfterDiscount();
        }
    };
    private final TextWatcher quantityWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            modified_fields.add("quantity");
            checkEnablePublishButton();
            updatePriceAfterDiscount();
        }
    };
    private final TextWatcher priceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            modified_fields.add("origPrice");
            checkEnablePublishButton();
            updatePriceAfterDiscount();
        }
    };
    // ----------------------------------------------------------------------------------------- //
}


