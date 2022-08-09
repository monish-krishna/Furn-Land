package com.practise.furn_land.ui.fragments

import android.app.AlertDialog
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.SuggestionHistory
import com.practise.furn_land.ui.adapters.SuggestionAdapter
import com.practise.furn_land.utils.safeNavigate
import com.practise.furn_land.view_models.ProductListViewModel
import com.practise.furn_land.view_models.ProductListViewModel.Companion.SEARCH_COMPLETED
import com.practise.furn_land.view_models.ProductListViewModel.Companion.SEARCH_INITIATED
import com.practise.furn_land.view_models.ProductListViewModel.Companion.SEARCH_NOT_INITIATED
import com.practise.furn_land.view_models.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val productListViewModel: ProductListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private val userViewModel: UserViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private lateinit var ivSearchIllus: ImageView
    private lateinit var tvSearchInfo: TextView
    private lateinit var searchView: SearchView
    private lateinit var rvSuggestions: RecyclerView
    private var firstFocus: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivSearchIllus = view.findViewById(R.id.ivSearchIllustration)
        tvSearchInfo = view.findViewById(R.id.tvScreenInfo)
        rvSuggestions = view.findViewById(R.id.rvSuggestions)

        searchView = view.findViewById(R.id.search)
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(getSearchQueryListener(view))
        searchView.setOnQueryTextFocusChangeListener{ _, hasFocus ->
            if(hasFocus){
                initSuggestions()
                ivSearchIllus.setImageResource(R.drawable.image_search_illustration)
                tvSearchInfo.text = getString(R.string.start_your_furniture_hunt_here)
            }else{
                ivSearchIllus.visibility = View.VISIBLE
                tvSearchInfo.visibility = View.VISIBLE
                rvSuggestions.visibility = View.GONE
            }
        }
        rvSuggestions.layoutManager = LinearLayoutManager(requireContext())
        userViewModel.getSuggestions().observe(viewLifecycleOwner){ suggestions ->
            val distinctSuggestions = suggestions.distinctBy {
                it.suggestion
            }
            if (suggestions.isEmpty()) return@observe
            val mutableSuggestions = ArrayList<SuggestionHistory>(distinctSuggestions)
            rvSuggestions.adapter= SuggestionAdapter(mutableSuggestions.toMutableList(),getOnClickSuggestion())
        }
        requireActivity().invalidateOptionsMenu()
    }

    private fun initSuggestions() {
        if (firstFocus) {
            userViewModel.initSuggestions()
            firstFocus = false
        }
        ivSearchIllus.visibility = View.GONE
        tvSearchInfo.visibility = View.GONE
        rvSuggestions.visibility = View.VISIBLE
    }

    private fun getOnClickSuggestion() = object : SuggestionAdapter.OnClickSuggestion{
        override fun onClick(suggestion: SuggestionHistory) {
            searchView.setQuery(suggestion.suggestion,true)
        }

        override fun onClickRemove(suggestionHistory: SuggestionHistory, position: Int) {
            AlertDialog.Builder(requireContext())
                .setTitle(suggestionHistory.suggestion)
                .setMessage(R.string.remove_from_history)
                .setPositiveButton(R.string.remove){_,_ ->
                    userViewModel.removeSuggestion(suggestionHistory)
                    (rvSuggestions.adapter as? SuggestionAdapter)?.removeSuggestion(position)
                }
                .setNegativeButton(getString(R.string.cancel)){_,_ ->}
                .create()
                .show()
        }
    }

    private fun getSearchQueryListener(view: View) = object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            rvSuggestions.visibility = View.GONE
            hideInput()
            searchView.clearFocus()
            firstFocus = true
            if (query == null) return false
            productListViewModel.setProductsWithImagesWithQuery(query).also {
                userViewModel.insertSuggestion(query)
            }
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
            rvSuggestions.visibility=View.VISIBLE
            newText?.let {
                userViewModel.updateSuggestions(it)
            }
            if (newText.isNullOrEmpty()) userViewModel.initSuggestions()
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        rvSuggestions.visibility = View.GONE
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