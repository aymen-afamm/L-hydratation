package com.example.salsabil.ui.main.goals


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.salsabil.R
import com.example.salsabil.data.local.database.SalsabilDatabase
import com.example.salsabil.data.repository.GoalRepository
import com.example.salsabil.data.repository.UserRepository
import com.example.salsabil.utils.showToast

class GoalsFragment : Fragment() {

    private lateinit var viewModel: GoalsViewModel
    private lateinit var goalsAdapter: GoalsAdapter
    private var userId: Long = -1

    private lateinit var etGoalAmount: EditText
    private lateinit var btnSetGoal: Button
    private lateinit var tvCurrentGoal: TextView
    private lateinit var tvProgressText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rvGoalsHistory: RecyclerView

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long): GoalsFragment {
            return GoalsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_USER_ID, userId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getLong(ARG_USER_ID) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = SalsabilDatabase.getDatabase(requireContext())
        val factory = GoalsViewModelFactory(
            GoalRepository(database.goalDao()),
            UserRepository(database.userDao())
        )
        viewModel = ViewModelProvider(this, factory)[GoalsViewModel::class.java]
        viewModel.setUserId(userId)

        etGoalAmount = view.findViewById(R.id.etGoalAmount)
        btnSetGoal = view.findViewById(R.id.btnSetGoal)
        tvCurrentGoal = view.findViewById(R.id.tvCurrentGoal)
        tvProgressText = view.findViewById(R.id.tvProgressText)
        progressBar = view.findViewById(R.id.progressBar)
        rvGoalsHistory = view.findViewById(R.id.rvGoalsHistory)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        goalsAdapter = GoalsAdapter()
        rvGoalsHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.currentGoal.observe(viewLifecycleOwner) { goal ->
            goal?.let {
                tvCurrentGoal.text = getString(R.string.current_goal_text, it.targetMl)
                progressBar.progress = if (it.targetMl > 0) {
                    ((it.achievedMl.toFloat() / it.targetMl.toFloat()) * 100).toInt()
                } else 0
                tvProgressText.text = getString(
                    R.string.goal_progress,
                    it.achievedMl,
                    it.targetMl,
                    progressBar.progress
                )
            }
        }

        viewModel.goalsHistory.observe(viewLifecycleOwner) { goals ->
            goalsAdapter.submitList(goals)
        }
    }

    private fun setupListeners() {
        btnSetGoal.setOnClickListener {
            val goalText = etGoalAmount.text.toString().trim()
            if (goalText.isEmpty()) {
                showToast(getString(R.string.error_goal_empty))
                return@setOnClickListener
            }

            val goalAmount = goalText.toIntOrNull()
            if (goalAmount == null || goalAmount <= 0) {
                showToast(getString(R.string.error_goal_invalid))
                return@setOnClickListener
            }

            viewModel.setDailyGoal(goalAmount)
            etGoalAmount.setText("")
            showToast(getString(R.string.goal_set_success))
        }
    }
}
