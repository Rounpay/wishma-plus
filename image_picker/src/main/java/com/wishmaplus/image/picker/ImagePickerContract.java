package com.wishmaplus.image.picker;

import android.content.Intent;

import java.io.File;

public interface ImagePickerContract {
    @SuppressWarnings("UnusedReturnValue")
    ImagePicker setWithImageCrop(int aspectRatioX, int aspectRatioY);

    ImagePicker setWithImageCrop();

    //    ImagePicker setWithIntentPickerTitle(String title);
//    ImagePicker setWithIntentPickerTitle(@StringRes int title);
    void choosePictureWithoutPermission(boolean includeCamera,boolean includeGallery);

    void choosePicture(boolean includeCamera,boolean includeGallery);

    void openCamera();

    File getImageFile();

    void handlePermission(int requestCode, int[] grantResults);

    void handleActivityResult(int resultCode, int requestCode, Intent data);
}
