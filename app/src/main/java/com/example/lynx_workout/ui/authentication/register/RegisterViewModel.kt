package com.example.lynx_workout.ui.authentication.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterViewModel : ViewModel() {

    val registerObservable = MutableLiveData<Task<AuthResult>>()
    val sendVerificationObservable = MutableLiveData<Task<Void>>()

    fun registerAuthUser(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                registerObservable.value = task
            }
    }

    fun sendVerificationEmail() {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnCompleteListener {
            sendVerificationObservable.value = it
        }
    }

    fun updateUserName(username: String) {
        FirebaseAuth.getInstance().currentUser?.let {
            val usernameUpdate = UserProfileChangeRequest.Builder().setDisplayName(username).build()
            it.updateProfile(usernameUpdate)
        }
    }

}