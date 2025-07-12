package com.android.example.blinkit.Fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.example.blinkit.Adapter.AdapterProduct
import com.android.example.blinkit.CartListener
import com.android.example.blinkit.Models.Product
import com.android.example.blinkit.R
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.ViewModel.UserViewModel
import com.android.example.blinkit.databinding.FragmentCategoryBinding
import com.android.example.blinkit.databinding.ItemViewProductBinding
import com.android.example.blinkit.roomDb.CartProducts
import kotlinx.coroutines.launch


class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private var category: String? = null
    private var cartListener: CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        getProductCategory()
        setToolbarTitle()
        fetchCategoryProduct()
        onSearchMenuClick()
        setStatusBarColor()


        return binding.root
    }

    private fun onSearchMenuClick() {
        binding.toolbar3.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.searchToolbar -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun fetchCategoryProduct() {
        binding.shimmer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getCategoryProduct(category).collect {

                if (it.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                adapterProduct = AdapterProduct(
                    ::onAddButtonClicked,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                )
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
//                adapterProduct.originalList=it as ArrayList<Product>

                binding.shimmer.visibility = View.GONE


            }
        }
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun setToolbarTitle() {
        binding.toolbar3.title = category
    }

    private fun getProductCategory() {
        val bundle = arguments
        category = bundle!!.getString("category")

    }

    private fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding) {

        //Step1:- update the count in every fragment
        var itemCountIncr = productBinding.tvProductCount.text.toString().toInt()
        itemCountIncr++

        if (product.productStock!!+1>itemCountIncr) {

            productBinding.tvProductCount.text = itemCountIncr.toString()

            cartListener?.showCartLayout(1)

            //Step2
            product.itemCount = itemCountIncr
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                viewModel.updateItemCount(product,itemCountIncr)
            }
        } else{
            Utils.showToast(requireContext(),"You cannot add more item")
        }



    }

    private fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding) {
        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--

        //Step2
        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product,itemCountDec)
        }

        if (itemCountDec > 0) {
            productBinding.tvProductCount.text = itemCountDec.toString()
        } else {
            lifecycleScope.launch { viewModel.deleteCartProduct(product.productRandomId) }
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.tvProductCount.text = "0"
        }



        cartListener?.showCartLayout(-1)


    }

    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding) {
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE


        //Step1:- update the count in every fragment
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()
        Utils.showToast(requireContext(), "${itemCount}")

        cartListener?.showCartLayout(1)



        //Step2
        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product,itemCount)
        }


    }

    private fun saveProductInRoomDb(product: Product) {

        val cartProduct = CartProducts(
            productId = product.productRandomId,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = product.productPrice.toString(),
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType
        )
        lifecycleScope.launch {
            viewModel.insertCartProducts(cartProduct)
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is CartListener) {
            cartListener = context
        } else {
            throw ClassCastException("Please implement CartListener")
        }


    }


}