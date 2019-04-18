package com.example.hetzi_beta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.JobIntentService;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PhotosService extends JobIntentService {
    // Constants

//    public static final int HTZ_UPLOAD =  3;
//    public static final int HTZ_DOWNLOAD =  4;
    public static final int HTZ_SHOW_RESULT = 123;
    public static final String HTZ_ACTION_DOWNLOAD = "action.DOWNLOAD_DATA";
    public static final String HTZ_ACTION_UPLOAD = "action.UPLOAD_DATA";
    static final int HTZ_PHOTOSERVICE_JOB_ID = 1000;
    private static final String TAG = "PhotosService";
    public static final String RECEIVER = "receiver";

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;

    private ResultReceiver mResultReceiver;

    private Uri firebase_photo_url;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotosStorageReference = mFirebaseStorage.getReference().child("product_photos");
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onHandleWork(Intent intent) {
        if (intent.getAction() != null) {
            Uri img_local_uri = intent.getParcelableExtra("url");

            switch (intent.getAction()) {
                case HTZ_ACTION_UPLOAD:
                    mResultReceiver = intent.getParcelableExtra(RECEIVER);
                    StorageReference photoRef = mPhotosStorageReference.child(img_local_uri.getLastPathSegment());
                    photoRef.putFile(img_local_uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            firebase_photo_url = uri;
                                        }
                                    });
                                }
                            });
            }
        }
    }

    public static void enqueueWork(Context context, PhotoResultReciever workerResultReceiver, String mode, Uri photo_uri) {
        Intent intent = new Intent(context, PhotosService.class);
        intent.putExtra(RECEIVER, workerResultReceiver);
        intent.putExtra("url", photo_uri);
        intent.setAction(mode);
        enqueueWork(context, PhotosService.class, HTZ_PHOTOSERVICE_JOB_ID, intent);
    }

    @Override
    public void onDestroy() {
        if(firebase_photo_url != null) {
            Bundle bundle = new Bundle();
            bundle.putString("uri", firebase_photo_url.toString());
            mResultReceiver.send(HTZ_SHOW_RESULT, bundle);
        }
    }

}