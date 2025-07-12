package com.android.example.blinkit.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.example.blinkit.FilteringProducts
import com.android.example.blinkit.Models.Product
import com.android.example.blinkit.Utils.Utils
import com.android.example.blinkit.databinding.ItemViewProductBinding
import com.denzcoskun.imageslider.models.SlideModel

class AdapterProduct(
    val onAddButtonClicked: (Product, ItemViewProductBinding) -> Unit,
   val onIncrementButtonClicked: (Product, ItemViewProductBinding) -> Unit,
  val onDecrementButtonClicked: (Product, ItemViewProductBinding) -> Unit
) :RecyclerView.Adapter<AdapterProduct.CategoriesViewHolder>(),Filterable{

    inner class CategoriesViewHolder(val binding: ItemViewProductBinding): RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId==newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
           return oldItem==newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil) // ye jo class iske object me hume functuin milta hai submit list ka=

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val binding = ItemViewProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoriesViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
       val product = differ.currentList[position]

        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()

            val productImage = product.productImageUris

            for (i in 0 until productImage?.size!!) {
                imageList.add(SlideModel(product.productImageUris!!.get(i).toString()))
            }
            imageSlider.setImageList(imageList)
            tvProductTitle.text=product.productTitle
            val total = product.productQuantity.toString() + product.productUnit
            tvProductQuantity.text = total
            tvProductPrice.text = product.productPrice.toString()

            if(product.itemCount!!>0) {
                tvProductCount.text=product.itemCount.toString()
                tvAdd.visibility=View.GONE
                llProductCount.visibility=View.VISIBLE
            }

            tvAdd.setOnClickListener {
                onAddButtonClicked(product,this)
            }

            tvIncrementCount.setOnClickListener {
                onIncrementButtonClicked(product,this)
            }
            tvDecrementCount.setOnClickListener {
                onDecrementButtonClicked(product,this)
            }


        }
//        holder.itemView.setOnClickListener {
//            onEditButtonClicked(product)
//        }

    }

   private val filter : FilteringProducts?=null
    var originalList = ArrayList<Product>()

    override fun getFilter(): Filter {
        if(filter==null) {

            return FilteringProducts(this,originalList)
        }
        return filter

    }


}