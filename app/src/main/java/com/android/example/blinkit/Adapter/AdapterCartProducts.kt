package com.android.example.blinkit.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.android.example.blinkit.databinding.ItemViewCartProductBinding
import com.android.example.blinkit.databinding.ItemViewProductCategoryBinding
import com.android.example.blinkit.roomDb.CartProducts
import com.squareup.picasso.Picasso

class AdapterCartProducts:RecyclerView.Adapter<AdapterCartProducts.CartProductsViewHolder>() {


    inner class CartProductsViewHolder(val binding:ItemViewCartProductBinding):RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : ItemCallback<CartProducts>() {
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.productId==newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        val binding= ItemViewCartProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CartProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val product = differ.currentList[position]

        holder.binding.apply {
            Picasso.get().load(product.productImage).into(imageView4)
            tvProductTitle.text=product.productTitle
            tvProductQuantity.text=product.productQuantity
            tvProductPrice.text=product.productPrice
            tvProductCount.text=product.productCount.toString()
        }


    }


}