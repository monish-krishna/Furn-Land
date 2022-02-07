package com.practise.furn_land.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.practise.furn_land.R
import com.practise.furn_land.data.models.UserProfileState
import com.practise.furn_land.databinding.FragmentProfileBinding
import com.practise.furn_land.databinding.LayoutLogInPromptBinding
import com.practise.furn_land.view_models.UserViewModel

class ProfileFragment : Fragment() {
    private val userViewModel: UserViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private var isUserLoggedIn = false
    private lateinit var bindingLogInPrompt: LayoutLogInPromptBinding
    private lateinit var bindingProfile: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        isUserLoggedIn = userViewModel.getLoggedInUser() != 0L
        return if(isUserLoggedIn) {
            bindingProfile = FragmentProfileBinding.inflate(layoutInflater)
            bindingProfile.root
        }else{
            bindingLogInPrompt = LayoutLogInPromptBinding.inflate(layoutInflater)
            bindingLogInPrompt.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn){
            setUpProfile()
        }else{
            setUpLogInPrompt()
        }
    }

    private fun setUpLogInPrompt() {
        bindingLogInPrompt.btnToLogin.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(ProfileFragmentDirections.actionProfileFragmentToLogInFragment())
        }
    }

    private fun setUpProfile() {
        bindingProfile.myOrders.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToOrdersFragment(userViewModel.getLoggedInUser().toInt()))
        }
        bindingProfile.myCart.setOnClickListener{
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToCartFragment())
        }
        bindingProfile.myFavorites.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFavoriteFragment())
        }
        bindingProfile.myAddress.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAddressBottomSheetDialogFragment())
        }
        userViewModel.getUserProfileState().observe(viewLifecycleOwner){
            setUpUserDetails(it)
        }
    }

    private fun setUpUserDetails(userProfileState: UserProfileState) {
        bindingProfile.tvUserName.text = userProfileState.name
        bindingProfile.tvEmail.text = userProfileState.email
        bindingProfile.tvMobileNumber.text = userProfileState.mobile
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isUserLoggedIn) inflater.inflate(R.menu.menu_log_out,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.mi_logout -> {
                initiateLogOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initiateLogOut(): Boolean {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLogOutDialogFragment())
        return false
    }

}