package com.practise.furn_land.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practise.furn_land.R
import com.practise.furn_land.databinding.FragmentFavoriteBinding
import com.practise.furn_land.databinding.LayoutLogInPromptBinding
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.ui.adapters.ProductListAdapter
import com.practise.furn_land.view_models.FavoriteViewModel
import com.practise.furn_land.view_models.UserViewModel

class FavoriteFragment : Fragment() {
    private val favoriteViewModel: FavoriteViewModel by lazy { ViewModelProvider(requireActivity())[FavoriteViewModel::class.java] }
    private val userViewModel: UserViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private var isUserLoggedIn = false
    private lateinit var bindingLogInPrompt: LayoutLogInPromptBinding
    private lateinit var bindingFavorite: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        favoriteViewModel.initUserFavorites(userViewModel.getLoggedInUser().toInt())
        isUserLoggedIn = userViewModel.isUserLoggedIn()
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        return if(isUserLoggedIn) {
            bindingFavorite = FragmentFavoriteBinding.inflate(layoutInflater)
            bindingFavorite.root
        }else{
            bindingLogInPrompt = LayoutLogInPromptBinding.inflate(layoutInflater)
            bindingLogInPrompt.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn){
            setUpFavorites()
        }else{
            setUpLogInPrompt()
        }
    }

    private fun setUpFavorites() {
        val rvFavorites = bindingFavorite.rvFavorites
        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        favoriteViewModel.getUserFavorites().observe(viewLifecycleOwner){ productList ->
            bindingFavorite.isListEmpty = productList.isEmpty()
            rvFavorites.adapter = ProductListAdapter(productList){
                findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToProductFragment(it.id))
            }
        }
    }

    private fun setUpLogInPrompt() {
        bindingLogInPrompt.btnToLogin.setOnClickListener {
            findNavController().navigate(FavoriteFragmentDirections.actionFavoriteFragmentToLogInFragment())
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }
}