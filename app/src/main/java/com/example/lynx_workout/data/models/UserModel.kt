package com.example.lynx_workout.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    var workouts: MutableList<WorkoutModel> = mutableListOf()
) : Parcelable {
}