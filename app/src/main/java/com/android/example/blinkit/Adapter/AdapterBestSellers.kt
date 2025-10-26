package com.android.example.blinkit.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.example.blinkit.Models.BestSeller
import com.android.example.blinkit.databinding.ItemViewBestSellerBinding
import com.squareup.picasso.Picasso


class AdapterBestSellers(val onSeeAllButtonClicked: (BestSeller) -> Unit) :RecyclerView.Adapter<AdapterBestSellers.BestsellerViewHolder>() {


    inner class BestsellerViewHolder(val binding:ItemViewBestSellerBinding):RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<BestSeller>() {
        override fun areItemsTheSame(oldItem: BestSeller, newItem: BestSeller): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: BestSeller, newItem: BestSeller): Boolean {
           return oldItem==newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        val binding=ItemViewBestSellerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BestsellerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        val producttype = differ.currentList[position]

        holder.binding.apply {
            tvProductType.text=producttype.productType
            tvTotalProducts.text=producttype.products?.size.toString() + " products"

            val listofIv = listOf(ivProduct1,ivProduct2,ivProduct3)

            val minSize = minOf(listofIv.size,producttype.products!!.size)

            Log.d("min",{minSize}.toString())

//            for(i in 0 until minSize) {
//                listofIv[i].visibility= View.VISIBLE
//                Picasso.get().load(producttype.products[i].productImageUris?.get(i)).into(listofIv[i])
//                //Glide.with(holder.itemView).load(producttype.products[i].productImageUris?.get(0)).into(listofIv[i])
//            }

            if(producttype.products?.size!!>3) {
                tvProductCount.visibility=View.VISIBLE
                tvProductCount.text="+" + producttype.products?.size!!.minus(3).toString()
            }


        }

        holder.itemView.setOnClickListener {
            onSeeAllButtonClicked(producttype)
        }


    }

}