package com.android.example.blinkit

interface CartListener {

    fun showCartLayout(itemCount : Int)

    fun savingCartItemCount(itemCount: Int)

    fun hideCartLayout()



}