package com.example.lynx_workout.ui.user_profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileViewModel : ViewModel() {

    val updateProfileObservable = MutableLiveData<Task<Void>>()

    fun getUserDetails(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun saveChanges(username: String, imageUri: Uri) {
        FirebaseAuth.getInstance().currentUser?.let {
            val profileUpdates =
                UserProfileChangeRequest.Builder().setDisplayName(username).setPhotoUri(
                    imageUri
                ).build()

            it.updateProfile(profileUpdates).addOnCompleteListener { task ->
                updateProfileObservable.value = task
            }
        }
    }
}