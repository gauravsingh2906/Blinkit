package com.android.example.blinkit

import android.util.Log
import android.widget.Toast
import com.android.example.blinkit.Adapter.AdapterProduct
import com.android.example.blinkit.Models.Product

import com.google.firebase.firestore.Filter
import com.google.gson.ReflectionAccessFilter.FilterResult
import java.util.Locale

class FilteringProducts(private val adapter: AdapterProduct, private val filter: ArrayList<Product>) : android.widget.Filter() {
    override fun performFiltering(constraint: CharSequence?):FilterResults {   // it means query
        val result = FilterResults()

        if (constraint!=null) {

            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")
            val filteredList = ArrayList<Product>()
            Log.d("Filter",filteredList.size.toString())
            for (products in filter) {
                Log.d("Gaurav","get hetfyhkiuivc")
                if (query.any {search ->
                                products.productTitle?.uppercase(Locale.getDefault())?.contains(search) == true ||
                                products.productCategory?.uppercase(Locale.getDefault())?.contains(search) == true ||
                                products.productPrice.toString().uppercase(Locale.getDefault()).contains(search) == true ||
                                products.productType?.uppercase(Locale.getDefault())?.contains(search) == true
                    }) {
                    filteredList.add(products)
                }
            }
            result.values = filteredList
            result.count = filteredList.size
        } else {
           result.apply {
               count=filter.size
               values=filter
           }
        }

        return result
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.differ.submitList(results?.values as ArrayList<Product>)
    }


}
