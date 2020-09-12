package com.example.lynx_workout.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.lynx_workout.R
import com.example.lynx_workout.ui.authentication.register.RegisterFragment
import com.example.lynx_workout.ui.workout_history.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login_activity_layout.*

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_layout)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        startObserver()
        initListeners()
    }

    private val mAuthListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            if (user.isEmailVerified)
                openWorkoutScreen()
            else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.email_verification_message_string),
                    Toast.LENGTH_SHORT
                ).show()
//                FirebaseAuth.getInstance().signOut()
            }
        }
    }

    private fun initListeners() {
        la_login_btn.setOnClickListener {
            if (la_email_et.text.isEmpty() || la_password_et.text.isEmpty()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.complete_fields_warning_string),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(la_email_et.text.toString()).matches()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.invalid_email_format_string),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            toggleLoading(true)
            loginViewModel.loginAction(la_email_et.text.toString(), la_password_et.text.toString())
        }

        la_register_msg_tv.setOnClickListener {
            val fragment =
                RegisterFragment()
            supportFragmentManager.beginTransaction().replace(R.id.la_fragment_fl, fragment)
                .addToBackStack(RegisterFragment::class.java.simpleName).commit()
        }
    }

    private fun startObserver() {
        loginViewModel.loginObservable.observe(this, Observer { response ->
            toggleLoading(false)
            if (response.isSuccessful) {
                FirebaseAuth.getInstance().currentUser?.let {
                    if (it.isEmailVerified) openWorkoutScreen()
                    else Toast.makeText(
                        this,
                        resources.getString(R.string.email_verification_message_string),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.unable_to_connect_string),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener)
    }

    private fun stopObserver() {
        loginViewModel.loginObservable.removeObservers(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopObserver()
    }

    private fun toggleLoading(isLoading: Boolean) {
        la_progress_bar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun openWorkoutScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

}