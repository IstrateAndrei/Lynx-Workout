package com.example.lynx_workout.ui.workout_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.lynx_workout.R
import com.example.lynx_workout.data.models.WorkoutModel
import kotlinx.android.synthetic.main.workout_details_fragment_layout.*

class WorkoutDetailsFragment : Fragment() {

    private var itemPosition = 0
    private lateinit var workoutDetailsViewModel: WorkoutDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.workout_details_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutDetailsViewModel = ViewModelProvider(this).get(WorkoutDetailsViewModel::class.java)
        startObservers()
        initListeners()
        arguments?.let {
            itemPosition = WorkoutDetailsFragmentArgs.fromBundle(it).position
            val item = WorkoutDetailsFragmentArgs.fromBundle(it).item
            displayDetails(item)
        }
    }

    private fun displayDetails(item: WorkoutModel) {
        wdf_name_tv.text = item.workout_name
        wdf_calories_tv.text = item.burned_calories
        wdf_duration_tv.text = item.workout_duration
        wdf_date_tv.text = item.workout_date

        wdf_poster_iv.visibility = if (item.workout_image.isNotEmpty()) View.VISIBLE else View.GONE
        if (item.workout_image.isNotEmpty()) {
            Glide.with(requireActivity()).load(item.workout_image)
                .placeholder(R.drawable.ic_profile_placeholder).centerInside().into(wdf_poster_iv)
        }
    }

    private fun initListeners() {
        wdf_delete_iv.setOnClickListener {
            workoutDetailsViewModel.deleteWorkout(itemPosition)
        }
    }

    private fun startObservers() {
        workoutDetailsViewModel.deleteWorkoutObservable.observe(viewLifecycleOwner, Observer {
            toggleLoading(false)
            if (it.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.workout_entry_deleted_string),
                    Toast.LENGTH_SHORT
                ).show()
                Navigation.findNavController(requireActivity(), R.id.wdf_delete_iv).popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.unable_delete_workout_string),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun stopObservers() {
        workoutDetailsViewModel.deleteWorkoutObservable.removeObservers(viewLifecycleOwner)
    }

    private fun toggleLoading(isLoading: Boolean) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopObservers()
    }
}