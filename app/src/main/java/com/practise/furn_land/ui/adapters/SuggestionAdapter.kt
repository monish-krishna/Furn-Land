package com.practise.furn_land.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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
        private val ibRemove = itemView.findViewById<ImageButton>(R.id.ibRemove)
        private val ivSearch = itemView.findViewById<ImageView>(R.id.ivSearch)

        fun bind(suggestionHistory: SuggestionHistory){
            tvSuggestion.text = suggestionHistory.suggestion
            if (suggestionHistory.userId>0) {
                ivSearch.setImageDrawable(ResourcesCompat.getDrawable(itemView.resources,R.drawable.ic_history,null))
                ibRemove.visibility = View.VISIBLE
            }else{
                ivSearch.setImageDrawable(ResourcesCompat.getDrawable(itemView.resources,R.drawable.ic_search,null))
                ibRemove.visibility = View.GONE
            }
            ibRemove.setOnClickListener {
                onClickSuggestion.onClickRemove(suggestionHistory,adapterPosition)
            }
            tvSuggestion.setOnClickListener {
                onClickSuggestion.onClick(suggestionHistory)
            }
        }
    }

    fun removeSuggestion(position: Int){
        suggestions.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnClickSuggestion{
        fun onClick(suggestion: SuggestionHistory)

        fun onClickRemove(suggestionHistory: SuggestionHistory, position: Int)
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}