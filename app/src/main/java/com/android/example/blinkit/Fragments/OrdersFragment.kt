package com.android.example.blinkit.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.example.blinkit.Adapter.AdapterOrders
import com.android.example.blinkit.Models.OrderedItems
import com.android.example.blinkit.R
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.ViewModel.UserViewModel
import com.android.example.blinkit.databinding.FragmentOrdersBinding
import kotlinx.coroutines.launch


class OrdersFragment : Fragment() {

    private lateinit var binding:FragmentOrdersBinding
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapterOrders: AdapterOrders

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOrdersBinding.inflate(layoutInflater)

        getAllOrders()

        return binding.root
    }



    private fun onOrdersItemClicked(orderedItems: OrderedItems) {
        val bundle =Bundle()
        bundle.putString("orderId",orderedItems.orderId)
        bundle.putInt("status",orderedItems.itemStatus!!)
        findNavController().navigate(R.id.action_ordersFragment_to_orderDetailFragment,bundle)
    }

    private fun getAllOrders() {
        lifecycleScope.launch {
            viewModel.getAllOrders().collect{orderList->
                binding.shimmer.visibility=View.VISIBLE

                if(orderList.isNotEmpty()) {
                    val orderedList = ArrayList<OrderedItems>()
                    for (orders in orderList) {
                        var totalPrice = 0
                        var title = StringBuilder()

                        for (products in orders.orderList!!) {
                            val price = products.productPrice?.toInt()
                            val itemCount = products.productCount!!.toInt()

                            totalPrice += (price?.times(itemCount)!!)

                            title.append("${products.productCategory},")
                        }

                        val orderedItems = OrderedItems(orders.orderId,orders.orderDate,orders.orderStatus,title.toString(),totalPrice)
                        orderedList.add(orderedItems)
                    }

                    adapterOrders=AdapterOrders(requireContext(),::onOrdersItemClicked)
                    binding.rvOrders.adapter=adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                    binding.shimmer.visibility=View.GONE
                } else {
                    binding.shimmer.visibility=View.GONE
                    Utils.showToast(requireContext(),"You didn't do any order till now")
                }

        }
        }

    }


}