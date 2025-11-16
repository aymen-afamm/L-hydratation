package com.example.salsabil.ui.main.tips


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.example.salsabil.R

class TipsFragment : Fragment() {

    private lateinit var rvTips: RecyclerView
    private lateinit var btnShare: MaterialButton
    private lateinit var tipsAdapter: TipsAdapter

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long): TipsFragment {
            return TipsFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_USER_ID, userId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTips = view.findViewById(R.id.rvTips)
        btnShare = view.findViewById(R.id.btnShareTips)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        val tips = getHydrationTips()
        tipsAdapter = TipsAdapter(tips)
        rvTips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tipsAdapter
        }
    }

    private fun setupListeners() {
        btnShare.setOnClickListener {
            shareTips()
        }
    }

    private fun getHydrationTips(): List<HydrationTip> {
        return listOf(
            HydrationTip(
                getString(R.string.tip_1_title),
                getString(R.string.tip_1_description),
                R.drawable.ic_tip_1
            ),
            HydrationTip(
                getString(R.string.tip_2_title),
                getString(R.string.tip_2_description),
                R.drawable.ic_tip_2
            ),
            HydrationTip(
                getString(R.string.tip_3_title),
                getString(R.string.tip_3_description),
                R.drawable.ic_tip_3
            ),
            HydrationTip(
                getString(R.string.tip_4_title),
                getString(R.string.tip_4_description),
                R.drawable.ic_tip_4
            ),
            HydrationTip(
                getString(R.string.tip_5_title),
                getString(R.string.tip_5_description),
                R.drawable.ic_tip_5
            )
        )
    }

    private fun shareTips() {
        val shareText = buildString {
            append(getString(R.string.share_tips_header))
            append("\n\n")
            getHydrationTips().forEachIndexed { index, tip ->
                append("${index + 1}. ${tip.title}\n")
                append("${tip.description}\n\n")
            }
            append(getString(R.string.share_tips_footer))
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
    }
}
