package com.practise.furn_land.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.furn_land.data.entities.Favorite
import com.practise.furn_land.data.entities.relations.ProductWithImages
import com.practise.furn_land.data.repository.FurnitureRepository
import com.practise.furn_land.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val furnitureRepository: FurnitureRepository
): ViewModel() {

    private val _userFavorites = MutableLiveData<List<ProductWithImages>>()

    fun getUserFavorites(): LiveData<List<ProductWithImages>> = _userFavorites

    suspend fun isUserFavorite(productId: Int, userId: Int): Boolean = userRepository.isUserFavorite(productId, userId) == 1

    fun initUserFavorites(userId: Int){
        viewModelScope.launch {
            val productIds = userRepository.getUserFavorites(userId)
            _userFavorites.postValue(furnitureRepository.getProductWithImagesByProductIds(productIds))
        }
    }

    fun insertFavorite(favorite: Favorite){
        viewModelScope.launch { userRepository.insertFavorite(favorite) }
    }

    fun removeFavorite(userId: Int, productId: Int){
        viewModelScope.launch { userRepository.removeFavorite(userId, productId) }
    }
}