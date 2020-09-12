package com.example.lynx_workout.utils

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.util.*


fun Bitmap.getImageUri(activity: Activity): Uri? {
    val bytes = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        activity.contentResolver,
        this,
        UUID.randomUUID().toString().toString() + ".png",
        "drawing"
    )
    return Uri.parse(path)
}

