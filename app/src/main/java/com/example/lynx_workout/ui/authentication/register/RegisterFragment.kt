package com.example.lynx_workout.ui.authentication.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.lynx_workout.R
import kotlinx.android.synthetic.main.register_fragment_layout.*

class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        startObserver()
        initListeners()
    }


    private fun initListeners() {
        rf_register_btn.setOnClickListener {
            if (rf_email_et.text.isEmpty() || rf_username_et.text.isEmpty() || rf_password_et.text.isEmpty() || rf_confirm_password_et.text.isEmpty()) {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.complete_fields_warning_string),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(rf_email_et.text.toString()).matches()) {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.invalid_email_format_string),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (rf_password_et.text.toString() != rf_confirm_password_et.text.toString()) {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.password_and_confirm_password_different_string),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!rf_terms_cb.isChecked) {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.accept_terms_and_conditions_warning_string),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            toggleLoading(true)
            registerViewModel.registerAuthUser(
                rf_email_et.text.toString(),
                rf_password_et.text.toString()
            )

        }
    }

    private fun startObserver() {
        registerViewModel.registerObservable.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                //automatically update username after registration completed
                registerViewModel.sendVerificationEmail()
                registerViewModel.updateUserName(rf_username_et.text.toString())
            } else {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.registration_failed_string),
                    Toast.LENGTH_SHORT
                ).show()
            }
            toggleLoading(false)
        })

        registerViewModel.sendVerificationObservable.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.email_verification_message_string),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    activity,
                    resources.getString(R.string.error_send_email_verification_message_string),
                    Toast.LENGTH_SHORT
                ).show()
            }
            toggleLoading(false)
            activity?.supportFragmentManager?.popBackStack()
        })
    }

    private fun stopObserver() {
        registerViewModel.registerObservable.removeObservers(viewLifecycleOwner)
        registerViewModel.sendVerificationObservable.removeObservers(viewLifecycleOwner)
    }

    private fun toggleLoading(isLoading: Boolean) {
        rf_progress_bar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopObserver()
    }


}