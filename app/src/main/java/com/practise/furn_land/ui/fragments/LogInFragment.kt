package com.practise.furn_land.ui.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputLayout
import com.practise.furn_land.R
import com.practise.furn_land.data.models.Field
import com.practise.furn_land.data.models.Response
import com.practise.furn_land.databinding.FragmentLogInBinding
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.view_models.UserViewModel

class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLogInBinding
    private lateinit var userViewModel: UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        binding = FragmentLogInBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.getFieldInfo().observe(viewLifecycleOwner){ field ->
            field?.let { it ->
                deliverFieldMessage(it)
                if (field.response == Response.Success) {
                    val sharedPref = activity?.getSharedPreferences(getString(R.string.user_details), Context.MODE_PRIVATE)
                    val editor = sharedPref?.edit()
                    editor?.let { editor ->
                        editor.putLong(getString(R.string.user_details),userViewModel.getLoggedInUser() )
                        editor.apply()
                    }
                    Navigation.findNavController(view).popBackStack(R.id.logInFragment,true)
                }
            }
        }
        binding.toSignUp.setOnClickListener {
            userViewModel.clearFieldInfo()
            cancelErrors()
            Navigation.findNavController(view).navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
        }
        binding.btnLogIn.setOnClickListener {
            val email = binding.tiEtEmail.text.toString()
            val password = binding.tiEtPassword.text.toString()
            if(inputCheck(email,password)){
                userViewModel.authenticateLogin(email,password)
            }else{
                if(email.isEmpty()) binding.tiLtEmail.error = getString(R.string.field_cant_be_empty)
                if(password.isEmpty()) binding.tiLtPassword.error = getString(R.string.field_cant_be_empty)
            }
        }
        binding.tiEtPassword.setOnFocusChangeListener { _, hasFocus ->
            setStartIconColor(binding.tiLtPassword,hasFocus)
            if(hasFocus) binding.tiLtPassword.error = null
        }
        binding.tiEtEmail.setOnFocusChangeListener { _, hasFocus ->
            setStartIconColor(binding.tiLtEmail,hasFocus)
            if(hasFocus) binding.tiLtEmail.error = null
        }
        binding.tiEtPassword.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when(actionId){
                EditorInfo.IME_ACTION_DONE ->{
                    v.clearFocus()
                    hideInput(v)
                    true
                }
                else -> false
            }
        }
    }

    private fun inputCheck(email: String, password: String): Boolean
        = email.isNotEmpty()||password.isNotEmpty()

    private fun deliverFieldMessage(field: Field) {
        when(field){
            Field.EMAIL -> binding.tiLtEmail.error = field.response.message
            Field.PASSWORD -> binding.tiLtPassword.error = field.response.message
            else -> {
                Toast.makeText(requireContext(), field.response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelErrors() {
        binding.tiLtPassword.error = null
        binding.tiLtEmail.error = null
    }

    private fun hideInput(view: View){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun setStartIconColor(textInputLayout: TextInputLayout, hasFocus: Boolean){
        val colorFocussed = ResourcesCompat.getColor(resources, R.color.customColorFontPrimary,null)
        val colorNonFocussed = ResourcesCompat.getColor(resources, R.color.fieldStrokeColor,null)
        val color = if(hasFocus) colorFocussed else colorNonFocussed
        textInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clearFieldInfo()
    }
}