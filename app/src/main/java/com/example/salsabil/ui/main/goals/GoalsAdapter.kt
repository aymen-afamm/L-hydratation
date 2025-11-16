package com.example.salsabil.ui.main.goals


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.salsabil.R
import com.example.salsabil.data.local.entities.Goal
import com.example.salsabil.utils.toFormattedDate

class GoalsAdapter : ListAdapter<Goal, GoalsAdapter.GoalViewHolder>(GoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvGoalDate)
        private val tvGoalAmount: TextView = itemView.findViewById(R.id.tvGoalAmount)
        private val tvAchievedAmount: TextView = itemView.findViewById(R.id.tvAchievedAmount)
        private val ivStatus: ImageView = itemView.findViewById(R.id.ivGoalStatus)

        fun bind(goal: Goal) {
            tvDate.text = goal.date.toFormattedDate()
            tvGoalAmount.text = itemView.context.getString(R.string.goal_target, goal.targetMl)
            tvAchievedAmount.text = itemView.context.getString(R.string.goal_achieved_amount, goal.achievedMl)

            if (goal.isAchieved) {
                ivStatus.setImageResource(R.drawable.ic_check_circle)
            } else {
                ivStatus.setImageResource(R.drawable.ic_circle_outline)
            }
        }
    }

    class GoalDiffCallback : DiffUtil.ItemCallback<Goal>() {
        override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem.goalId == newItem.goalId
        }

        override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem == newItem
        }
    }
}
