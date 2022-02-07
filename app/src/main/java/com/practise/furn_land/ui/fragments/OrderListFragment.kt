package com.practise.furn_land.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.Order
import com.practise.furn_land.databinding.FragmentOrderListBinding
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.ui.adapters.OrderedProductsAdapter
import com.practise.furn_land.view_models.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

@AndroidEntryPoint
class OrderListFragment : Fragment() {
    private val navArgs by navArgs<OrderListFragmentArgs>()
    private lateinit var binding : FragmentOrderListBinding
    private val orderViewModel: OrderViewModel by lazy { ViewModelProvider(this)[OrderViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = FragmentOrderListBinding.inflate(inflater)
        orderViewModel.initOrderList(navArgs.orderId)
        Log.i(TAG,"OrderListFragment orderId - ${navArgs.orderId}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderViewModel.getOrderList().observe(viewLifecycleOwner){
            binding.rvOrderedProductList.adapter = OrderedProductsAdapter(it){ product ->
                findNavController().navigate(OrderListFragmentDirections.actionOrderListFragmentToProductFragment(product.id))
            }
        }
        orderViewModel.getOrder().observe(viewLifecycleOwner){
            it?.let { order ->
                setUpViews(order)
            }
        }
        binding.rvOrderedProductList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpViews(order: Order) {
        val date = SimpleDateFormat.getDateTimeInstance().parse(order.orderDateTime)
        val dateFormatter = SimpleDateFormat("dd-MM-yy")
        val timeFormatter = SimpleDateFormat("h:mm a")
        val dateString = dateFormatter.format(date)
        val timeString = timeFormatter.format(date)
        binding.tvOrderDate.text = dateString
        binding.tvOrderTime.text = timeString
        val priceString = requireView().resources.getString(R.string.rupee_symbol) + " ${order.totalPrice.roundToInt()}"
        binding.tvTotalPrice.text = priceString
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }

    companion object{
        const val TAG = "OrderListIssue"
    }
}