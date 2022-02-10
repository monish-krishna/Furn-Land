package com.practise.furn_land.ui.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputLayout
import com.practise.furn_land.R
import com.practise.furn_land.view_models.UserViewModel
import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.practise.furn_land.data.entities.User
import com.practise.furn_land.data.models.Field
import com.practise.furn_land.data.models.Field.*
import com.practise.furn_land.data.models.Response
import com.practise.furn_land.databinding.FragmentSignUpBinding
import com.practise.furn_land.ui.activity.HomeActivity

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFocusChangeListeners()
        binding.btnSignUp.setOnClickListener {
            userViewModel.clearFieldInfo()
            clearFormFocuses()
            initiateSignUp()
        }
        binding.tiEtConfirmPassword.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    v.clearFocus()
                    hideInput(v)
                    true
                }
                else -> false
            }
        }
    }

    private fun clearFormFocuses() {
        binding.tiEtEmail.clearFocus()
        binding.tiEtMobile.clearFocus()
        binding.tiEtName.clearFocus()
        binding.tiEtPassword.clearFocus()
        binding.tiEtConfirmPassword.clearFocus()
    }

    private fun initiateSignUp() {
        userViewModel.getFieldInfo().observe(viewLifecycleOwner){ field ->
            field?.let {
                deliverFieldMessage(it)
                if (field.response == Response.Success){
                    val sharedPref = activity?.getSharedPreferences(getString(R.string.user_details), Context.MODE_PRIVATE)
                    val editor = sharedPref?.edit()
                    editor?.let { editor ->
                        editor.putLong(getString(R.string.user_details),userViewModel.getLoggedInUser() )
                        editor.apply()
                    }
                    userViewModel.clearFieldInfo()
                    Navigation.findNavController(requireView()).popBackStack(R.id.logInFragment,true)
                }
            }
        }
        cancelErrors()
        if(inputCheck()){
            val name = binding.tiEtName.text.toString()
            val email = binding.tiEtEmail.text.toString()
            val mobile = binding.tiEtMobile.text.toString()
            val password = binding.tiEtPassword.text.toString()
            val user = User(0,name,email,password,mobile,null)
            Log.i(TAG,"$user")
            userViewModel.initiateSignUp(user)
        }
    }

    private fun deliverFieldMessage(field: Field) {
        when(field){
            EMAIL -> binding.tiLtEmail.error = field.response.message
            ALL -> {
                Toast.makeText(requireContext(), field.response.message,Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(requireContext(), field.response.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelErrors() {
        binding.tiLtName.error = null
        binding.tiLtEmail.error = null
        binding.tiLtMobile.error = null
        binding.tiLtPassword.error = null
        binding.tiLtConfirmPassword.error = null
    }

    private fun inputCheck(): Boolean {
        return isFieldsNotEmpty() && emailCheck() && mobileCheck() && passwordCheck()
    }

    private fun passwordCheck(): Boolean {
        val password = binding.tiEtPassword.text.toString()
        val confirmPassword = binding.tiEtConfirmPassword.text.toString()
        if (password != confirmPassword){
            val passwordInfo = getString(R.string.password_doesnt_match)
            binding.tiLtPassword.error = passwordInfo
            binding.tiLtConfirmPassword.error = passwordInfo
            return false
        }
        if(password.length<8){
            val passwordInfo = getString(R.string.password_min_character)
            binding.tiLtPassword.error = passwordInfo
            binding.tiLtConfirmPassword.error = passwordInfo
            return false
        }
        return true
    }

    private fun mobileCheck(): Boolean {
        val mobile = binding.tiEtMobile.text.toString()
        Log.i("SignUp","mobile($mobile) isDigitsOnly() - ${mobile.isDigitsOnly()} length - ${mobile.length} in mobileCheck()")
        if ( !mobile.isDigitsOnly() || mobile.length!=10 ){
            val mobileInfo = getString(R.string.not_a_mobile)
            binding.tiLtMobile.error = mobileInfo
            Log.i(TAG,"mobile($mobile) not passed in mobileCheck()")
            return false
        }
        return true
    }

    private fun emailCheck(): Boolean {
        val email = binding.tiEtEmail.text
        val isEmailPattern = EMAIL_ADDRESS.matcher(email).matches()
        if (!isEmailPattern) {
            val emailInfo = getString(R.string.not_an_email)
            binding.tiLtEmail.error = emailInfo
            Log.i(TAG,"Email($email) not in pattern in emailCheck()")
        }
        return isEmailPattern
    }

    private fun isFieldsNotEmpty(): Boolean {
        val name = binding.tiEtName.text
        val email = binding.tiEtEmail.text
        val mobile = binding.tiEtMobile.text
        val password = binding.tiEtPassword.text
        val confirmPassword = binding.tiEtConfirmPassword.text
        val emptyInfo = getString(R.string.field_cant_be_empty)
        if(name.isNullOrEmpty()) {
            binding.tiLtName.error = emptyInfo
            Log.i(TAG,"Name($name) is Empty in isFieldNotEmpty()")
        }
        if(email.isNullOrEmpty()) {
            binding.tiLtEmail.error = emptyInfo
            Log.i(TAG,"Email($email) is Empty in isFieldNotEmpty()")
        }
        if(mobile.isNullOrEmpty()) {
            binding.tiLtMobile.error = emptyInfo
            Log.i(TAG,"Mobile($mobile) is Empty in isFieldNotEmpty()")
        }
        if(password.isNullOrEmpty()) {
            binding.tiLtPassword.error = emptyInfo
            Log.i(TAG,"Password($password) is Empty in isFieldNotEmpty()")
        }
        if(confirmPassword.isNullOrEmpty()) {
            binding.tiLtConfirmPassword.error = emptyInfo
            Log.i(TAG,"ConfirmPassword($password) is Empty in isFieldNotEmpty()")
        }
        return (!name.isNullOrEmpty() &&
                !email.isNullOrEmpty() &&
                !mobile.isNullOrEmpty() &&
                !password.isNullOrEmpty() &&
                !confirmPassword.isNullOrEmpty())
    }

    private fun hideInput(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun setStartIconColor(textInputLayout: TextInputLayout, hasFocus: Boolean) {
        val colorFocussed =
            ResourcesCompat.getColor(resources, R.color.customColorFontPrimary, null)
        val colorNonFocussed = ResourcesCompat.getColor(resources, R.color.fieldStrokeColor, null)
        val color = if (hasFocus) colorFocussed else colorNonFocussed
        textInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }

    private fun setUpFocusChangeListeners(){
        binding.tiEtName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtName.error = null
            setStartIconColor(
                binding.tiLtName,
                hasFocus
            )
        }
        binding.tiEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtEmail.error = null
            setStartIconColor(
                binding.tiLtEmail,
                hasFocus
            )
        }
        binding.tiEtMobile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtMobile.error = null
            setStartIconColor(
                binding.tiLtMobile,
                hasFocus
            )
        }
        binding.tiEtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtPassword.error = null
            setStartIconColor(
                binding.tiLtPassword,
                hasFocus
            )
        }
        binding.tiEtConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tiLtConfirmPassword.error = null
            setStartIconColor(
                binding.tiLtConfirmPassword,
                hasFocus
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clearFieldInfo()
    }

    companion object{
        private const val TAG = "SignUp"
    }
}