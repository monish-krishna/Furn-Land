package com.practise.furn_land.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.Product
import com.practise.furn_land.data.entities.relations.ProductWithImages
import com.practise.furn_land.utils.isStrikeThrough
import kotlin.math.roundToInt

class ProductListAdapter(
    private val productWithImages: MutableList<ProductWithImages>,
    private val onClickListener: OnClickProduct
): RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val view: View): RecyclerView.ViewHolder(view){
        private val tvProductName = view.findViewById<TextView>(R.id.tvItemProductName)
        private val tvBrandName = view.findViewById<TextView>(R.id.tvItemBrandName)
        private val tvRatingIcon = view.findViewById<TextView>(R.id.tvItemRatingIcon)
        private val tvCurrentPrice = view.findViewById<TextView>(R.id.tvItemCurrentPrice)
        private val tvOriginalPrice = view.findViewById<TextView>(R.id.tvItemOriginalPrice)
        private val tvOffer = view.findViewById<TextView>(R.id.tvItemOffer)
        private val tvNoRating = view.findViewById<TextView>(R.id.tvItemNoRating)
        private val ivProductImage = view.findViewById<ImageView>(R.id.ivProductImage)

        fun bind(productWithImages: ProductWithImages){
            val product = productWithImages.product
            val productImages = productWithImages.productImages
            var imageUrl = productImages[0].productImageUrl
            view.setOnClickListener { onClickListener.onClick(product) }
            productImages.forEach { productImage ->
                if (productImage.productImageIndex ==1) imageUrl = productImage.productImageUrl
                else return@forEach
            }
            tvProductName.text = product.name
            val brandString = "Brand: ${product.brandName}"
            tvBrandName.text =  brandString
            tvRatingIcon.text = product.totalRating.toString()
            tvOriginalPrice.isStrikeThrough(true)
            ivProductImage.load(imageUrl){
                crossfade(true)
                placeholder(R.drawable.ic_image_load)
            }
            setUpRatingIcon()
            setUpPrices(product)
        }

        private fun setUpPrices(product: Product) {
            val currentPriceText = view.resources.getString(R.string.rs_symbol) + product.currentPrice.roundToInt()
            tvCurrentPrice.text = currentPriceText
            if(product.originalPrice > product.currentPrice){
                val originalPriceText = view.resources.getString(R.string.rs_symbol) + product.originalPrice.roundToInt()
                tvOriginalPrice.text = originalPriceText
                val offerPrice = (product.originalPrice - product.currentPrice) / product.originalPrice
                val offer = offerPrice * 100
                if (offer > 1) {
                    val offerText = offer.roundToInt().toString() + "% Off"
                    tvOffer.text = offerText
                } else {
                    tvOffer.visibility = View.GONE
                }
            }else{
                tvOriginalPrice.visibility = View.GONE
                tvOffer.visibility = View.GONE
            }
        }

        private fun setUpRatingIcon() {
            var rating = 0F
            try{
                rating = tvRatingIcon.text.toString().toFloat()
            }catch (exc : NumberFormatException){
                exc.printStackTrace()
            }
            when {
                rating <= 0F -> {
                    tvRatingIcon.visibility = View.GONE
                    tvNoRating.visibility = View.VISIBLE
                }
                rating > 3.9F -> {
                    tvRatingIcon.background = ResourcesCompat.getDrawable(view.resources,R.drawable.rating_backgroud_green,null)
                }
                rating > 0 -> {
                    tvRatingIcon.background = ResourcesCompat.getDrawable(view.resources,R.drawable.rating_backgroud_orange,null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item,parent,false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productWithImages[position])
    }

    override fun getItemCount(): Int {
        return productWithImages.size
    }

    fun interface OnClickProduct{
        fun onClick(product: Product)
    }
}