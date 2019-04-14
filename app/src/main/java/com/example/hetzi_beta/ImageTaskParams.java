package com.example.hetzi_beta;

import android.net.Uri;
import android.widget.ImageButton;

/*
* ImageTaskParams-
* A simple class built to contain some parameters for doInBackground.
*
* */

public class ImageTaskParams {
    public Uri image_uri;
    public ImageButton mPhotoPickerButton;
    public ProductDetailsPopupActivity context;

    public ImageTaskParams(Uri uri, ImageButton img_btn, ProductDetailsPopupActivity context) {
        this.image_uri = uri;
        this.mPhotoPickerButton = img_btn;
        this.context = context;
    }
}
