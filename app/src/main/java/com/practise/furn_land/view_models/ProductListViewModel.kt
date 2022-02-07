package com.practise.furn_land.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.furn_land.data.models.SortType
import com.practise.furn_land.data.models.SortType.*
import com.practise.furn_land.data.entities.Category
import com.practise.furn_land.data.entities.relations.ProductWithImages
import com.practise.furn_land.data.repository.FurnitureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val furnitureRepository: FurnitureRepository
): ViewModel() {


    private val _categoryIdList = MutableLiveData<List<Int>>() //LiveData of CategoryIds
    private val _productWithImagesList = MutableLiveData<List<ProductWithImages>>()
    private val _categoryList = MutableLiveData<List<Category>>()
    private val _searchStatus = MutableLiveData<Int>()
    private var _productListTitle: String? = null

    private var _sortType : SortType? = null

    init {
        setCategories()
        _searchStatus.value = SEARCH_NOT_INITIATED
    }



    //Product List Access/Get Method
    fun getProductWithImagesList(): LiveData<List<ProductWithImages>> = _productWithImagesList

    //::::Product List Access/Set Methods::::
    fun setProductWithImagesList(categoryIds: List<Int>) {
        viewModelScope.launch { _productWithImagesList.postValue(furnitureRepository.getProductWithImages(categoryIds)) }
    }

    fun setProductWithImagesList(categoryId: Int) {
        viewModelScope.launch { _productWithImagesList.postValue(furnitureRepository.getProductWithImages(categoryId)) }
    }

    fun setProductWithImages() {
        viewModelScope.launch { _productWithImagesList.postValue(furnitureRepository.getProductWithImages()) }
    }

    fun setProductWithImages(list: List<ProductWithImages>) {
        _productWithImagesList.value = list
        setSearchStatus(SEARCH_COMPLETED)
    }

    //::::Product Categories Access Methods::::
    fun setCategoryIds(categoryIds: List<Int>){
        if (categoryIds.isNullOrEmpty()) return
        _categoryIdList.value = categoryIds
    }

    fun setCategories() {
        viewModelScope.launch { _categoryList.postValue(furnitureRepository.getCategories()) }
    }

    fun getCategories(): LiveData<List<Category>> = _categoryList

    fun setProductsWithImagesWithQuery(searchQuery: String) = viewModelScope.launch {
        _productListTitle = searchQuery
        setSearchStatus(SEARCH_INITIATED)
        val searchList = getQueryAsList(searchQuery)
        furnitureRepository.getProductWithImagesWithQuery(searchList).also{
            setProductWithImages(it)
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

    fun setSearchStatus(status: Int) {
        _searchStatus.value = status
    }

    fun getSearchStatus(): LiveData<Int> = _searchStatus

    fun emptyProductList(){
        _productWithImagesList.value = emptyList()
    }
    
    fun sortProductList(sortType: SortType){
        when(sortType){
            PRICE_LOW_TO_HIGH -> {
                _productWithImagesList.value = _productWithImagesList.value?.sortedBy {
                    it.product.currentPrice
                }
                _sortType = PRICE_LOW_TO_HIGH
            }
            PRICE_HIGH_TO_LOW -> {
                _productWithImagesList.value = _productWithImagesList.value?.sortedByDescending {
                    it.product.currentPrice
                }
                _sortType = PRICE_HIGH_TO_LOW
            }
            RATING_LOW_TO_HIGH -> {
                _productWithImagesList.value = _productWithImagesList.value?.sortedBy {
                    it.product.totalRating
                }
                _sortType = RATING_LOW_TO_HIGH
            }
            RATING_HIGH_TO_LOW -> {
                _productWithImagesList.value = _productWithImagesList.value?.sortedByDescending {
                    it.product.totalRating
                }
                _sortType = RATING_HIGH_TO_LOW
            }
        }
        Log.i("ProdBottomSheetSort","sortType = $sortType")
    }

    fun getSortType(): SortType? {
        return _sortType
    }

    fun clearSortType(){
        _sortType = null
    }

    fun getProductListTitle(): String{
        return _productListTitle.toString()
    }

    fun setProductListTitle(title: String) {
        _productListTitle = title
    }

    companion object{
        const val SEARCH_COMPLETED = 1
        const val SEARCH_INITIATED = 0
        const val SEARCH_NOT_INITIATED = -1
    }
}