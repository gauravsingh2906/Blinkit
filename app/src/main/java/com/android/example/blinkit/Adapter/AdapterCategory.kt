package com.android.example.blinkit.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.example.blinkit.Models.Category
import com.android.example.blinkit.databinding.ItemViewProductCategoryBinding
import com.squareup.picasso.Picasso

class AdapterCategory(val categoryList: ArrayList<Category>, val onCategoryIconClicked: (Category) -> Unit):RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding : ItemViewProductCategoryBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding=ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
       val data = categoryList[position]
        Picasso.get().load(data.image).into(holder.binding.imageView3)
        holder.binding.tvCategoryTitle.text=data.title

        holder.itemView.setOnClickListener {
            onCategoryIconClicked(data)  // so by here they will get the data of each position it means by this method they get the data
        }
    }


}