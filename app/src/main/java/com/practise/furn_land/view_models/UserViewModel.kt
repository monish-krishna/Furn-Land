package com.practise.furn_land.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.furn_land.data.entities.SuggestionHistory
import com.practise.furn_land.data.entities.User
import com.practise.furn_land.data.models.Address
import com.practise.furn_land.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.practise.furn_land.data.models.Field
import com.practise.furn_land.data.models.Response
import com.practise.furn_land.data.models.UserProfileState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private var _isUserLoggedIn: Boolean = false
    private var _loggedInUser: Long = 0
    private var _fieldInfo = MutableLiveData<Field>()
    private var userProfileState = MutableLiveData<UserProfileState>()
    private var userHasAddress: Boolean = false
    private val isUserAddressUpdated = MutableLiveData<Boolean>()
    private var userAddress = MutableLiveData<Address>()
    private val _suggestions = MutableLiveData<List<SuggestionHistory>>()

    fun getFieldInfo(): LiveData<Field> = _fieldInfo

    fun clearFieldInfo(){
        _fieldInfo.value = null
    }

    fun isUserLoggedIn() = _isUserLoggedIn

    fun getLoggedInUser() = _loggedInUser

    fun getUserHasAddress() = userHasAddress

    fun setLoggedInUser(userId: Long){
        _loggedInUser = userId
        _isUserLoggedIn = true
        setUserState(userId)
        checkUserHasAddress(userId.toInt())
    }

    private fun checkUserHasAddress(userId: Int) {
        viewModelScope.launch {
            userHasAddress = userRepository.userHasAddress(userId)
        }
    }

    private fun setUserState(userId: Long) {
        viewModelScope.launch {
            val name = userRepository.getUsername(userId)
            val email = userRepository.getUserEmail(userId)
            val mobile = userRepository.getUserMobile(userId)
            userProfileState.value = UserProfileState(name, email, mobile)
        }
    }

    fun logOutUser(){
        _loggedInUser = 0
        _isUserLoggedIn = false
    }

    fun authenticateLogin(emailId: String, password: String) = viewModelScope.launch {
        if (isExistingEmail(emailId)) {
            if (validatePassword(emailId, password)) {
                val userId = userRepository.getUserId(emailId)
                _fieldInfo.value = Field.ALL.also { field ->
                    setLoggedInUser(userId)
                    field.response = Response.Success.also { it.message = "Log In Successful" }
                }
            } else {
                _fieldInfo.value = Field.ALL.also { field ->
                    field.response = Response.Failure.also { it.message = "Invalid credentials" }
                }
            }
        } else {
            _fieldInfo.value = Field.EMAIL.also { field ->
                field.response = Response.Failure.also { it.message = "Email Id doesn't exist" }
            }
        }
    }

    suspend fun isExistingEmail(emailId: String): Boolean = userRepository.isRegisteredUser(emailId)==1

    private suspend fun validatePassword(emailId: String, password: String): Boolean =
        userRepository.validatePassword(emailId, password)==1

    fun initiateSignUp(user: User)= viewModelScope.launch{
        if (isExistingEmail(user.emailId)) {
            _fieldInfo.value = Field.EMAIL.also { field ->
                field.response = Response.Failure.also { it.message = "Email Id already exists" }
            }
            return@launch
        }
        createUser(user)
        _fieldInfo.value = Field.ALL.also { it.response.message = "Sign up successful" }
    }

    private fun createUser(user: User){
        viewModelScope.launch { userRepository.createUser(user) }
    }

    fun getUserProfileState(): LiveData<UserProfileState> = userProfileState

    fun isUserAddressUpdated():LiveData<Boolean> = isUserAddressUpdated

    fun addAddress(address: Address) {
        viewModelScope.launch {
            userRepository.addAddress(getLoggedInUser().toInt(),address)
            userHasAddress = true
            isUserAddressUpdated.value = true
            initAddress()
        }
    }

    fun clearAddressAdded(){
        isUserAddressUpdated.value = false
    }

    fun initAddress(){
        viewModelScope.launch {
            userAddress.postValue(userRepository.getUserAddress(getLoggedInUser()))
        }
    }

    fun getAddress(): LiveData<Address> = userAddress

    fun getSuggestions(): LiveData<List<SuggestionHistory>> = _suggestions

    fun initSuggestions(){
        viewModelScope.launch {
            _suggestions.postValue(userRepository.getSuggestions(_loggedInUser.toInt()))
        }
    }

    fun updateSuggestions(searchString: String){
        viewModelScope.launch {
            _suggestions.postValue(userRepository.getSuggestions(getQueryAsList(searchString),_loggedInUser.toInt()))
        }
    }

    private fun getQueryAsList(query: String): List<String>{
        val list = query.split(" ",",",", "," ,")
        val searchQuery = ArrayList<String>()
        list.forEach { it.apply {
            searchQuery.add("%$it%")
        } }
        return searchQuery
    }
}