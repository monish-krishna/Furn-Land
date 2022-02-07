package com.practise.furn_land.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.practise.furn_land.R

class OrderConfirmedDialogFragment : DialogFragment() {
    private val navArgs by navArgs<OrderConfirmedDialogFragmentArgs>()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.fragment_order_confirmed_dialog)
                .setPositiveButton(R.string.okay) { _, _ ->
                    findNavController().popBackStack(R.id.homeFragment,false)
                }
                .setNegativeButton(R.string.view_order) { _, _ ->
                    findNavController().navigate(OrderConfirmedDialogFragmentDirections.actionOrderConfirmedDialogFragmentToOrderListFragment(navArgs.orderId))
                }
                .setCancelable(false)
            // Create the AlertDialog object and return it
            builder.create()
        }
    }
}