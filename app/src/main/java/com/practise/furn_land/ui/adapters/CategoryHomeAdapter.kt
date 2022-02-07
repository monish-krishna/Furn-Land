package com.practise.furn_land.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.Category

class CategoryHomeAdapter(
    private val categories: List<Category>,
    private val onClickListener: OnClickCategory
): RecyclerView.Adapter<CategoryHomeAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val tvCategoryName = view.findViewById<TextView>(R.id.tvCategoryName)
        val iVcategory = view.findViewById<ImageView>(R.id.ivCategory)

        fun bind(category: Category){
            tvCategoryName.text = category.name
            iVcategory.load(category.image){
                crossfade(true)
                placeholder(R.drawable.ic_image_load)
            }
            view.setOnClickListener { onClickListener.onClick(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item,parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun interface OnClickCategory{
        fun onClick(category: Category)
    }
}