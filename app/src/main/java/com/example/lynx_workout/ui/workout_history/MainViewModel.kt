package com.example.lynx_workout.ui.workout_history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lynx_workout.data.models.UserModel
import com.example.lynx_workout.data.models.WorkoutModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel : ViewModel() {

    val workoutHistoryObservable = MutableLiveData<MutableList<WorkoutModel>>()
    fun getWorkouts() {
        val rootReference = FirebaseDatabase.getInstance().reference

        val q1 = rootReference.child("users").orderByKey()
            .equalTo(FirebaseAuth.getInstance().currentUser!!.uid)

        q1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() > 0) {
                    for (item in snapshot.children) {
                        val user = item.getValue(UserModel::class.java)
                        workoutHistoryObservable.value = user?.workouts
                    }
                } else workoutHistoryObservable.value = mutableListOf()

            }
        })

    }
}