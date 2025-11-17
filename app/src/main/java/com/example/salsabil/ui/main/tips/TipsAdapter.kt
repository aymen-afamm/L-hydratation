package com.example.salsabil.ui.main.tips


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.salsabil.R



class TipsAdapter(
    private val tips: List<HydrationTip>
) : RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.bind(tips[position])
    }

    override fun getItemCount(): Int = tips.size

    class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivTipIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTipTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvTipDescription)

        fun bind(tip: HydrationTip) {
            ivIcon.setImageResource(tip.iconRes)
            tvTitle.text = tip.title
            tvDescription.text = tip.description
        }
    }
}
data class HydrationTip(
    val title: String,
    val description: String,
    val iconRes: Int
)