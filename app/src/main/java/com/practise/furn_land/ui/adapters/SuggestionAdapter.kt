package com.practise.furn_land.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.SuggestionHistory

class SuggestionAdapter(
    private val suggestions : MutableList<SuggestionHistory>,
    val onClickSuggestion: OnClickSuggestion
): RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    inner class SuggestionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val tvSuggestion = itemView.findViewById<TextView>(R.id.tvSuggestion)

        fun bind(suggestionHistory: SuggestionHistory){
            tvSuggestion.text = suggestionHistory.suggestion
            if (suggestionHistory.userId!=0) {
                tvSuggestion.setCompoundDrawables(
                    ResourcesCompat.getDrawable(
                        itemView.resources,
                        R.drawable.ic_history,
                        null
                    ),
                    null,
                    null,
                    null
                )
            }
            tvSuggestion.setOnLongClickListener {
                onClickSuggestion.onLongClick(suggestionHistory,adapterPosition)
                removeSuggestion(adapterPosition)
                true
            }
            tvSuggestion.setOnClickListener {
                onClickSuggestion.onClick(suggestionHistory)
            }
        }
    }

    private fun removeSuggestion(position: Int){
        suggestions.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnClickSuggestion{
        fun onClick(suggestion: SuggestionHistory)

        fun onLongClick(suggestionHistory: SuggestionHistory,position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_suggestion_item,parent,false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }
}