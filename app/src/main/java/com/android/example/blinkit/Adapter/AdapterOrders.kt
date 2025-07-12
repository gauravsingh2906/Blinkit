package com.android.example.blinkit.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.example.blinkit.Models.OrderedItems
import com.android.example.blinkit.R
import com.android.example.blinkit.databinding.ItemViewOrdersBinding

class AdapterOrders(val context: Context,val onOrdersItemClicked: (OrderedItems) -> Unit) :RecyclerView.Adapter<AdapterOrders.OrderViewHolder>() {


    inner class OrderViewHolder(val binding:ItemViewOrdersBinding):RecyclerView.ViewHolder(binding.root)


    val diffUtil = object: DiffUtil.ItemCallback<OrderedItems>() {
        override fun areItemsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
          return oldItem.orderId==newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
           return oldItem==newItem
        }

    }
    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemViewOrdersBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.binding.apply {
           tvOrderTitles.text=order.itemTitle
            tvOrderDate.text=order.itemDate
            tvOrderAmount.text="₹${order.itemPrice.toString()}"
            when(order.itemStatus) {

                0->{
                    tvOrderStatus.text="Ordered"
                    tvOrderStatus.backgroundTintList=ContextCompat.getColorStateList(holder.itemView.context,R.color.yellow)
                }
                1->{
                    tvOrderStatus.text="Received"
                    tvOrderStatus.backgroundTintList=ContextCompat.getColorStateList(holder.itemView.context,R.color.blue)
                }
                2->{
                    tvOrderStatus.text="Dispatched"
                    tvOrderStatus.backgroundTintList=ContextCompat.getColorStateList(holder.itemView.context,R.color.green)
                }
                3->{
                    tvOrderStatus.text="Delivered"
                    tvOrderStatus.backgroundTintList=ContextCompat.getColorStateList(holder.itemView.context,R.color.orange)
                }
            }
        }

        holder.itemView.setOnClickListener {
            onOrdersItemClicked(order)
        }
    }


}