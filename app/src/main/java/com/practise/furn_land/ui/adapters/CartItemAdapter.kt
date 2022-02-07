package com.practise.furn_land.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.Product
import com.practise.furn_land.data.entities.relations.CartWithProductAndImages
import com.practise.furn_land.data.entities.relations.ProductWithImages
import com.practise.furn_land.utils.isStrikeThrough
import kotlin.math.roundToInt

class CartItemAdapter(
    private val cartsWithProductAndImages: List<CartWithProductAndImages>,
    private val onClickProduct: ProductListAdapter.OnClickProduct,
    private val onClickQuantity: OnClickCartQuantity
):RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    inner class CartItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val tvProductName = view.findViewById<TextView>(R.id.tvItemProductName)
        private val tvBrandName = view.findViewById<TextView>(R.id.tvItemBrandName)
        private val tvRatingIcon = view.findViewById<TextView>(R.id.tvItemRatingIcon)
        private val tvCurrentPrice = view.findViewById<TextView>(R.id.tvItemCurrentPrice)
        private val tvOriginalPrice = view.findViewById<TextView>(R.id.tvItemOriginalPrice)
        private val tvOffer = view.findViewById<TextView>(R.id.tvItemOffer)
        private val tvNoRating = view.findViewById<TextView>(R.id.tvItemNoRating)
        private val ivProductImage = view.findViewById<ImageView>(R.id.ivProductImage)
        private val tvProductQuantity = view.findViewById<TextView>(R.id.tvQuantity)
        private val btnQuantityPlus = view.findViewById<ImageButton>(R.id.btnQuantityIncrease)
        private val btnQuantityMinus = view.findViewById<ImageButton>(R.id.btnQuantityDecrease)
        private val productClickView = view.findViewById<ConstraintLayout>(R.id.include)

        fun bindProduct(productWithImages: ProductWithImages) {
            val product = productWithImages.product
            val productImages = productWithImages.productImages
            var imageUrl = productImages[0].productImageUrl
            productClickView.setOnClickListener { onClickProduct.onClick(product) }
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

        fun bindProductQuantity(product: Product, quantity: Int) {
            tvProductQuantity.text = quantity.toString()
            if(quantity!=1) btnQuantityMinus.setImageDrawable(ResourcesCompat.getDrawable(btnQuantityMinus.resources,R.drawable.ic_minus,null))
            btnQuantityPlus.setOnClickListener {
                if (onClickQuantity.onClickPlus(
                        product,
                        tvProductQuantity.text.toString().toInt()
                    )
                ){
                    btnQuantityMinus.setImageDrawable(ResourcesCompat.getDrawable(btnQuantityMinus.resources,R.drawable.ic_minus,null))
                    val updatedValue = (tvProductQuantity.text.toString().toInt() + 1).toString()
                    tvProductQuantity.text = updatedValue
                }
            }
            btnQuantityMinus.setOnClickListener {
                val currentValue = tvProductQuantity.text.toString().toInt()
                if (onClickQuantity.onClickMinus(
                        product,
                        currentValue
                    )
                ) {
                    if (currentValue == 2){
                        btnQuantityMinus.setImageDrawable(ResourcesCompat.getDrawable(btnQuantityMinus.resources,R.drawable.ic_delete,null))
                    }
                    val updatedValue = (currentValue - 1).toString()
                    tvProductQuantity.text = updatedValue
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_cart_item,parent,false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bindProduct(cartsWithProductAndImages[position].productWithImages)
        holder.bindProductQuantity(cartsWithProductAndImages[position].productWithImages.product ,cartsWithProductAndImages[position].cart.quantity)
    }

    override fun getItemCount(): Int {
        return cartsWithProductAndImages.size
    }

    interface OnClickCartQuantity{
        fun onClickPlus(product: Product, quantity: Int): Boolean

        fun onClickMinus(product: Product, quantity: Int): Boolean
    }

}