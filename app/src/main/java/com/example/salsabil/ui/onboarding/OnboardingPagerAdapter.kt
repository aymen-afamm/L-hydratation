package com.example.salsabil.ui.onboarding


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.salsabil.R

class OnboardingPagerAdapter(
    private val activity: OnboardingActivity
) : RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder>() {

    private val pages = listOf(
        OnboardingPage(
            R.drawable.ic_onboarding_1,
            R.string.onboarding_title_1,
            R.string.onboarding_desc_1
        ),
        OnboardingPage(
            R.drawable.ic_onboarding_2,
            R.string.onboarding_title_2,
            R.string.onboarding_desc_2
        ),
        OnboardingPage(
            R.drawable.ic_onboarding_3,
            R.string.onboarding_title_3,
            R.string.onboarding_desc_3
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivOnboarding)
        private val titleView: TextView = itemView.findViewById(R.id.tvOnboardingTitle)
        private val descView: TextView = itemView.findViewById(R.id.tvOnboardingDesc)

        fun bind(page: OnboardingPage) {
            imageView.setImageResource(page.imageRes)
            titleView.setText(page.titleRes)
            descView.setText(page.descRes)
        }
    }

    data class OnboardingPage(
        val imageRes: Int,
        val titleRes: Int,
        val descRes: Int
    )
}
