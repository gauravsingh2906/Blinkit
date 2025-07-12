package com.android.example.blinkit.Models

import com.android.example.blinkit.roomDb.CartProducts

data class Orders(
    val orderId:String?=null,
    val orderList: List<CartProducts>?=null,
    val address:String?= null,
    val orderStatus:Int?=0,
    val orderDate:String?=null,
    val orderingUserId:String?=null
)
