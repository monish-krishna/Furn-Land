package com.practise.furn_land.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.Order
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class OrderAdapter(
    private val orders: List<Order>,
    private val onClickListener: OnClickOrder
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(){

    inner class OrderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val tvOrderDate = view.findViewById<TextView>(R.id.tvOrderDate)
        private val tvOrderTime = view.findViewById<TextView>(R.id.tvOrderTime)
        private val tvNoOfProducts = view.findViewById<TextView>(R.id.tvNoOfProducts)
        private val tvTotalPrice = view.findViewById<TextView>(R.id.tvTotalPrice)

        fun bind(order: Order) {
            val date = SimpleDateFormat.getDateTimeInstance().parse(order.orderDateTime)
            val dateFormatter = SimpleDateFormat("dd-MM-yy")
            val timeFormatter = SimpleDateFormat("h:mm a")
            val dateString = dateFormatter.format(date)
            val timeString = timeFormatter.format(date)
            view.setOnClickListener {
                onClickListener.onClick(order)
            }
            tvOrderDate.text = dateString
            tvOrderTime.text = timeString
            tvNoOfProducts.text = order.productCount.toString()
            val priceString = view.resources.getString(R.string.rupee_symbol) + " ${order.totalPrice.roundToInt()}"
            tvTotalPrice.text = priceString
        }
    }

    fun interface OnClickOrder {
        fun onClick(order: Order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_item,parent,false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}