package com.practise.furn_land.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.relations.ProductWithImages
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.ui.adapters.ProductListAdapter
import com.practise.furn_land.view_models.ProductListViewModel

class ProductListFragment : Fragment() {

    private val navArgs by navArgs<ProductListFragmentArgs>()
    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var rvProducts: RecyclerView
    private lateinit var btnSort: Button
    private lateinit var tvResultCount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG,"onCreate() called")
        setHasOptionsMenu(true)
        productListViewModel = ViewModelProvider(requireActivity())[ProductListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG,"onCreateView() called")
        productListViewModel.setCategoryId(navArgs.categoryId)
        // Inflate the layout for this fragment

        (requireActivity() as HomeActivity).invalidateOptionsMenu()
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG,"onViewCreated() called")
        btnSort = view.findViewById(R.id.btnSort)
        tvResultCount = view.findViewById(R.id.tvResultCount)
        rvProducts = view.findViewById(R.id.rvProductsList)

        if (productListViewModel.getCategoryId() > 0){
            productListViewModel.setProductWithImagesList(navArgs.categoryId)
        }else if(productListViewModel.getProductWithImagesList().value.isNullOrEmpty()){
            productListViewModel.setProductsWithImagesWithQuery(productListViewModel.getProductListTitle())
        }
        productListViewModel.getProductWithImagesList().observe(viewLifecycleOwner){ productWithImagesList ->
            val resultText = productWithImagesList.size.toString() + " products"
            tvResultCount.text = resultText
            val mutableProductWithImagesList = ArrayList<ProductWithImages>(productWithImagesList)
            rvProducts.adapter = ProductListAdapter(mutableProductWithImagesList.toMutableList()){ product ->
                Navigation.findNavController(requireView()).navigate(ProductListFragmentDirections.actionProductListFragmentToProductFragment(product.id))
            }
        }

        rvProducts.layoutManager = LinearLayoutManager(view.context)
        btnSort.setOnClickListener {
            Navigation.findNavController(view).navigate(ProductListFragmentDirections.actionProductListFragmentToSortBottomSheetFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fav_cart_menu,menu)
        true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favoriteFragment -> {
                findNavController().navigate(ProductListFragmentDirections.actionProductListFragmentToFavoriteFragment())
                true
            }
            R.id.cartFragment -> {
                findNavController().navigate(ProductListFragmentDirections.actionProductListFragmentToCartFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Log.i(TAG,"onPrepareOptionsMenu() called")
        setActionBarTitle()
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }

    fun setActionBarTitle(){
        val activity = requireActivity() as HomeActivity
        activity.setActionBarTitle(productListViewModel.getProductListTitle())
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG,"onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG,"onPause() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"onDestroy() called")
        productListViewModel.emptyProductList()
        productListViewModel.clearSortType()
    }

    companion object{
        private const val TAG = "ProductListFragment"
    }
}