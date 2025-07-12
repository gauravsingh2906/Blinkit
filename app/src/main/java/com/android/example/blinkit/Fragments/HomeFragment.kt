package com.android.example.blinkit.Fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.example.blinkit.Adapter.AdapterBestSellers
import com.android.example.blinkit.Adapter.AdapterCategory
import com.android.example.blinkit.Adapter.AdapterProduct
import com.android.example.blinkit.CartListener
import com.android.example.blinkit.Models.BestSeller
import com.android.example.blinkit.Models.Category
import com.android.example.blinkit.Models.Product
import com.android.example.blinkit.R

import com.android.example.blinkit.Utils.Object
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.ViewModel.UserViewModel
import com.android.example.blinkit.databinding.BgSeeAllBinding
import com.android.example.blinkit.databinding.FragmentHomeBinding
import com.android.example.blinkit.databinding.ItemViewProductBinding
import com.android.example.blinkit.roomDb.CartProducts
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var adapter:AdapterBestSellers
    private lateinit var adapterProduct: AdapterProduct
    private  var cartListener: CartListener?=null
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()

        setAllCategories()
        navigatingToSearchFragment()
        fetchBestSellers()
        navigatingToProfilefragment()

        return binding.root
    }

    private fun onSeeAllButtonClicked(productType:BestSeller) {
        val bsSeeAllBinding:BgSeeAllBinding = BgSeeAllBinding.inflate(LayoutInflater.from(requireContext()))
        val bs = BottomSheetDialog(requireContext())
        bs.setContentView(bsSeeAllBinding.root)

        adapterProduct=AdapterProduct(::onAddButtonClicked,::onIncrementButtonClicked,::onDecrementButtonClicked)
        bsSeeAllBinding.rvProducts.adapter=adapterProduct
        adapterProduct.differ.submitList(productType.products)
        bs.show()

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

    private fun fetchBestSellers() {
        binding.shimmer.visibility=View.VISIBLE   //kisi ke baap ke bass ki nhi
        lifecycleScope.launch {
            viewModel.fetchProductTypes().collect{
                adapter=AdapterBestSellers(::onSeeAllButtonClicked)
                binding.rvBestseller.adapter=adapter
                adapter.differ.submitList(it)
                binding.shimmer.visibility=View.GONE
            }
        }
    }

    private fun navigatingToProfilefragment() {
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun navigatingToSearchFragment() {
        binding.searchEt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun onCategoryIconClicked(category: Category) {
        val bundle = Bundle()
        bundle.putString("category",category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment,bundle)
       //   getCtaegoryProduct(category.title)
    }



    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Object.allProductsCategory.size) {
            categoryList.add(Category(Object.allProductsCategory[i],Object.allProductCategoryIcon[i]))
        }

        binding.rvCategories.adapter=AdapterCategory(categoryList,::onCategoryIconClicked)

    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor=statusBarColors
            if(Build.VERSION.SDK_INT> Build.VERSION_CODES.M) {
                decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
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