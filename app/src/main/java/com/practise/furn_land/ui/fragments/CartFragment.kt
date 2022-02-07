package com.practise.furn_land.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.practise.furn_land.R
import com.practise.furn_land.data.entities.Cart
import com.practise.furn_land.data.entities.Product
import com.practise.furn_land.data.entities.relations.CartWithProductAndImages
import com.practise.furn_land.databinding.FragmentCartBinding
import com.practise.furn_land.databinding.LayoutLogInPromptBinding
import com.practise.furn_land.ui.activity.HomeActivity
import com.practise.furn_land.ui.adapters.CartItemAdapter
import com.practise.furn_land.utils.safeNavigate
import com.practise.furn_land.view_models.CartViewModel
import com.practise.furn_land.view_models.OrderViewModel
import com.practise.furn_land.view_models.UserViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CartFragment : Fragment() {
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }
    private val orderViewModel by lazy { ViewModelProvider(requireActivity())[OrderViewModel::class.java] }
    private var isUserLoggedIn = false
    private lateinit var bindingLogInPrompt: LayoutLogInPromptBinding
    private lateinit var bindingCart: FragmentCartBinding
    private lateinit var cartsWithProductAndImages: List<CartWithProductAndImages>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        isUserLoggedIn = userViewModel.isUserLoggedIn()
        return if (isUserLoggedIn) {
            bindingCart = FragmentCartBinding.inflate(layoutInflater)
            bindingCart.root
        } else {
            bindingLogInPrompt = LayoutLogInPromptBinding.inflate(layoutInflater)
            bindingLogInPrompt.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn) {
            setUpCart()
        } else {
            setUpLogInPrompt()
        }
        userViewModel.isUserAddressUpdated().observe(viewLifecycleOwner){
            if (it == false) return@observe
            orderViewModel.getOrderStatus().observe(viewLifecycleOwner) { orderStatus ->
                if (orderStatus == false) return@observe
                lifecycleScope.launch {
                    cartViewModel.clearCart(
                        userViewModel.getLoggedInUser().toInt()
                    )
                }
                findNavController().safeNavigate(
                    CartFragmentDirections.actionCartFragmentToOrderConfirmedDialogFragment(
                        orderViewModel.getOrderId()
                    )
                )
                orderViewModel.getOrderStatus().removeObservers(viewLifecycleOwner)
            }
            initiateCheckOut(cartsWithProductAndImages)
        }
    }

    private fun setUpCart() {
        cartViewModel.initCartItems(userViewModel.getLoggedInUser().toInt())
        bindingCart.rvCart.layoutManager = LinearLayoutManager(requireContext())
        setUpRecyclerView()
        setUpCheckOut()
    }

    private fun setUpCheckOut() {
        bindingCart.btnCheckOut.setOnClickListener {
            if (userViewModel.getUserHasAddress()) {
                orderViewModel.getOrderStatus().observe(viewLifecycleOwner) {
                    if (it == false) return@observe
                    lifecycleScope.launch {
                        cartViewModel.clearCart(
                            userViewModel.getLoggedInUser().toInt()
                        )
                    }
                    findNavController().safeNavigate(
                        CartFragmentDirections.actionCartFragmentToOrderConfirmedDialogFragment(
                            orderViewModel.getOrderId()
                        )
                    )
                    orderViewModel.getOrderStatus().removeObservers(viewLifecycleOwner)
                }
                cartsWithProductAndImages = cartViewModel.getCartItems().value as List<CartWithProductAndImages>
                initiateCheckOut(cartsWithProductAndImages)
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.adrress_required)
                    .setMessage(R.string.add_address_and_checkout)
                    .setIcon(R.drawable.ic_location)
                    .setPositiveButton(R.string.yes){ _,_ ->
                        cartsWithProductAndImages = cartViewModel.getCartItems().value as List<CartWithProductAndImages>
                        findNavController().navigate(CartFragmentDirections.actionCartFragmentToNewAddressDialogFragment())
                    }
                    .setNegativeButton(R.string.no){_,_ -> }
                    .setCancelable(false)
                    .create()
                    .show()
            }
        }
    }

    private fun initiateCheckOut(cartWithProductAndImages: List<CartWithProductAndImages>) {
        val cartItems = ArrayList<Cart>()
        cartWithProductAndImages.forEach {
            cartItems.add(it.cart)
        }
        Log.i("CartFragment", "cartItems - $cartItems")

        orderViewModel.addOrder(
            userViewModel.getLoggedInUser().toInt(),
            cartItems,
            cartViewModel.getTotalPrice()
        )
    }

    private fun setUpRecyclerView() {
        cartViewModel.getCartItems().observe(viewLifecycleOwner) { cartsWithProductAndImages ->
            if(cartsWithProductAndImages.isNotEmpty()) setUpTotalPrice(cartsWithProductAndImages)
            bindingCart.isListEmpty = cartsWithProductAndImages.isEmpty()
            bindingCart.rvCart.swapAdapter(
                CartItemAdapter(
                    cartsWithProductAndImages,
                    {
                        findNavController().navigate(
                            CartFragmentDirections.actionCartFragmentToProductFragment(
                                it.id
                            )
                        )
                    },
                    getOnClickQuantityListener()
                ),
                true
            )
        }
    }

    private fun setUpTotalPrice(cartsWithProductAndImages: List<CartWithProductAndImages>?) {
        var totalPrice = 0
        cartsWithProductAndImages?.forEach {
            val quantity = it.cart.quantity
            val productPrice = it.productWithImages.product.currentPrice.roundToInt()
            totalPrice+=(quantity*productPrice)
        }
        cartsWithProductAndImages?.let {
            val priceString = getString(R.string.rupee_symbol) + " $totalPrice"
            bindingCart.tvTotalPrice.text = priceString
        }
        cartViewModel.setTotalPrice(totalPrice)
    }

    private fun getOnClickQuantityListener(): CartItemAdapter.OnClickCartQuantity
        = object : CartItemAdapter.OnClickCartQuantity {
            override fun onClickPlus(product: Product, quantity: Int): Boolean {
                return if (quantity < 5 && product.stockCount > quantity) {
                    cartViewModel.updateQuantity(
                        userViewModel.getLoggedInUser().toInt(),
                        product.id,
                        quantity + 1
                    )
                    updateTotalPrice(product.currentPrice.roundToInt(), false)
                    true
                } else {
                    Snackbar.make(
                        bindingCart.layoutSnackBar,
                        "Quantity reached its upper limit",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("Okay") {}
                        .show()
                    false
                }
            }

            override fun onClickMinus(product: Product, quantity: Int): Boolean {
                return when {
                    quantity > 1 -> {
                        cartViewModel.updateQuantity(
                            userViewModel.getLoggedInUser().toInt(),
                            product.id,
                            quantity - 1
                        )
                        updateTotalPrice(product.currentPrice.roundToInt(), true)
                        true
                    }
                    quantity == 1 -> {
                        lifecycleScope.launch {
                            cartViewModel.removeCart(
                                userViewModel.getLoggedInUser().toInt(),
                                product.id
                            )
                        }
                        false
                    }
                    else -> false
                }
            }
        }

    private fun updateTotalPrice(price: Int, deduct: Boolean){
        val currentPrice = bindingCart.tvTotalPrice.text.toString().drop(2).toInt()
        if (deduct){
            val updatedPrice = currentPrice - price
            val updatedPriceString = getString(R.string.rupee_symbol)+ " $updatedPrice"
            bindingCart.tvTotalPrice.text = updatedPriceString
        }else{
            val updatedPrice = currentPrice + price
            cartViewModel.setTotalPrice(updatedPrice)
            val updatedPriceString = getString(R.string.rupee_symbol)+ " $updatedPrice"
            bindingCart.tvTotalPrice.text = updatedPriceString
        }
    }


    private fun setUpLogInPrompt() {
        bindingLogInPrompt.btnToLogin.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionCartFragmentToLogInFragment())
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_arrow_back)
    }


    override fun onDestroy() {
        cartViewModel.cleanUp()
        orderViewModel.cleanStatus()
        userViewModel.clearAddressAdded()
        super.onDestroy()
    }

    companion object{
        const val TAG = "OrderListIssue"
    }
}