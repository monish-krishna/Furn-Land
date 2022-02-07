package com.practise.furn_land.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.practise.furn_land.R

class LogInDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.layout_not_logged_in)
                .setPositiveButton(R.string.log_in) { _, _ ->
                    findNavController().navigate(LogInDialogFragmentDirections.actionLogInDialogFragmentToLogInFragment())
                }
                .setNegativeButton(R.string.not_now) { _, _ ->
                    dismiss()
                }
                .setCancelable(false)
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}