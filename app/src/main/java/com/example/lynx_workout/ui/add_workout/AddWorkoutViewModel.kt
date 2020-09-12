package com.example.lynx_workout.ui.add_workout

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lynx_workout.data.models.WorkoutModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddWorkoutViewModel : ViewModel() {

    val saveWorkoutObservable = MutableLiveData<Task<Void>>()

    fun saveWorkout(position: Int,workout: WorkoutModel) {
        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseDatabase.getInstance().reference.child("users").child(it.uid)
                .child("workouts").child("$position")
                .setValue(workout).addOnCompleteListener { task ->
                    saveWorkoutObservable.value = task
                }
        }
    }

}