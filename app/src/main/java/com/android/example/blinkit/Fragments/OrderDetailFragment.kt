package com.android.example.blinkit.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.example.blinkit.Adapter.AdapterCartProducts
import com.android.example.blinkit.R
import com.android.example.blinkit.ViewModel.UserViewModel
import com.android.example.blinkit.databinding.FragmentOrderDetailBinding
import kotlinx.coroutines.launch


class OrderDetailFragment : Fragment() {
    private lateinit var binding:FragmentOrderDetailBinding
    private lateinit var adapterCartProducts: AdapterCartProducts
    private val viewModel: UserViewModel by viewModels()
    private var status:Int=0
    private var orderId:String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOrderDetailBinding.inflate(layoutInflater)

        getValues()
        settingStatus()
        lifecycleScope.launch {
            getOrderedProducts()
        }



        return binding.root
    }

    suspend fun getOrderedProducts() {
       viewModel.getOrderedProducts(orderId).collect{
           adapterCartProducts=AdapterCartProducts()
           binding.rvProductItems.adapter=adapterCartProducts
           adapterCartProducts.differ.submitList(it)
       }
    }

    private fun settingStatus() {
        val statusToviews = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1,binding.iv2,binding.view1),
            2 to listOf(binding.iv1,binding.iv2,binding.view1,binding.iv3,binding.view3),
            3 to listOf(binding.iv1,binding.iv2,binding.view1,binding.iv3,binding.view3,binding.iv4,binding.view4),
        )
        val viewsToTint = statusToviews.getOrDefault(status, emptyList())

        for (view in viewsToTint) {
            view.backgroundTintList=ContextCompat.getColorStateList(requireContext(), R.color.blue)
        }

    }

    private fun getValues() {
        val bundle=arguments
       status = bundle?.getInt("status")!!
        orderId=bundle.getString("orderId")!!


    }


}