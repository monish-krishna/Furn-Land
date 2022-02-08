package com.practise.furn_land.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.practise.furn_land.R
import com.practise.furn_land.data.models.Address
import com.practise.furn_land.databinding.FragmentNewAddressBinding
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.view_models.UserViewModel


class NewAddressFragment : Fragment() {
    private lateinit var binding: FragmentNewAddressBinding
    private val userViewModel: UserViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = FragmentNewAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFocusChangeListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_done,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_done -> {
                cancelErrors()
                if(checkInput()){
                    addUserAddress()
                    findNavController().navigateUp()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val activity = requireActivity() as HomeActivity
        activity.setBackButtonAs(R.drawable.ic_baseline_close_24)
        activity.setActionBarTitle(navArgs<NewAddressFragmentArgs>().value.title)
    }

    private fun checkInput(): Boolean = fieldsNotEmpty() && pincodeCheck()

    private fun pincodeCheck(): Boolean{
        val pincode = binding.tiEtPincode.text.toString()
        var isValidPincode = pincode.isDigitsOnly() || pincode.length == 6
        if(!isValidPincode){
            binding.tiLtPincode.error = getString(R.string.not_a_valid_pincode)
        }
        if(isValidPincode){
            val isValidForTN = pincode.toInt() > 599_999
            if(!isValidForTN) binding.tiLtPincode.error = getString(R.string.not_a_valid_pincode)
            isValidPincode = isValidPincode && isValidForTN
        }
        return isValidPincode
    }

    private fun fieldsNotEmpty(): Boolean{
        val door = binding.tiEtDoorOrApartment.text
        val landMark = binding.tiEtLandMark.text
        val city = binding.tiEtCity.text
        val pincode = binding.tiEtPincode.text
        val emptyInfo = getString(R.string.field_cant_be_empty)
        if (door.isNullOrBlank()) {
            binding.tiLtDoorOrApartment.error = emptyInfo
        }
        if (landMark.isNullOrBlank()) {
            binding.tiLtLandmark.error = emptyInfo
        }
        if (city.isNullOrBlank()) {
            binding.tiLtCity.error = emptyInfo
        }
        if (pincode.isNullOrBlank()) {
            binding.tiLtPincode.error = emptyInfo
        }
        return (!door.isNullOrEmpty() &&
                !landMark.isNullOrEmpty() &&
                !city.isNullOrEmpty() &&
                !pincode.isNullOrEmpty())
    }

    private fun addUserAddress() {
        val addressLine1 = binding.tiEtDoorOrApartment.text.toString()+ ", "+binding.tiEtLandMark.text.toString()
        val addressLine2 = binding.tiEtCity.text.toString()
        val pincode = binding.tiEtPincode.text.toString().toInt()
        val address = Address(addressLine1, addressLine2, pincode)
        userViewModel.addAddress(address)
    }

    private fun cancelErrors() {
        binding.tiLtDoorOrApartment.error = null
        binding.tiLtLandmark.error = null
        binding.tiLtCity.error = null
        binding.tiLtPincode.error = null
    }

    private fun setUpFocusChangeListeners(){
        binding.tiEtDoorOrApartment.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtDoorOrApartment.error = null
        }
        binding.tiEtLandMark.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtLandmark.error = null
        }
        binding.tiEtCity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtCity.error = null
        }
        binding.tiEtPincode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtPincode.error = null
        }
    }
}