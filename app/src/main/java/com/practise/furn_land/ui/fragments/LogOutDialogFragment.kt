package com.practise.furn_land.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.practise.furn_land.R
import com.practise.furn_land.view_models.UserViewModel

class LogOutDialogFragment : DialogFragment() {

    private val userViewModel: UserViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.confirm_logout)
                .setPositiveButton(R.string.log_out) { _, _ ->
                    logOutUser()
                }
                .setNegativeButton(R.string.no) { _, _ ->
                    dismiss()
                }
                .setCancelable(false)
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun logOutUser() {
        val sharedPref = requireActivity().getSharedPreferences(getString(R.string.user_details), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.let { editor ->
            editor.putLong(getString(R.string.user_details),0 )
            editor.apply()
        }
        userViewModel.logOutUser()
        findNavController().popBackStack(R.id.homeFragment,false)
    }
}