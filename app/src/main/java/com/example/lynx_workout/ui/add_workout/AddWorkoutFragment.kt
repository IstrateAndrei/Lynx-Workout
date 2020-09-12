package com.example.lynx_workout.ui.add_workout

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.lynx_workout.R
import com.example.lynx_workout.data.models.WorkoutModel
import com.example.lynx_workout.utils.getImageUri
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.add_workout_fragment_layout.*
import kotlinx.android.synthetic.main.user_profile_fragment_layout.*
import org.jetbrains.anko.doAsync
import java.util.*

class AddWorkoutFragment : Fragment() {

    private var newItemPosition = 0
    private lateinit var selectedBitmap: Bitmap
    private lateinit var addWorkoutViewModel: AddWorkoutViewModel
    private var date = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_workout_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addWorkoutViewModel = ViewModelProvider(this).get(AddWorkoutViewModel::class.java)
        startObserver()
        initListeners()
        arguments?.let {
            newItemPosition = AddWorkoutFragmentArgs.fromBundle(it).listCount
        }
    }

    private fun initListeners() {
        awf_save_btn.setOnClickListener {

            if (awf_workout_date_et.text.isEmpty() || awf_workout_calories_et.text.isEmpty() || awf_workout_duration_et.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.complete_fields_warning_string),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            toggleLoading(true)
            val workout = WorkoutModel()
            workout.workout_name = awf_workout_name_et.text.toString()
            workout.burned_calories = awf_workout_calories_et.text.toString()
            workout.workout_duration = awf_workout_duration_et.text.toString()
            workout.workout_date = awf_workout_date_et.text.toString()
            workout.workout_image = ""

            if (awf_workout_name_et.text.isEmpty())
                workout.workout_name = awf_workout_date_et.text.toString()

            addWorkoutViewModel.saveWorkout(newItemPosition, workout)

            if (::selectedBitmap.isInitialized) {
                doAsync {
                    selectedBitmap.getImageUri(requireActivity())?.let { it1 ->
                        FirebaseStorage.getInstance().reference.child("images/workouts").putFile(
                            it1
                        ).addOnSuccessListener {
                            it.storage.downloadUrl.addOnCompleteListener { uri ->
                                if (uri.isSuccessful) {
//                                workout.workout_image = uri.result.toString()
//                                addWorkoutViewModel.saveWorkout(newItemPosition, workout)

                                    FirebaseDatabase.getInstance().reference.child("users")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child("workouts").child("${newItemPosition - 1}")
                                        .child("workout_image")
                                        .setValue(uri.result.toString())
                                }

                            }
                        }
                    }
                }
            }
        }

        awf_camera_iv.setOnClickListener {
            RxPermissions(requireActivity()).request(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                .subscribe { granted ->
                    if (granted) {
                        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0)
                    }
                }
        }

        awf_gallery_iv.setOnClickListener {
            RxPermissions(requireActivity()).request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        startActivityForResult(
                            Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            ), 1
                        )
                    }
                }
        }

        awf_workout_date_til.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private val dateListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            date = "$year/$month/$dayOfMonth"
            TimePickerDialog(
                requireContext(),
                timeListener,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
            ).show()
        }

    private val timeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        val displayDate =
            "$date - ${if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"}:${if (minute < 10) "0$minute" else "$minute"}"
        awf_workout_date_et.setText(displayDate)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            val photo = (data?.extras?.get("data") as Bitmap?)
            photo?.let {
                selectedBitmap = it
                awf_picture_iv.visibility = View.VISIBLE
                awf_picture_iv.setImageBitmap(it)
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.let {

                awf_picture_iv.visibility = View.VISIBLE
                Glide.with(requireContext()).load(it).fitCenter()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(awf_picture_iv)

                doAsync {
                    selectedBitmap =
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it);
                }
            }
        }
    }

    private fun startObserver() {
        addWorkoutViewModel.saveWorkoutObservable.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.new_workout_entry_added_string),
                    Toast.LENGTH_SHORT
                ).show()

                newItemPosition += 1
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.unable_to_save_workout_string),
                    Toast.LENGTH_SHORT
                ).show()
            }
            toggleLoading(false)
        })
    }

    private fun stopObserver() {
        addWorkoutViewModel.saveWorkoutObservable.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopObserver()
    }

    private fun toggleLoading(isLoading: Boolean) {
        awf_loading_pb.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}