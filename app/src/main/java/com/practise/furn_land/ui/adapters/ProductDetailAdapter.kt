package com.practise.furn_land.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.ProductDetail

class ProductDetailAdapter(
    var productDetails: List<ProductDetail>
    ): RecyclerView.Adapter<ProductDetailAdapter.ProductDetailViewHolder>() {

    inner class ProductDetailViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val tvDetailTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        val tvDetailContent = view.findViewById<TextView>(R.id.tvDetailContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewHolder {
        Log.i("ProductAdapter","onCreateViewHolder() called")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_detail_item,parent,false)
        return ProductDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {

        Log.i("ProductAdapter","onCreateViewHolder() called - ${productDetails[position]}")
        holder.apply {
            tvDetailTitle.text = productDetails[position].productDetailTitle
            tvDetailContent.text = productDetails[position].productDetailContent
        }
    }

    override fun getItemCount(): Int {
        Log.i("ProductAdapter","getItemCount() called - itemCount : ${productDetails.size}")
        return productDetails.size
    }

}