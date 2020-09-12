package com.example.lynx_workout.ui.authentication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lynx_workout.data.repository.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class LoginViewModel : ViewModel(), KoinComponent {


    val loginObservable = MutableLiveData<Task<AuthResult>>()

    fun loginAction(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            loginObservable.value = task
        }
    }
}