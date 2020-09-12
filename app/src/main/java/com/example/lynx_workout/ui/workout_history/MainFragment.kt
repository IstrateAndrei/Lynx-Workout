package com.example.lynx_workout.ui.workout_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lynx_workout.R
import com.example.lynx_workout.data.models.WorkoutModel
import com.example.lynx_workout.ui.add_workout.AddWorkoutFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.main_fragment_layout.*

class MainFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleLoading(true)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        startObserver()
        initComponents()
        mainViewModel.getWorkouts()
    }

    private fun initComponents() {

        //initialize workout history RecyclerView
        if (mf_workout_rv.layoutManager == null)
            mf_workout_rv.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        if (mf_workout_rv.adapter == null)
            mf_workout_rv.adapter =
                WorkoutHistoryAdapter(object : WorkoutHistoryAdapter.WorkoutHistoryItemListener {
                    override fun onWorkoutClicked(view: View, position: Int, item: WorkoutModel) {
                        val action = MainFragmentDirections.nextActionWorkoutDetails(item)
                        action.position = position
                        Navigation.findNavController(view).navigate(action)
                    }

                })


        mf_add_fab.setOnClickListener {
            val action = MainFragmentDirections.nextActionAddWorkout()
            mf_workout_rv.adapter?.let { adapter ->
                action.listCount = (adapter as WorkoutHistoryAdapter).itemCount
            }
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun startObserver() {
        mainViewModel.workoutHistoryObservable.observe(viewLifecycleOwner, Observer { workoutList ->
            toggleLoading(false)
            if (workoutList.isNotEmpty()) {
                mf_workout_rv.visibility = View.VISIBLE
                mf_couch_potato_iv.visibility = View.GONE
                (mf_workout_rv.adapter as WorkoutHistoryAdapter).updateList(workoutList)
            } else {
                mf_workout_rv.visibility = View.GONE
                mf_couch_potato_iv.visibility = View.VISIBLE
            }
        })
    }

    private fun stopObserver() {
        mainViewModel.workoutHistoryObservable.removeObservers(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopObserver()
    }

    private fun toggleLoading(isLoading: Boolean) {
        mf_loading_pb.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
