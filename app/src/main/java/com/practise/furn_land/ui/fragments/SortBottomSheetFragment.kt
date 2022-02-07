package com.practise.furn_land.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practise.furn_land.R
import com.practise.furn_land.data.models.SortType.*
import com.practise.furn_land.view_models.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var mProductListViewModel: ProductListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mProductListViewModel = ViewModelProvider(requireActivity())[ProductListViewModel::class.java]
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sort_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = view.findViewById<RadioGroup>(R.id.rgSort)
        Log.i("ProdBottomSheetSort","sortType = ${mProductListViewModel.getSortType()}")
        mProductListViewModel.getSortType()?.let { sortType ->
            when(sortType){
                PRICE_LOW_TO_HIGH -> radioGroup.check(R.id.rbPriceLowToHigh)
                PRICE_HIGH_TO_LOW -> radioGroup.check(R.id.rbPriceHighToLow)
                RATING_LOW_TO_HIGH -> radioGroup.check(R.id.rbRatingLowToHigh)
                RATING_HIGH_TO_LOW -> radioGroup.check(R.id.rbRatingHighToLow)
            }
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            Log.i("ProdBottomSheetSort","checkedId - $checkedId")
            when(checkedId){
                R.id.rbPriceHighToLow -> mProductListViewModel.sortProductList(PRICE_HIGH_TO_LOW)
                R.id.rbPriceLowToHigh -> mProductListViewModel.sortProductList(PRICE_LOW_TO_HIGH)
                R.id.rbRatingHighToLow -> mProductListViewModel.sortProductList(RATING_HIGH_TO_LOW)
                R.id.rbRatingLowToHigh -> mProductListViewModel.sortProductList(RATING_LOW_TO_HIGH)
            }
            dismiss()
        }
    }
}