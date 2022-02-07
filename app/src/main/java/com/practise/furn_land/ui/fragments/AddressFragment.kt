package com.practise.furn_land.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.practise.furn_land.R
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.view_models.UserViewModel

class AddressFragment : Fragment() {
    private val userViewModel: UserViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private lateinit var tvAddress: TextView
    private lateinit var btnChangeAddress: Button
    private var userHasAddress = false
    private var isNewAddress: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        userHasAddress = userViewModel.getUserHasAddress()
        if (userHasAddress) {
            userViewModel.initAddress()
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvAddress = view.findViewById(R.id.tvAddress)
        btnChangeAddress = view.findViewById(R.id.btnChangeAddress)
        if (userHasAddress){
            isNewAddress = true
            setUpAddressObserver()
        }else{
            isNewAddress = false
            tvAddress.text = getString(R.string.no_address_added)
            btnChangeAddress.text = getString(R.string.add_address)
        }
        setButtonChangeAddressListener()
        setUpAddressUpdatedObserver()
    }

    private fun setUpAddressUpdatedObserver() {
        userViewModel.isUserAddressUpdated().observe(viewLifecycleOwner){
            isNewAddress = true
            setUpAddressObserver()
        }
    }

    private fun setUpAddressObserver(){
        userViewModel.getAddress().observe(viewLifecycleOwner){
            val addressString = it.addressLine1+",\n"+it.addressLine2+",\n"+"Pincode: ${it.pincode}."
            tvAddress.text = addressString
            btnChangeAddress.text = getString(R.string.change_address)
        }
    }

    private fun setButtonChangeAddressListener(){
        btnChangeAddress.setOnClickListener {
            val addressChangeTitle = if (isNewAddress) "New Address" else "Address"
            findNavController().navigate(AddressFragmentDirections.actionAddressBottomSheetDialogFragmentToNewAddressDialogFragment(addressChangeTitle))
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }
}