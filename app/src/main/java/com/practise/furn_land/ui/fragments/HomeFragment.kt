package com.practise.furn_land.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practise.furn_land.R
import com.practise.furn_land.ui.adapters.CategoryHomeAdapter
import com.practise.furn_land.view_models.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var mProductListViewModel: ProductListViewModel
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById(R.id.rvCategories)

        mProductListViewModel = ViewModelProvider(requireActivity())[ProductListViewModel::class.java]
        mProductListViewModel.getCategories().observe(viewLifecycleOwner){
            val categoryAdapter = CategoryHomeAdapter(it) { category ->
                mProductListViewModel.setProductListTitle(category.name)
                val action = HomeFragmentDirections.actionHomeFragmentToProductListFragment(category.id,category.name)
                findNavController().navigate(action)
            }
            mRecyclerView.adapter = categoryAdapter
        }
        mRecyclerView.layoutManager = GridLayoutManager(activity,3)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fav_cart_menu,menu)
        true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favoriteFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFavoriteFragment())
                true
            }
            R.id.cartFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCartFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object{
        private const val TAG = "HomeFragment"
    }
}