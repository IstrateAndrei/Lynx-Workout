package com.example.lynx_workout.ui.workout_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class WorkoutDetailsViewModel : ViewModel() {

    val deleteWorkoutObservable = MutableLiveData<Task<Void>>()

    fun deleteWorkout(position: Int) {
        FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("workouts").child("$position").removeValue().addOnCompleteListener {
                deleteWorkoutObservable.value = it
            }
    }
}