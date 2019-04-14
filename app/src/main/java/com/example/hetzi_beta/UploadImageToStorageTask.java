package com.example.hetzi_beta;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.Executor;

/*
* UploadImageToStorageTask -
* Basically the goal of this task is to upload the product image asynchronously so that the user can
* continue editing the form in the meantime. It also handles changing the ImageButton image to the
* uploaded picture, and updates the ProductDetailsPopupActivity that called it with the url.
*
* The relevant ImageButton and ProductDetailsPopupActivity are passed in ImageTaskParams.
* */

public class UploadImageToStorageTask extends AsyncTask<ImageTaskParams, Integer, ImageTaskParams> {
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotosStorageReference;
    private Uri firebase_img_url;

    @Override
    protected ImageTaskParams doInBackground(ImageTaskParams... params) {
        // for now only one image
        Uri img_local_uri = params[0].image_uri;

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
                                firebase_img_url = uri;
                            }
                        });
                    }
                });

        return new ImageTaskParams(firebase_img_url,
                params[0].mPhotoPickerButton, params[0].context);
    }

    @Override
    public void onPostExecute(ImageTaskParams result) {
        result.context.p_photo_url = result.image_uri; // Updating the caller ProductDetailsPopupActivity's member
        // Glide handles the image replacement in the given ImageButton
        Glide.with(result.context)
                .load(result.image_uri)
                .centerCrop()
                .into(result.mPhotoPickerButton);
    }
}
