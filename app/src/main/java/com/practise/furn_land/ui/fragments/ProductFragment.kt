package com.practise.furn_land.ui.fragments

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.Favorite
import com.practise.furn_land.data.entities.ProductDetail
import com.practise.furn_land.data.entities.ProductImage
import com.practise.furn_land.databinding.FragmentProductBinding
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.ui.adapters.ProductDetailAdapter
import com.practise.furn_land.utils.isStrikeThrough
import com.practise.furn_land.view_models.CartViewModel
import com.practise.furn_land.view_models.FavoriteViewModel
import com.practise.furn_land.view_models.ProductViewModel
import com.practise.furn_land.view_models.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProductFragment : Fragment() {

    private val navArgs by navArgs<ProductFragmentArgs>()
    private lateinit var binding: FragmentProductBinding
    private val productViewModel by lazy { ViewModelProvider(this)[ProductViewModel::class.java] }
    private val favoriteViewModel by lazy { ViewModelProvider(this)[FavoriteViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialise data binder
        setHasOptionsMenu(true)
        binding = FragmentProductBinding.inflate(layoutInflater)
        Log.i(TAG, "onCreate() called - productListenerSetTimes : $productListenerSetTimes")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = productViewModel
        productViewModel.getProductImages().observe(viewLifecycleOwner){ imageList ->
            setUpImageList(imageList)
        }
        productViewModel.getProductDetails().observe(viewLifecycleOwner){ detailsList ->
            detailsList?.let { setUpDetailsAdapter(detailsList) }
        }
        productViewModel.getProductRatingBackground().observe(viewLifecycleOwner){
            setRatingDrawable()
        }
        binding.more.visibility = View.GONE
        binding.roundToInt = {input ->  input.roundToInt() }
        binding.detailsView.setOnClickListener(getDetailsClickListener())
        productViewModel.getProduct().observe(viewLifecycleOwner) { product ->
            product?.let {
                checkProductIsInStock(it.stockCount)
                checkIsUserFavorite(it.id)
                cartViewModel.checkProductInUserCart(it.id, userViewModel.getLoggedInUser().toInt())
            }
        }
        cartViewModel.getAddToCartStatus().observe(viewLifecycleOwner){
            if (it==1) Toast.makeText(requireContext(),"Added to cart",Toast.LENGTH_SHORT).show()
        }
        cartViewModel.isProductInUserCart().observe(viewLifecycleOwner){
            it?.let { setUpAddToCartButton(it) }
        }
        setUpAddToCartListener()
        Log.i(TAG,"onViewCreated called()")
    }

    private fun checkProductIsInStock(stockCount: Int) {
        if (stockCount==0) {
            binding.buttonCard.visibility = View.GONE
        }
    }

    private fun setUpAddToCartButton(status: Boolean) {
        if (status){
            with(binding.btnAddToCart){
                icon = ResourcesCompat.getDrawable(resources,R.drawable.ic_shopping_cart_filled,null)
                text = getString(R.string.view_cart)
            }
        }else{
            with(binding.btnAddToCart){
                icon = ResourcesCompat.getDrawable(resources,R.drawable.ic_add_to_cart,null)
                text = getString(R.string.add_to_cart)
            }
        }
    }

    private fun setUpAddToCartListener() {
        binding.btnAddToCart.setOnClickListener {
            if (userViewModel.isUserLoggedIn()) {
                addProductToCart()
            } else {
                findNavController().navigate(ProductFragmentDirections.actionProductFragmentToLogInDialogFragment())
            }
        }
    }

    private fun addProductToCart() {
        if (cartViewModel.getIsProductInUserCart()){
            findNavController().navigate(ProductFragmentDirections.actionProductFragmentToCartFragment())
        }else{
            productViewModel.getProductId()?.let { cartViewModel.insertIntoCart(userViewModel.getLoggedInUser().toInt(), it) }
        }
    }

    private fun checkIsUserFavorite(productId: Int) {
        lifecycleScope.launch {
            val isUserFavorite = favoriteViewModel.isUserFavorite(productId,userViewModel.getLoggedInUser().toInt())
            productViewModel.setIsFavorite(isUserFavorite).also {
                setUpFavoriteCheckedListener()
            }
        }
    }

    private fun setUpFavoriteCheckedListener() {
        binding.favoriteButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (userViewModel.isUserLoggedIn()) {
                editUserFavorite(isChecked)
            } else {
                buttonView.isChecked = false
                findNavController().navigate(ProductFragmentDirections.actionProductFragmentToLogInDialogFragment())
            }
        }
    }

    private fun editUserFavorite(checked: Boolean) {
        val productId = productViewModel.getProductId()
        val userId = userViewModel.getLoggedInUser().toInt()
        if (productId == null){
            Log.e(TAG,"Unexpected value : productId can't be null")
            return
        }
        productId.let {
            val favorite = Favorite(userId, it)
            if (checked){
                favoriteViewModel.insertFavorite(favorite)
            }else{
                favoriteViewModel.removeFavorite(userId, it)
            }
            productViewModel.setIsFavorite(checked)
        }
    }

    private fun getDetailsClickListener(): View.OnClickListener = View.OnClickListener {
        if (binding.rvDetailsList.visibility == View.VISIBLE){
            TransitionManager.beginDelayedTransition(binding.rvDetailsList,AutoTransition())
            binding.rvDetailsList.visibility = View.GONE
            binding.moreArrow.setImageResource(R.drawable.ic_expand_more)
        }else{
            TransitionManager.beginDelayedTransition(binding.rvDetailsList,AutoTransition())
            binding.rvDetailsList.visibility = View.VISIBLE
            binding.moreArrow.setImageResource(R.drawable.ic_expand_less)
        }
    }

    private fun setUpDetailsAdapter(detailsList: List<ProductDetail>) {
        val adapter = ProductDetailAdapter(detailsList)
        binding.rvDetailsList.adapter = adapter
        binding.rvDetailsList.layoutManager = LinearLayoutManager(activity)
    }

    override fun onResume() {
        super.onResume()
        productViewModel.setProduct(navArgs.productId)
        //product name expansion listener condition

        setUpListenerProductTv()
        binding.tvOriginalPrice.isStrikeThrough(true)
        Log.i(TAG,"onResume called()")
    }

    override fun onPause() {
        super.onPause()
        cartViewModel.cleanUp()
    }

    private fun setUpListenerProductTv(){
        val productTv = binding.tvProductName
        val textViewTreeObserver = productTv.viewTreeObserver
        textViewTreeObserver.addOnGlobalLayoutListener ( object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                Log.i(TAG,"onGlobal Layout")
                val tvLayout = productTv.layout
                if(tvLayout != null){
                    productListenerSetTimes+=1
                    val lines = tvLayout.lineCount
                    val ellipsisCount = tvLayout.getEllipsisCount(lines-1)
                    Log.i(TAG,ellipsisCount.toString())
                    var conditionPass = false
                    if (lines > 0 && (tvLayout.getEllipsisCount(lines-1) > 0) && productListenerSetTimes < 10) {
                        binding.more.visibility = View.VISIBLE
                        binding.productNameLayout.setOnClickListener(getProductNameOnClickListener())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            binding.productNameLayout.foreground = ResourcesCompat.getDrawable(productTv.resources,R.drawable.ripple_bg,null)
                        }
                        conditionPass = true
                        Log.i(TAG,"in if block- productListenerTimes = $productListenerSetTimes")
                    }else if(lines > 0 && (tvLayout.getEllipsisCount(lines-1) < 1) && productListenerSetTimes > 10){
                        conditionPass = true
                        Log.i(TAG,"in else if block - productListenerTimes = $productListenerSetTimes")
                    }
                    Log.i(TAG,"conditionPass $conditionPass")
                    if (conditionPass || productListenerSetTimes > 20) {
                        textViewTreeObserver.removeOnGlobalLayoutListener(this)
                        Log.i(TAG,"conditionPass - remover of listener")
                    }
                }
            }
        })
    }

    private fun setUpImageList(list: List<ProductImage>) {
        val array = Array(list.size) { "" }
        val imageList = mutableListOf<SlideModel>()
        list.forEach { productImage ->
            array[productImage.productImageIndex-1] = productImage.productImageUrl
        }
        array.forEachIndexed { index, url ->
            imageList.add(
                index,
                SlideModel(url, ScaleTypes.CENTER_INSIDE)
            )
            binding.imageSlider.setImageList(imageList)
        }
    }

    private fun getProductNameOnClickListener() = View.OnClickListener {
        val productName = binding.tvProductName
        val moreTv = binding.more
        if (productName.maxLines==3){
            productName.maxLines = Int.MAX_VALUE
            productName.ellipsize = null
            moreTv.text = getString(R.string.less)
        }else if (productName.maxLines == Int.MAX_VALUE){
            productName.maxLines = 3
            productName.ellipsize = TextUtils.TruncateAt.END
            moreTv.text = getString(R.string.more)
        }
    }

    private fun setRatingDrawable(){
        var rating = 0F
        try{
            rating = binding.tvRatingIcon.text.toString().toFloat()
        }catch (exc : NumberFormatException){
            Log.i(TAG,"Rating not a number")
        }
        when {
            rating <= 0F -> {
                binding.tvRatingIcon.visibility = View.GONE
                binding.tvNoRating.visibility = View.VISIBLE
            }
            rating > 3.9F -> {
                binding.tvRatingIcon.background = ResourcesCompat.getDrawable(resources,R.drawable.rating_backgroud_green,null)
            }
            rating > 0 -> {
                binding.tvRatingIcon.background = ResourcesCompat.getDrawable(resources,R.drawable.rating_backgroud_orange,null)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fav_cart_menu,menu)
        true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favoriteFragment -> {
                findNavController().navigate(ProductFragmentDirections.actionProductFragmentToFavoriteFragment())
                true
            }
            R.id.cartFragment -> {
                findNavController().navigate(ProductFragmentDirections.actionProductFragmentToCartFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }

    override fun onDestroy() {
        super.onDestroy()
        productListenerSetTimes = 0
        cartViewModel.cleanUp()
        Log.i(TAG, "onDestroy() called - productListenerSetTimes : $productListenerSetTimes")
    }

    companion object{
        private var productListenerSetTimes = 0
        private const val TAG = "ProductFragment"
    }
}