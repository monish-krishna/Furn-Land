package com.practise.furn_land.ui.adapters

import android.os.Build
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
import com.practise.furn_land.data.entities.relations.OrderListWithProductAndImages
import com.practise.furn_land.data.entities.relations.ProductWithImages
import com.practise.furn_land.utils.isStrikeThrough
import kotlin.math.roundToInt

class OrderedProductsAdapter(
    private val orderListWithProductAndImages: List<OrderListWithProductAndImages>,
    private val onClickOrderedProduct: OnClickOrderedProduct
): RecyclerView.Adapter<OrderedProductsAdapter.OrderedProductViewHolder>() {

    inner class OrderedProductViewHolder(val view: View): RecyclerView.ViewHolder(view){
        private val tvProductName = view.findViewById<TextView>(R.id.tvItemProductName)
        private val tvBrandName = view.findViewById<TextView>(R.id.tvItemBrandName)
        private val tvRatingIcon = view.findViewById<TextView>(R.id.tvItemRatingIcon)
        private val tvCurrentPrice = view.findViewById<TextView>(R.id.tvItemCurrentPrice)
        private val tvOriginalPrice = view.findViewById<TextView>(R.id.tvItemOriginalPrice)
        private val tvOffer = view.findViewById<TextView>(R.id.tvItemOffer)
        private val tvNoRating = view.findViewById<TextView>(R.id.tvItemNoRating)
        private val ivProductImage = view.findViewById<ImageView>(R.id.ivProductImage)
        private val tvProductQuantity = view.findViewById<TextView>(R.id.tvProductQuantity)

        fun bindOrderedProduct(productWithImages: ProductWithImages){
            val product = productWithImages.product
            val productImages = productWithImages.productImages
            var imageUrl = productImages[0].productImageUrl
            view.setOnClickListener { onClickOrderedProduct.onClick(product) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.foreground = ResourcesCompat.getDrawable(view.resources,R.drawable.ripple_bg,null)
            }
            productImages.forEach { productImage ->
                if (productImage.productImageIndex == 1) imageUrl = productImage.productImageUrl
                else return@forEach
            }
            tvProductName.text = product.name
            tvBrandName.text = product.brandName
            tvRatingIcon.text = product.totalRating.toString()
            tvOriginalPrice.isStrikeThrough(true)
            ivProductImage.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_image_load)
            }
            setUpRatingIcon()
            setUpPrices(product)
        }

        private fun setUpPrices(product: Product) {
            val currentPriceText =
                view.resources.getString(R.string.rs_symbol) + product.currentPrice.roundToInt()
            tvCurrentPrice.text = currentPriceText
            if (product.originalPrice > product.currentPrice) {
                val originalPriceText =
                    view.resources.getString(R.string.rs_symbol) + product.originalPrice.roundToInt()
                tvOriginalPrice.text = originalPriceText
                val offerPrice =
                    (product.originalPrice - product.currentPrice) / product.originalPrice
                val offer = offerPrice * 100
                if (offer > 1) {
                    val offerText = offer.roundToInt().toString() + "% Off"
                    tvOffer.text = offerText
                } else {
                    tvOffer.visibility = View.GONE
                }
            } else {
                tvOriginalPrice.visibility = View.GONE
                tvOffer.visibility = View.GONE
            }
        }

        private fun setUpRatingIcon() {
            var rating = 0F
            try {
                rating = tvRatingIcon.text.toString().toFloat()
            } catch (exc: NumberFormatException) {
                exc.printStackTrace()
            }
            when {
                rating <= 0F -> {
                    tvRatingIcon.visibility = View.GONE
                    tvNoRating.visibility = View.VISIBLE
                }
                rating > 3.9F -> {
                    tvRatingIcon.background = ResourcesCompat.getDrawable(
                        view.resources,
                        R.drawable.rating_backgroud_green,
                        null
                    )
                }
                rating > 0 -> {
                    tvRatingIcon.background = ResourcesCompat.getDrawable(
                        view.resources,
                        R.drawable.rating_backgroud_orange,
                        null
                    )
                }
            }
        }

        fun bindQuantity(quantity: Int){
            tvProductQuantity.text = quantity.toString()
        }
    }

    fun interface OnClickOrderedProduct{
        fun onClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_ordered_product_item,parent,false)
        return OrderedProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderedProductViewHolder, position: Int) {
        holder.bindOrderedProduct(orderListWithProductAndImages[position].productWithImages)
        holder.bindQuantity(orderListWithProductAndImages[position].orderList.quantity)
    }

    override fun getItemCount(): Int {
        return orderListWithProductAndImages.size
    }
}