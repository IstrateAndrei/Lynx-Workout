package com.example.lynx_workout.ui.workout_history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lynx_workout.R
import com.example.lynx_workout.data.models.WorkoutModel
import kotlinx.android.synthetic.main.workout_history_item_layout.view.*

class WorkoutHistoryAdapter(private val listener: WorkoutHistoryItemListener) :
    RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutViewHolder>() {


    interface WorkoutHistoryItemListener {
        fun onWorkoutClicked(view: View, position: Int, item: WorkoutModel)
    }

    private val workoutList = mutableListOf<WorkoutModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        return WorkoutViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.workout_history_item_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    fun updateList(newList: MutableList<WorkoutModel>) {
        workoutList.clear()
        workoutList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.displayWorkout(position, workoutList[position])
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun displayWorkout(position: Int, item: WorkoutModel) {

            itemView.whi_workout_name_tv.text = item.workout_name
            itemView.whi_calories_tv.text = item.burned_calories
            itemView.whi_date_tv.text = item.workout_date
            itemView.whi_duration_tv.text = item.workout_duration

            if (item.workout_image.isNotEmpty()) {
                Glide.with(itemView.context).load(item.workout_image).centerInside()
                    .placeholder(R.drawable.ic_baseline_image_24).into(itemView.whi_photo_iv)
            }

            itemView.setOnClickListener { listener.onWorkoutClicked(it, position, item) }
        }
    }
}