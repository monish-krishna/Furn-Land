package com.practise.furn_land.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.furn_land.data.entities.Product
import com.practise.furn_land.data.entities.ProductDetail
import com.practise.furn_land.data.entities.ProductImage
import com.practise.furn_land.data.repository.FurnitureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val furnitureRepository: FurnitureRepository
) : ViewModel() {

    //:::: Members - LiveData ::::

    //Product - Individual
    private val _product = MutableLiveData<Product>()
    private val _productImages = MutableLiveData<List<ProductImage>>()
    private val _productDetails = MutableLiveData<List<ProductDetail>>()
    private val _productRatingDrawable = MutableLiveData<Int>()
    private val _productOffer = MutableLiveData<Int>()
    private val _isUserFavorite = MutableLiveData<Boolean>()

    //:::: Functions ::::

    init {
        _isUserFavorite.value = false
    }

    //Product Individual Access/Set Method
    fun setProduct(productId: Int) {
        viewModelScope.launch {
            _product.postValue(furnitureRepository.getProduct(productId))
            _productImages.postValue(furnitureRepository.getProductImages(productId))
            _productDetails.postValue(furnitureRepository.getProductDetails(productId))
            refreshRatingDrawable()
            updateProductOffer()
        }
    }

    private fun refreshRatingDrawable(){
        _product.value?.let { product ->
            val rating = product.totalRating
            if (rating != null && rating > 3.9){
                _productRatingDrawable.value = 1
            }else{
                _productRatingDrawable.value = 0
            }
        }
    }

    private fun updateProductOffer(){
        val currentPrice = _product.value?.currentPrice
        val originalPrice = _product.value?.originalPrice
        val offerPrice = (originalPrice!! - currentPrice!!) / originalPrice
        val offer = offerPrice * 100
        if (offer > 1) {
            _productOffer.value = offer.toInt()
        } else {
            _productOffer.value = 1
        }
    }

    //Product Individual Access/Get Methods
    fun getProduct(): LiveData<Product> = _product
    fun getProductImages(): LiveData<List<ProductImage>> = _productImages
    fun getProductDetails(): LiveData<List<ProductDetail>> = _productDetails
    fun getProductRatingBackground(): LiveData<Int> = _productRatingDrawable
    fun getProductOffer(): LiveData<Int> = _productOffer
    fun getProductId(): Int? = _product.value?.id
    fun getIsUserFavorite() = _isUserFavorite


    //Product Individual Access/Get Methods
    fun setIsFavorite(isFavorite: Boolean){
        _isUserFavorite.value = isFavorite
    }
}