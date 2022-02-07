package com.practise.furn_land.ui.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.practise.furn_land.R
import com.practise.furn_land.utils.safeNavigate
import com.practise.furn_land.view_models.ProductListViewModel
import com.practise.furn_land.view_models.ProductListViewModel.Companion.SEARCH_COMPLETED
import com.practise.furn_land.view_models.ProductListViewModel.Companion.SEARCH_INITIATED
import com.practise.furn_land.view_models.ProductListViewModel.Companion.SEARCH_NOT_INITIATED
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var ivSearchIllus: ImageView
    private lateinit var tvSearchInfo: TextView
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        productListViewModel = ViewModelProvider(requireActivity())[ProductListViewModel::class.java]

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivSearchIllus = view.findViewById(R.id.ivSearchIllustration)
        tvSearchInfo = view.findViewById(R.id.tvScreenInfo)

        searchView = view.findViewById(R.id.search)
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(getSearchQueryListener(view))
        searchView.setOnQueryTextFocusChangeListener{ _, hasFocus ->
            if(hasFocus){
                ivSearchIllus.setImageResource(R.drawable.image_search_illustration)
                tvSearchInfo.text = getString(R.string.start_your_furniture_hunt_here)
            }
        }
        requireActivity().invalidateOptionsMenu()
    }

    private fun getSearchQueryListener(view: View) = object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            hideInput()
            searchView.clearFocus()
            if (query == null) return false
            productListViewModel.setProductsWithImagesWithQuery(query)
            productListViewModel.getSearchStatus().observe(viewLifecycleOwner){ status ->
                when (status) {
                    SEARCH_COMPLETED -> {
                        val list = productListViewModel.getProductWithImagesList().value
                        if (list.isNullOrEmpty()) {
                            ivSearchIllus.setImageResource(R.drawable.no_results)
                            tvSearchInfo.text = getString(R.string.no_results)
                        } else if (list.isNotEmpty()) {
                            Navigation.findNavController(view).safeNavigate(SearchFragmentDirections.actionSearchFragmentToProductListFragment())
                        }
                    }
                    SEARCH_INITIATED -> {
                        Log.i("PSearchFragment", "status - SEARCH_INITIALISED")
                    }
                    SEARCH_NOT_INITIATED -> {
                        Log.i("PSearchFragment", "status - SEARCH_NOT_INITIALISED")
                    }
                }
            }
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }
    }

    fun hideInput(){
        val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fav_cart_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favoriteFragment -> {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToFavoriteFragment())
                true
            }
            R.id.cartFragment -> {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToCartFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        productListViewModel.setSearchStatus(SEARCH_NOT_INITIATED)
    }
}