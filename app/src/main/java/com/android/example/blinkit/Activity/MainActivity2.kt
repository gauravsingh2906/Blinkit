package com.android.example.blinkit.Activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.example.blinkit.Adapter.AdapterCartProducts
import com.android.example.blinkit.CartListener
import com.android.example.blinkit.ViewModel.UserViewModel
import com.android.example.blinkit.databinding.ActivityMain2Binding
import com.android.example.blinkit.databinding.BsCartProductsBinding
import com.android.example.blinkit.roomDb.CartProducts
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity2 : AppCompatActivity(), CartListener {
    private lateinit var binding: ActivityMain2Binding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var cartProductsList: List<CartProducts>
    private lateinit var adapterCartProducts: AdapterCartProducts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        getTotalItemCountInCart()
        onCartClicked()
        getAllCartProducts()
        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
        binding.btnNexts.setOnClickListener {
            startActivity(Intent(this,OrderPlacedActivity::class.java))
            finish()
        }
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this) {
            //    for (i in it) {
            cartProductsList = it
//                Log.d("vvv",i.productTitle.toString())
//                Log.d("vvv",i.productCount.toString())

        }
    }

    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener {
            val bsCartProductsBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))
            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductsBinding.root)

            bsCartProductsBinding.tvNoOfProductCount.text = binding.tvNoOfProductCount.text
            adapterCartProducts = AdapterCartProducts()
            bsCartProductsBinding.rvProductItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductsList)
            bsCartProductsBinding.llItemCart.setOnClickListener {
                bs.dismiss()
            }
            bsCartProductsBinding.btnNexts.setOnClickListener {
                startActivity(Intent(this,OrderPlacedActivity::class.java))
                finish()
            }
            bs.show()
        }
    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this) {
            if (it > 0) {
                binding.llCart.visibility = View.VISIBLE
                binding.tvNoOfProductCount.text = it.toString()
            } else {
                binding.llCart.visibility = View.GONE
            }
        }
    }


    override fun showCartLayout(itemCount: Int) {
        val previousCount = binding.tvNoOfProductCount.text.toString().toInt()
        val updateCount = previousCount + itemCount

        if (updateCount > 0) {
            binding.llCart.visibility = View.VISIBLE
            binding.tvNoOfProductCount.text = updateCount.toString()

        } else {
            binding.llCart.visibility = View.GONE
            binding.tvNoOfProductCount.text = "0"
        }


    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this) {
            viewModel.savingCartItemCount(it + itemCount)
        }

        //  val previousCount = binding.tvNoOfProductCount.text.toString().toInt()

    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNoOfProductCount.text = "0"
    }


}