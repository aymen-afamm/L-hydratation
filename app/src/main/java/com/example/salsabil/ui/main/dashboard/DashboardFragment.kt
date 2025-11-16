package com.example.salsabil.ui.main.dashboard


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.salsabil.R
import com.example.salsabil.data.local.database.SalsabilDatabase
import com.example.salsabil.data.repository.ConsumptionRepository
import com.example.salsabil.data.repository.GoalRepository
import com.example.salsabil.data.repository.UserRepository
import com.example.salsabil.ui.dialogs.AddConsumptionDialog
import com.example.salsabil.utils.Constants
import com.example.salsabil.utils.toFormattedAmount

class DashboardFragment : Fragment() {

    private lateinit var viewModel: DashboardViewModel
    private var userId: Long = -1

    private lateinit var tvConsumption: TextView
    private lateinit var tvGoal: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var barChart: BarChart
    private lateinit var fabAdd: FloatingActionButton

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long): DashboardFragment {
            return DashboardFragment().apply {
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
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = SalsabilDatabase.getDatabase(requireContext())
        val factory = DashboardViewModelFactory(
            ConsumptionRepository(database.consumptionLogDao()),
            GoalRepository(database.goalDao()),
            UserRepository(database.userDao())
        )
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        viewModel.setUserId(userId)

        tvConsumption = view.findViewById(R.id.tvConsumption)
        tvGoal = view.findViewById(R.id.tvGoal)
        tvProgress = view.findViewById(R.id.tvProgress)
        progressBar = view.findViewById(R.id.progressBar)
        barChart = view.findViewById(R.id.barChart)
        fabAdd = view.findViewById(R.id.fabAdd)

        setupChart()
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.todayConsumption.observe(viewLifecycleOwner) { consumed ->
            tvConsumption.text = getString(R.string.consumed_today, consumed.toFormattedAmount())
        }

        viewModel.todayGoal.observe(viewLifecycleOwner) { goal ->
            goal?.let {
                tvGoal.text = getString(R.string.daily_goal, it.targetMl.toFormattedAmount())
            }
        }

        viewModel.progressPercentage.observe(viewLifecycleOwner) { progress ->
            progressBar.progress = progress
            tvProgress.text = getString(R.string.progress_percentage, progress)
        }

        viewModel.weeklyStats.observe(viewLifecycleOwner) { weekData ->
            updateChart(weekData)
        }
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener {
            showAddConsumptionDialog()
        }
    }

    private fun setupChart() {
        barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            legend.isEnabled = false
            setFitBars(true)
            animateY(1000)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.parseColor(Constants.COLOR_TEXT_DARK)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                textColor = Color.parseColor(Constants.COLOR_TEXT_DARK)
            }

            axisRight.isEnabled = false
        }
    }

    private fun updateChart(weekData: List<com.example.salsabil.domain.models.DayData>) {
        val entries = weekData.mapIndexed { index, dayData ->
            BarEntry(index.toFloat(), dayData.amount.toFloat())
        }

        val dataSet = BarDataSet(entries, "Weekly Consumption").apply {
            color = Color.parseColor(Constants.COLOR_PRIMARY)
            valueTextColor = Color.parseColor(Constants.COLOR_TEXT_DARK)
            valueTextSize = 10f
        }

        val barData = BarData(dataSet)
        barChart.data = barData

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(weekData.map { it.dayName })
        barChart.invalidate()
    }

    private fun showAddConsumptionDialog() {
        AddConsumptionDialog(requireContext()) { amountMl ->
            viewModel.addConsumption(amountMl)
        }.show()
    }
}
