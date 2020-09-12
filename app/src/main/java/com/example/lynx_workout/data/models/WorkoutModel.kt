package com.example.lynx_workout.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkoutModel(
    var workout_name: String = "",
    var workout_date: String = "",
    var burned_calories: String = "",
    var workout_duration: String = "",
    var workout_image: String = ""
) : Parcelable {
}