package com.practise.furn_land.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practise.furn_land.R
import com.practise.furn_land.databinding.FragmentOrdersBinding
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.ui.adapters.OrderAdapter
import com.practise.furn_land.view_models.OrderViewModel
import com.practise.furn_land.view_models.UserViewModel

class OrdersFragment : Fragment() {
    private val userViewModel: UserViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val orderViewModel: OrderViewModel by lazy { ViewModelProvider(requireActivity())[OrderViewModel::class.java] }
    private lateinit var binding : FragmentOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        orderViewModel.initOrders(userViewModel.getLoggedInUser().toInt())
        orderViewModel.getOrders().observe(viewLifecycleOwner){ orders ->
            binding.isListEmpty = orders.isEmpty()
            binding.rvOrders.adapter = OrderAdapter(orders){ order ->
                findNavController().navigate(OrdersFragmentDirections.actionOrdersFragmentToOrderListFragment(order.id))
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }
}